package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkchat.entity.DialogueComponent;
import com.chaosbuffalo.mkchat.entity.IPlayerChatReceiver;
import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityTargetingDecision;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.chaosbuffalo.mknpc.entity.ai.memory.ThreatMapEntry;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.FollowMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.KiteMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.MovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.StationaryMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.sensor.MKSensorTypes;
import com.chaosbuffalo.targeting_api.Targeting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("EntityConstructor")
public abstract class MKEntity extends CreatureEntity implements IPlayerChatReceiver {
    private int castAnimTimer;
    private VisualCastState visualCastState;
    private MKAbility castingAbility;
    private final DialogueComponent dialogueComponent;


    public enum VisualCastState {
        NONE,
        CASTING,
        RELEASE,
    }


    protected MKEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        castAnimTimer = 0;
        visualCastState = VisualCastState.NONE;
        castingAbility = null;
        this.dialogueComponent = createDialogueComponent();
        getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent((mkEntityData -> {
            mkEntityData.getAbilityExecutor().setStartCastCallback(this::startCast);
            mkEntityData.getAbilityExecutor().setCompleteAbilityCallback(this::endCast);
        }));
    }

    public MovementStrategy getMovementStrategy(AbilityTargetingDecision decision){
        MKAbility ability = decision.getAbility();
        if (ability == null){
            return StationaryMovementStrategy.STATIONARY_MOVEMENT_STRATEGY;
        }
        switch (decision.getMovementSuggestion()){
            case KITE:
                return new KiteMovementStrategy(ability.getDistance() * .5);
            case FOLLOW:
                return new FollowMovementStrategy(1.0f, Math.round(ability.getDistance()));
            case STATIONARY:
            default:
                return StationaryMovementStrategy.STATIONARY_MOVEMENT_STRATEGY;
        }
    }

    protected DialogueComponent createDialogueComponent(){
        return new DialogueComponent(this);
    }

    public DialogueComponent getDialogueComponent() {
        return dialogueComponent;
    }

    public void addThreat(LivingEntity entity, int value) {
        Optional<Map<LivingEntity, ThreatMapEntry>> threatMap = this.brain.getMemory(MKMemoryModuleTypes.THREAT_MAP);
        Map<LivingEntity, ThreatMapEntry> newMap = threatMap.orElse(new HashMap<>());
        newMap.put(entity, newMap.getOrDefault(entity, new ThreatMapEntry()).addThreat(value));
        this.brain.setMemory(MKMemoryModuleTypes.THREAT_MAP, newMap);
    }

    protected void updateEntityCastState() {
        if (castAnimTimer > 0) {
            castAnimTimer--;
            if (castAnimTimer == 0) {
                castingAbility = null;
                visualCastState = VisualCastState.NONE;
            }
        }
    }

    @Override
    public void livingTick() {
        updateArmSwingProgress();
        updateEntityCastState();
        super.livingTick();
    }

    public VisualCastState getVisualCastState() {
        return visualCastState;
    }

    public int getCastAnimTimer() {
        return castAnimTimer;
    }

    public MKAbility getCastingAbility() {
        return castingAbility;
    }

    public void startCast(MKAbility ability) {
        visualCastState = VisualCastState.CASTING;
        castingAbility = ability;
    }

    public void endCast(MKAbility ability) {
        castingAbility = ability;
        visualCastState = VisualCastState.RELEASE;
        castAnimTimer = 15;
    }

    public abstract void enterDefaultMovementState(LivingEntity target);

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    public void reduceThreat(LivingEntity entity, int value) {
        Optional<Map<LivingEntity, ThreatMapEntry>> threatMap = this.brain.getMemory(MKMemoryModuleTypes.THREAT_MAP);
        Map<LivingEntity, ThreatMapEntry> newMap = threatMap.orElse(new HashMap<>());
        newMap.put(entity, newMap.getOrDefault(entity, new ThreatMapEntry()).subtractThreat(value));
        this.brain.setMemory(MKMemoryModuleTypes.THREAT_MAP, newMap);
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.world.getProfiler().startSection("brain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().endSection();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof LivingEntity) {
            addThreat((LivingEntity) source.getTrueSource(), Math.round(amount));
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if (hand.equals(Hand.MAIN_HAND) && getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                .map((cap) -> cap.getRelationToMob(player) != Targeting.TargetRelation.ENEMY).orElse(false)){
            if (!player.world.isRemote()){
                dialogueComponent.startDialogue((ServerPlayerEntity) player);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void receiveMessage(ServerPlayerEntity player, String message) {
        MKNpc.LOGGER.info("Received chat message from {}, {}", player, message);
        dialogueComponent.receiveMessageFromPlayer(player, message);
    }


    @Override
    public Brain<MKEntity> getBrain() {
        return (Brain<MKEntity>) super.getBrain();
    }

    @Override
    protected Brain<MKEntity> createBrain(Dynamic<?> dynamicIn) {
        return new Brain<>(
                ImmutableList.of(
                        MKMemoryModuleTypes.ALLIES,
                        MKMemoryModuleTypes.ENEMIES,
                        MKMemoryModuleTypes.THREAT_LIST,
                        MKMemoryModuleTypes.THREAT_MAP,
                        MKMemoryModuleTypes.VISIBLE_ENEMIES,
                        MemoryModuleType.WALK_TARGET,
                        MemoryModuleType.PATH,
                        MKMemoryModuleTypes.MOVEMENT_STRATEGY,
                        MKMemoryModuleTypes.MOVEMENT_TARGET,
                        MKMemoryModuleTypes.CURRENT_ABILITY,
                        MKAbilityMemories.ABILITY_TARGET),
                ImmutableList.of(
                        MKSensorTypes.ENTITIES_SENSOR,
                        MKSensorTypes.THREAT_SENSOR,
                        MKSensorTypes.DESTINATION_SENSOR,
                        MKSensorTypes.ABILITY_SENSOR),
                dynamicIn);
    }

}
