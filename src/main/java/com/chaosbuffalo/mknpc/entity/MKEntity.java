package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkchat.capabilities.ChatCapabilities;
import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityTargetingDecision;
import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.core.pets.IMKPet;
import com.chaosbuffalo.mkcore.core.pets.PetNonCombatBehavior;
import com.chaosbuffalo.mkcore.core.player.ParticleEffectInstanceTracker;
import com.chaosbuffalo.mkcore.core.player.SyncComponent;
import com.chaosbuffalo.mkcore.entities.IUpdateEngineProvider;
import com.chaosbuffalo.mkcore.sync.EntityUpdateEngine;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
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
import com.chaosbuffalo.mknpc.entity.boss.BossStage;
import com.chaosbuffalo.mknpc.inventories.QuestGiverInventoryContainer;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.utils.NpcConstants;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.targeting_api.ITargetingOwner;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("EntityConstructor")
public abstract class MKEntity extends CreatureEntity implements IModelLookProvider, IRangedAttackMob, IUpdateEngineProvider, IMKPet, ITargetingOwner {
    private static final DataParameter<String> LOOK_STYLE = EntityDataManager.createKey(MKEntity.class, DataSerializers.STRING);
    private static final DataParameter<Float> SCALE = EntityDataManager.createKey(MKEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> IS_GHOST = EntityDataManager.createKey(MKEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> GHOST_TRANSLUCENCY = EntityDataManager.createKey(MKEntity.class, DataSerializers.FLOAT);
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
    private final List<BossStage> bossStages = new ArrayList<>();
    private int currentStage;
    @Nullable
    private PetNonCombatBehavior nonCombatBehavior;


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

    public void setGhost(boolean ghost) {
        getDataManager().set(IS_GHOST, ghost);
    }

    public boolean isGhost() {
        return getDataManager().get(IS_GHOST);
    }

    public void setGhostTranslucency(float ghostTranslucency) {
        getDataManager().set(GHOST_TRANSLUCENCY, ghostTranslucency);
    }

    @Nullable
    @Override
    public Entity getTargetingOwner() {
        IMKEntityData data = MKCore.getEntityDataOrNull(this);
        if (data != null) {
            return data.getPets().getOwner();
        } else {
            return null;
        }
    }

    public float getGhostTranslucency() {
        return getDataManager().get(GHOST_TRANSLUCENCY);
    }

    protected MKEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        if (!worldIn.isRemote()){
            setAttackComboStatsAndDefault(1, GameConstants.TICKS_PER_SECOND);
            setupDifficulty(worldIn.getDifficulty());
        }
        entityTradeContainer = new EntityTradeContainer(this);
        castAnimTimer = 0;
        currentStage = 0;
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
            mkEntityData.attachUpdateEngine(updateEngine);
            mkEntityData.getAbilityExecutor().setStartCastCallback(this::startCast);
            mkEntityData.getAbilityExecutor().setCompleteAbilityCallback(this::endCast);
        }));
    }

    public boolean hasBossStages(){
        return !bossStages.isEmpty();
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void addBossStage(BossStage stage){
        if (!hasBossStages()){
            stage.apply(this);
        }
        bossStages.add(stage);
    }

    @Override
    public boolean isInvisibleToPlayer(PlayerEntity player) {
        return !isGhost() && super.isInvisibleToPlayer(player);
    }

    @Override
    public boolean isInvisible() {
        return isGhost() || super.isInvisible();
    }

    public boolean hasNextStage(){
        return bossStages.size() > getCurrentStage() + 1;
    }

    public BossStage getNextStage(){
        return bossStages.get(getCurrentStage() + 1);
    }

    protected double getCastingSpeedForDifficulty(Difficulty difficulty){
        switch (difficulty){
            case NORMAL:
                return 0.5;
            case HARD:
                return 0.75;
            case EASY:
            default:
                return 0.25;
        }
    }

    protected void setupDifficulty(Difficulty difficulty){
        ModifiableAttributeInstance inst = getAttribute(MKAttributes.CASTING_SPEED);
        if (inst != null){
            inst.applyNonPersistentModifier(new AttributeModifier("difficulty",
                    getCastingSpeedForDifficulty(difficulty), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }


    public float getTranslucency(){
        // vanilla value is 0.15f
        return isGhost() ? getGhostTranslucency() : 0.15f;
    }

    public void postDefinitionApply(NpcDefinition definition){
        float maxHealth = getMaxHealth();
        if (maxHealth > 100.0f){
            float ratio = maxHealth / 100.0f;
            float adjustForBase = ratio - 1.0f;
            ModifiableAttributeInstance inst = getAttribute(MKAttributes.HEAL_EFFICIENCY);
            if (inst != null){
                inst.applyNonPersistentModifier(new AttributeModifier("heal_scaling",
                        adjustForBase, AttributeModifier.Operation.ADDITION));
            }
        }
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
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(LOOK_STYLE, "default");
        this.dataManager.register(SCALE, 1.0f);
        this.dataManager.register(IS_GHOST, false);
        this.dataManager.register(GHOST_TRANSLUCENCY, 1.0f);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        attackEntityWithRangedAttack(target, distanceFactor, 1.6f);
    }

    @Override
    protected void dropExperience() {
        super.dropExperience();
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

    public void callForHelp(LivingEntity entity, float threatVal){
       brain.getMemory(MKMemoryModuleTypes.ALLIES).ifPresent(x -> {
           x.forEach(ent -> {
               if (ent instanceof MKEntity){
                   if (ent.getDistanceSq(this) < 9.0){
                       ((MKEntity) ent).addThreat(entity, threatVal, true);
                   }
               }
           });
       });
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

    @Override
    public void onKillEntity(ServerWorld world, LivingEntity killedEntity) {
        super.onKillEntity(world, killedEntity);
        enterNonCombatMovementState();
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

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
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
    public void clearThreat() {
        getBrain().removeMemory(MKMemoryModuleTypes.THREAT_MAP);
        getBrain().removeMemory(MKMemoryModuleTypes.THREAT_TARGET);
        getBrain().removeMemory(MKMemoryModuleTypes.THREAT_LIST);
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
        boolean isReturningToPlayer = MKCore.getEntityData(this).map(x -> x.getPets().isPet()
                && x.getPets().getOwner() instanceof PlayerEntity).orElse(false);
        if (!isReturningToPlayer) {
            setHealth(Math.min(getHealth() + getMaxHealth() * .2f * 1.0f / GameConstants.TICKS_PER_SECOND,
                    getMaxHealth()));
        }
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

    @Override
    public void addThreat(LivingEntity entity, float value, boolean propagate) {
        Optional<Map<LivingEntity, ThreatMapEntry>> threatMap = this.brain.getMemory(MKMemoryModuleTypes.THREAT_MAP);
        Map<LivingEntity, ThreatMapEntry> newMap = threatMap.orElse(new HashMap<>());
        newMap.put(entity, newMap.getOrDefault(entity, new ThreatMapEntry()).addThreat(value));
        this.brain.setMemory(MKMemoryModuleTypes.THREAT_MAP, newMap);
        if (propagate) {
            MKCore.getEntityData(this).ifPresent(x -> {
                if (x.getPets().hasPet()) {
                    x.getPets().addThreatToPets(entity, value, false);
                }
            });
        }
    }

    @Override
    public void setNoncombatBehavior(PetNonCombatBehavior petNonCombatBehavior) {
        nonCombatBehavior = petNonCombatBehavior;
        enterNonCombatMovementState();
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
        if (nonCombatBehavior != null && !hasThreatTarget()){
            nonCombatBehavior.getEntity().ifPresent(x -> getBrain().setMemory(MKMemoryModuleTypes.SPAWN_POINT, x.getPosition()));
        }
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

    @Override
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

    @Override
    public void enterNonCombatMovementState() {
        if (nonCombatBehavior != null) {
            if (nonCombatBehavior.getBehaviorType() == PetNonCombatBehavior.Behavior.FOLLOW) {
                nonCombatBehavior.getEntity().ifPresent(x -> MovementStrategyController.enterFollowMode(this, 2, x));
            } else if (nonCombatBehavior.getBehaviorType() == PetNonCombatBehavior.Behavior.GUARD) {
                nonCombatBehavior.getPos().ifPresent(x -> getBrain().setMemory(MKMemoryModuleTypes.SPAWN_POINT, new BlockPos(x)));
            }
        } else {
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
            addThreat((LivingEntity) source.getTrueSource(), amount * NpcConstants.DAMAGE_THREAT_MULTIPLIER, true);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (hasNextStage()){
            BossStage next = getNextStage();
            next.apply(this);
            next.transition(this);
            setHealth(getMaxHealth());
            currentStage++;
            return;
        }
        super.onDeath(cause);
    }

    public boolean hasThreatWithTarget(LivingEntity target) {
        return getBrain().getMemory(MKMemoryModuleTypes.THREAT_MAP).map(x -> x.containsKey(target)).orElse(false);
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity target) {
        super.setRevengeTarget(target);
        if (target != null){
            addThreat(target, NpcConstants.INITIAL_THREAT, true);
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
    public float getHighestThreat() {
        return getBrain().getMemory(MKMemoryModuleTypes.THREAT_MAP).map(x -> {
            List<ThreatMapEntry> sorted = x.values().stream()
                    .sorted(Comparator.comparingDouble(ThreatMapEntry::getCurrentThreat))
                    .collect(Collectors.toList());
            if (sorted.size() == 0) {
                return 0f;
            }
            return sorted.get(sorted.size() - 1).getCurrentThreat();
        }).orElse(0f);
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
                        MKMemoryModuleTypes.ABILITY_TIMEOUT,
                        MKAbilityMemories.ABILITY_POSITION_TARGET
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
