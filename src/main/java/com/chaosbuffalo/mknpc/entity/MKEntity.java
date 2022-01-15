package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkchat.capabilities.ChatCapabilities;
import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityTargetingDecision;
import com.chaosbuffalo.mkcore.core.player.ParticleEffectInstanceTracker;
import com.chaosbuffalo.mkcore.core.player.SyncComponent;
import com.chaosbuffalo.mkcore.entities.IUpdateEngineProvider;
import com.chaosbuffalo.mkcore.sync.EntityUpdateEngine;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
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
import com.chaosbuffalo.mknpc.inventories.QuestGiverInventoryContainer;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.targeting_api.Targeting;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("EntityConstructor")
public abstract class MKEntity extends CreatureEntity implements IModelLookProvider, IRangedAttackMob, IUpdateEngineProvider {
    private static final DataParameter<String> LOOK_STYLE = EntityDataManager.createKey(MKEntity.class, DataSerializers.STRING);
    private static final DataParameter<Float> SCALE = EntityDataManager.createKey(MKEntity.class, DataSerializers.FLOAT);
    private final SyncComponent animSync = new SyncComponent("anim");
    private int castAnimTimer;
    private VisualCastState visualCastState;
    private MKAbility castingAbility;
    private double lungeSpeed;
    private NonCombatMoveType nonCombatMoveType;
    private CombatMoveType combatMoveType;
    private MKMeleeAttackGoal meleeAttackGoal;
    private int comboCountDefault;
    private int comboCooldownDefault;
    private int comboCount;
    private int comboCooldown;
    private final EntityUpdateEngine updateEngine;
    private final ParticleEffectInstanceTracker particleEffectTracker;
    private final EntityTradeContainer entityTradeContainer;


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

    protected MKEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        if (!worldIn.isRemote()){
            setAttackComboStatsAndDefault(1, GameConstants.TICKS_PER_SECOND);
        }
        entityTradeContainer = new EntityTradeContainer(this);
        castAnimTimer = 0;
        visualCastState = VisualCastState.NONE;
        castingAbility = null;
        lungeSpeed = .25;
        updateEngine = new EntityUpdateEngine(this);
        animSync.attach(updateEngine);
        particleEffectTracker = ParticleEffectInstanceTracker.getTracker(this);
        animSync.addPublic(particleEffectTracker);
        nonCombatMoveType = NonCombatMoveType.RANDOM_WANDER;
        combatMoveType = CombatMoveType.MELEE;
        getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent((mkEntityData -> {
            mkEntityData.getAbilityExecutor().setStartCastCallback(this::startCast);
            mkEntityData.getAbilityExecutor().setCompleteAbilityCallback(this::endCast);
        }));
    }

    public ParticleEffectInstanceTracker getParticleEffectTracker() {
        return particleEffectTracker;
    }

    @Override
    public EntityUpdateEngine getUpdateEngine() {
        return updateEngine;
    }

    public double getLungeSpeed() {
        return lungeSpeed * getAttackSpeedMultiplier();
    }

    public void setLungeSpeed(double lungeSpeed) {
        this.lungeSpeed = lungeSpeed;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(double attackDamage, double movementSpeed) {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, attackDamage)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, movementSpeed)
                .createMutableAttribute(NpcAttributes.AGGRO_RANGE, 6)
                .createMutableAttribute(Attributes.ATTACK_SPEED)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(LOOK_STYLE, "default");
        this.dataManager.register(SCALE, 1.0f);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        attackEntityWithRangedAttack(target, distanceFactor, 1.6f);
        PointOfInterest
    }

    public void attackEntityWithRangedAttack(LivingEntity target, float launchPower, float launchVelocity) {
        ItemStack arrowStack = this.findAmmo(this.getHeldItem(Hand.MAIN_HAND));
        AbstractArrowEntity arrowEntity = ProjectileHelper.fireArrow(this, arrowStack, launchPower);
        Item mainhand = this.getHeldItemMainhand().getItem();
        if (mainhand instanceof BowItem){
            arrowEntity = ((BowItem) this.getHeldItemMainhand().getItem()).customArrow(arrowEntity);
        }
        EntityUtils.shootArrow(this, arrowEntity, target, launchPower * launchVelocity);
        this.playSound(getShootSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(arrowEntity);
    }

    protected SoundEvent getShootSound(){
        return SoundEvents.ENTITY_ARROW_SHOOT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected SoundEvent getStepSound(){
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    @Override
    public float getRenderScale() {
        return dataManager.get(SCALE);
    }

    public void setRenderScale(float newScale){
        dataManager.set(SCALE, newScale);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtThreatTargetGoal(this));
        this.targetSelector.addGoal(3, new MKTargetGoal(this, true, true));
        this.goalSelector.addGoal(0, new ReturnToSpawnGoal(this));
        this.goalSelector.addGoal(2, new MovementGoal(this));
        this.meleeAttackGoal =  new MKMeleeAttackGoal(this);
        this.goalSelector.addGoal(4, new MKBowAttackGoal(this, 5, 15.0f));
        this.goalSelector.addGoal(5, meleeAttackGoal);
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

    public MKMeleeAttackGoal getMeleeAttackGoal(){
        return meleeAttackGoal;
    }

    public void setComboDefaults(int count, int cooldown){
        comboCountDefault = count;
        comboCooldownDefault = cooldown;
    }

    public void setAttackComboStatsAndDefault(int count, int cooldown){
        setComboDefaults(count, cooldown);
        restoreComboDefaults();
    }

    public void restoreComboDefaults(){
        setAttackComboCount(comboCountDefault);
        setAttackComboCooldown(comboCooldownDefault);
    }

    public void setAttackComboCount(int count){
        comboCount = count;
    }

    public int getAttackComboCount(){
        return comboCount;
    }

    public void setAttackComboCooldown(int ticks){
        comboCooldown = ticks;
    }

    public int getAttackComboCooldown(){
        return comboCooldown;
    }



    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (key.equals(SCALE)){
            recalculateSize();
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return super.getStandingEyeHeight(poseIn, sizeIn) * dataManager.get(SCALE);
    }

    @Override
    public String getCurrentModelLook() {
        return dataManager.get(LOOK_STYLE);
    }

    @Override
    public void setCurrentModelLook(String group) {
        dataManager.set(LOOK_STYLE, group);
    }

    public MovementStrategy getMovementStrategy(AbilityTargetingDecision decision){
        MKAbility ability = decision.getAbility();
        if (ability == null){
            return StationaryMovementStrategy.STATIONARY_MOVEMENT_STRATEGY;
        }
        switch (decision.getMovementSuggestion()){
            case KITE:
                return new KiteMovementStrategy(Math.max(ability.getDistance(this) * .5, 8));
            case FOLLOW:
                return new FollowMovementStrategy(1.0f, Math.round(ability.getDistance(this) / 2.0f));
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
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
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
        ticksSinceLastSwing++;
        super.livingTick();
    }

    public void resetSwing(){
        ticksSinceLastSwing = 0;
    }

    public void subtractFromTicksSinceLastSwing(int toSubtract){
        ticksSinceLastSwing -= toSubtract;
    }

    public int getTicksSinceLastSwing(){
        return ticksSinceLastSwing;
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
                MovementStrategyController.enterRandomWander(this);
                break;
            case STATIONARY:
            default:
                MovementStrategyController.enterStationary(this);
                break;
        }

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
        ModifiableAttributeInstance attackSpeed = getAttribute(Attributes.ATTACK_SPEED);
        return attackSpeed.getValue() / getBaseAttackSpeedValueWithItem();
    }

    public double getBaseAttackSpeedValueWithItem(){
        ItemStack itemInHand = getHeldItemMainhand();
        double baseValue = getAttribute(Attributes.ATTACK_SPEED).getBaseValue();
        if (!itemInHand.equals(ItemStack.EMPTY)) {
            if (itemInHand.getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(
                    Attributes.ATTACK_SPEED)) {
                Collection<AttributeModifier> itemAttackSpeed = itemInHand.getAttributeModifiers(EquipmentSlotType.MAINHAND)
                        .get(Attributes.ATTACK_SPEED);
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
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.world.getProfiler().startSection("brain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().endSection();
    }

@Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        super.setPositionAndRotation(x, y, z, yaw, pitch);
        this.renderYawOffset = yaw;
        this.prevRenderYawOffset = yaw;
        this.setRotationYawHead(yaw);
        this.prevRotationYawHead = yaw;
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
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        if (hand.equals(Hand.MAIN_HAND) && getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                .map((cap) -> cap.getRelationToEntity(player) != Targeting.TargetRelation.ENEMY).orElse(false)){
            if (!player.world.isRemote() && player instanceof ServerPlayerEntity){
                if (player.isSneaking()){
                    player.openContainer(entityTradeContainer);
                } else {
                    getCapability(ChatCapabilities.NPC_DIALOGUE_CAPABILITY).ifPresent(cap ->
                            cap.startDialogue((ServerPlayerEntity) player, false));
                }

            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }


    @Override
    public Brain<MKEntity> getBrain() {
        return (Brain<MKEntity>) super.getBrain();
    }

    @Override
    protected Brain.BrainCodec<?> getBrainCodec() {
        return Brain.createCodec(
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
                        MKMemoryModuleTypes.IS_RETURNING,
                        MKMemoryModuleTypes.ABILITY_TIMEOUT
                ),
                ImmutableList.of(
                        MKSensorTypes.ENTITIES_SENSOR,
                        MKSensorTypes.THREAT_SENSOR,
                        MKSensorTypes.DESTINATION_SENSOR,
                        MKSensorTypes.ABILITY_SENSOR
                ));
    }

    @Override
    public ItemStack findAmmo(ItemStack shootable) {
        if (shootable.getItem() instanceof ShootableItem) {
            Predicate<ItemStack> predicate = ((ShootableItem)shootable.getItem()).getAmmoPredicate();
            ItemStack itemstack = ShootableItem.getHeldAmmo(this, predicate);
            return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

}
