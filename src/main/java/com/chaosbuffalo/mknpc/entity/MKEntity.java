package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkchat.capabilities.ChatCapabilities;
import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityTargetingDecision;
import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.entity.ai.controller.MovementStrategyController;
import com.chaosbuffalo.mknpc.entity.ai.goal.*;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.chaosbuffalo.mknpc.entity.ai.memory.ThreatMapEntry;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.FollowMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.KiteMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.MovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.movement_strategy.StationaryMovementStrategy;
import com.chaosbuffalo.mknpc.entity.ai.sensor.MKSensorTypes;
import com.chaosbuffalo.mknpc.entity.attributes.NpcAttributes;
import com.chaosbuffalo.targeting_api.Targeting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("EntityConstructor")
public abstract class MKEntity extends CreatureEntity {
    private int castAnimTimer;
    private VisualCastState visualCastState;
    private MKAbility castingAbility;
    private double lungeSpeed;
    private int meleeRange;
    private NonCombatMoveType nonCombatMoveType;
    private CombatMoveType combatMoveType;

    public enum CombatMoveType {
        MELEE,
        RANGE,
        STATIONARY
    }

    public enum NonCombatMoveType {
        STATIONARY,
        RANDOM_WANDER
    }

    public enum VisualCastState {
        NONE,
        CASTING,
        RELEASE,
    }

    public double getLungeSpeed() {
        return lungeSpeed * getAttackSpeedMultiplier();
    }

    public void setLungeSpeed(double lungeSpeed) {
        this.lungeSpeed = lungeSpeed;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtThreatTargetGoal(this));
        this.targetSelector.addGoal(3, new MKTargetGoal(this, true, true));
        this.goalSelector.addGoal(0, new ReturnToSpawnGoal(this));
        this.goalSelector.addGoal(2, new MovementGoal(this));
        this.goalSelector.addGoal(4, new MKMeleeAttackGoal(this));
        this.goalSelector.addGoal(3, new UseAbilityGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
    }

    public boolean avoidsWater(){
        return true;
    }

    private void handleCombatMovementDetect(ItemStack stack){
        if (ItemUtils.isRangedWeapon(stack)){
            setCombatMoveType(CombatMoveType.RANGE);
        } else {
            setCombatMoveType(CombatMoveType.MELEE);
        }
    }

    @Override
    public void setHeldItem(Hand hand, ItemStack stack) {
        super.setHeldItem(hand, stack);
        if (hand == Hand.MAIN_HAND){
            handleCombatMovementDetect(stack);
        }
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
        if (slotIn == EquipmentSlotType.MAINHAND){
            handleCombatMovementDetect(stack);
        }
    }

    protected MKEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        castAnimTimer = 0;
        visualCastState = VisualCastState.NONE;
        castingAbility = null;
        lungeSpeed = .25;
        meleeRange = 1;
        nonCombatMoveType = NonCombatMoveType.RANDOM_WANDER;
        combatMoveType = CombatMoveType.MELEE;
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
                return new KiteMovementStrategy(Math.max(ability.getDistance() * .5, 8));
            case FOLLOW:
                return new FollowMovementStrategy(1.0f, Math.round(ability.getDistance() / 2.0f));
            case MELEE:
                return new FollowMovementStrategy(1.0f, 1);
            case STATIONARY:
            default:
                return StationaryMovementStrategy.STATIONARY_MOVEMENT_STRATEGY;
        }
    }

    public void returnToSpawnTick(){
        setHealth(Math.min(getHealth() + getMaxHealth() * .2f * 1.0f / GameConstants.TICKS_PER_SECOND,
                getMaxHealth()));
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        MKNpc.LOGGER.info("In initial spawn for {}", this);
        ILivingEntityData entityData = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.getCapability(NpcCapabilities.ENTITY_NPC_DATA_CAPABILITY).ifPresent((cap) -> {
            if (cap.wasMKSpawned()){
                getBrain().setMemory(MKMemoryModuleTypes.SPAWN_POINT, cap.getSpawnPos());
            }
        });
        enterNonCombatMovementState();
        return entityData;
    }

    public void addThreat(LivingEntity entity, float value) {
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

    public void returnToDefaultMovementState(){
        LivingEntity target = getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET).orElse(null);
        if (target != null){
            enterCombatMovementState(target);
        } else {
            enterNonCombatMovementState();
        }
    }

    public void endCast(MKAbility ability) {
        castingAbility = ability;
        visualCastState = VisualCastState.RELEASE;
        castAnimTimer = 15;
    }

    public void setCombatMoveType(CombatMoveType combatMoveType) {
        this.combatMoveType = combatMoveType;
    }

    public void setNonCombatMoveType(NonCombatMoveType nonCombatMoveType) {
        this.nonCombatMoveType = nonCombatMoveType;
    }


    public NonCombatMoveType getNonCombatMoveType() {
        return nonCombatMoveType;
    }

    public CombatMoveType getCombatMoveType() {
        return combatMoveType;
    }

    public int getWanderRange(){
        return 10;
    }

    public void enterCombatMovementState(LivingEntity target) {
        getBrain().setMemory(MKMemoryModuleTypes.MOVEMENT_TARGET, target);
        switch (getCombatMoveType()){
            case STATIONARY:
                MovementStrategyController.enterStationary(this);
                break;
            case RANGE:
                MovementStrategyController.enterCastingMode(this, 6.0);
                break;
            case MELEE:
            default:
                MovementStrategyController.enterMeleeMode(this, 1);
                break;
        }
    }

    public void enterNonCombatMovementState() {
        switch (getNonCombatMoveType()){
            case RANDOM_WANDER:
                MKNpc.LOGGER.info("Entering random wander");
                MovementStrategyController.enterRandomWander(this);
                break;
            case STATIONARY:
            default:
                MovementStrategyController.enterStationary(this);
                break;
        }

    }

    public void setMeleeRange(int meleeRange) {
        this.meleeRange = meleeRange;
    }

    public int getMeleeRange() {
        return meleeRange;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttributes().registerAttribute(NpcAttributes.AGGRO_RANGE);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
    }

    public boolean hasThreatTarget(){
        return getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET).isPresent();
    }

    public void reduceThreat(LivingEntity entity, float value) {
        Optional<Map<LivingEntity, ThreatMapEntry>> threatMap = this.brain.getMemory(MKMemoryModuleTypes.THREAT_MAP);
        Map<LivingEntity, ThreatMapEntry> newMap = threatMap.orElse(new HashMap<>());
        newMap.put(entity, newMap.getOrDefault(entity, new ThreatMapEntry()).subtractThreat(value));
        this.brain.setMemory(MKMemoryModuleTypes.THREAT_MAP, newMap);
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
    }

    public double getAttackSpeedMultiplier(){
        IAttributeInstance attackSpeed = getAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        return attackSpeed.getValue() / getBaseAttackSpeedValueWithItem();
    }

    public double getBaseAttackSpeedValueWithItem(){
        ItemStack itemInHand = getHeldItemMainhand();
        double baseValue = getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
        if (!itemInHand.equals(ItemStack.EMPTY)) {
            if (itemInHand.getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(
                    SharedMonsterAttributes.ATTACK_SPEED.getName())) {
                Collection<AttributeModifier> itemAttackSpeed = itemInHand.getAttributeModifiers(EquipmentSlotType.MAINHAND)
                        .get(SharedMonsterAttributes.ATTACK_SPEED.getName());
                double attackSpeed = 4.0;
                for (AttributeModifier mod : itemAttackSpeed) {
                    if (mod.getOperation().equals(AttributeModifier.Operation.ADDITION)) {
                        attackSpeed += mod.getAmount();
                    }
                }
                baseValue = attackSpeed;
            }
        }
        return baseValue;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.world.getProfiler().startSection("brain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().endSection();
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return 0.5F - worldIn.getBrightness(pos);
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof LivingEntity) {
            addThreat((LivingEntity) source.getTrueSource(), amount * 100.0f);
        }
        return super.attackEntityFrom(source, amount);
    }

    public void addTargetToThreat(@Nullable LivingEntity target, float baseThreat){
        getBrain().getMemory(MKMemoryModuleTypes.THREAT_MAP).ifPresent(map ->
                map.put(target, new ThreatMapEntry().addThreat(baseThreat)));
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity target) {
        super.setRevengeTarget(target);
        if (target != null){
            addTargetToThreat(target, 500);
        }
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if (hand.equals(Hand.MAIN_HAND) && getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                .map((cap) -> cap.getRelationToEntity(player) != Targeting.TargetRelation.ENEMY).orElse(false)){
            if (!player.world.isRemote()){
                getCapability(ChatCapabilities.NPC_DIALOGUE_CAPABILITY).ifPresent(cap ->
                        cap.startDialogue((ServerPlayerEntity) player));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
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
                        MKAbilityMemories.ABILITY_TARGET,
                        MKMemoryModuleTypes.SPAWN_POINT,
                        MKMemoryModuleTypes.IS_RETURNING),
                ImmutableList.of(
                        MKSensorTypes.ENTITIES_SENSOR,
                        MKSensorTypes.THREAT_SENSOR,
                        MKSensorTypes.DESTINATION_SENSOR,
                        MKSensorTypes.ABILITY_SENSOR),
                dynamicIn);
    }

}
