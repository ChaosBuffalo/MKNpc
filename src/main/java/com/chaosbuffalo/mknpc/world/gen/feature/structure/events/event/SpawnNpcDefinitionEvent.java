package com.chaosbuffalo.mknpc.world.gen.feature.structure.events.event;

import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.StringAttribute;
import com.chaosbuffalo.mkcore.utils.WorldUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.WorldStructureManager;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.StructureEvent;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.conditions.NotableDeadCondition;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.requirements.StructureHasNotableRequirement;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.requirements.StructureHasPoiRequirement;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public class SpawnNpcDefinitionEvent extends StructureEvent {
    public final static ResourceLocation TYPE_NAME = new ResourceLocation(MKNpc.MODID,
            "struct_event.spawn_npc");
    protected ResourceLocationAttribute npcDefinition = new ResourceLocationAttribute(
            "npcDefinition", NpcDefinitionManager.INVALID_NPC_DEF);
    protected StringAttribute poiTag = new StringAttribute("poiTag", "invalid");
    protected StringAttribute faceTag = new StringAttribute("faceTag", "invalid");
    protected MKEntity.NonCombatMoveType moveType;


    public SpawnNpcDefinitionEvent() {
        super(TYPE_NAME);
        addAttributes(npcDefinition, poiTag, faceTag);
    }

    public SpawnNpcDefinitionEvent(ResourceLocation npcDef, String spawnLocation, String faceTagIn,
                                   MKEntity.NonCombatMoveType moveType) {
        this();
        npcDefinition.setValue(npcDef);
        poiTag.setValue(spawnLocation);
        faceTag.setValue(faceTagIn);
        this.moveType = moveType;
        addRequirement(new StructureHasPoiRequirement(spawnLocation));
        addRequirement(new StructureHasPoiRequirement(faceTagIn));
    }

    public SpawnNpcDefinitionEvent addNotableDeadCondition(ResourceLocation notable, boolean killAll) {
        addRequirement(new StructureHasNotableRequirement(notable));
        addCondition(new NotableDeadCondition(notable, killAll));
        return this;
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        moveType = MKEntity.NonCombatMoveType.values()[dynamic.get("moveType").asInt(0)];
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("moveType"), ops.createInt(moveType.ordinal()));
    }

    @Override
    public void execute(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, World world) {
        NpcDefinition def = NpcDefinitionManager.getDefinition(npcDefinition.getValue());
        if (def != null) {
            entry.getFirstPoiWithTag(poiTag.getValue()).ifPresent(x -> {
                UUID npcId = entry.getCustomData().computeUUID(getEventName());
                Vector3d pos = Vector3d.copyCenteredHorizontally(x.getLocation().getPos());
                double difficultyValue = WorldUtils.getDifficultyForGlobalPos(x.getLocation());
                Entity entity = def.createEntity(world, pos, npcId, difficultyValue);
                if (entity != null){
                    entry.getFirstPoiWithTag(faceTag.getValue()).ifPresent( face -> {
                        entity.lookAt(EntityAnchorArgument.Type.EYES, Vector3d.copyCentered(face.getLocation().getPos()));
                    });
                    world.addEntity(entity);
                    final double finDiff = difficultyValue;
                    MKNpc.getNpcData(entity).ifPresent((cap) -> {
                        cap.setMKSpawned(true);
                        cap.setSpawnPos(new BlockPos(pos).up());
                        cap.setNotableUUID(npcId);
                        cap.setStructureId(entry.getStructureId());
                        cap.setDifficultyValue(finDiff);
                    });
                    if (entity instanceof MKEntity){
                        ((MKEntity) entity).setNonCombatMoveType(moveType);
                    }
                    if (entity instanceof MobEntity && world instanceof IServerWorld){
                        ((MobEntity) entity).onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(
                                entity.getPosition()), SpawnReason.SPAWNER, null, null);
                    }

                }

            });
        }

    }
}
