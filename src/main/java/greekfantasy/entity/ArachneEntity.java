package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.WebBallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class ArachneEntity extends MonsterEntity implements IRangedAttackMob {
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.defineId(ArachneEntity.class, DataSerializers.BYTE);

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED,
            BossInfo.Overlay.PROGRESS);

    public ArachneEntity(EntityType<? extends ArachneEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.285D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new ArachneEntity.RangedAttackGoal(this, 1.0D, 75, 15.0F));
        this.goalSelector.addGoal(4, new ArachneEntity.MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
    }

    /**
     * Returns new PathNavigateGround instance
     */
    @Override
    protected PathNavigator createNavigation(World worldIn) {
        return new ClimberPathNavigator(this, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte) 0);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    // Climbing //

    /**
     * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or for AI
     * reasons)
     */
    @Override
    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vector3d motionMultiplierIn) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    // Ranged Attack //

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        WebBallEntity web = WebBallEntity.create(level, this);
        // set the web type with sometimes web, always spider, and no item
        web.setWebType(getRandom().nextBoolean() || !hasSilkstep(target), true, false);
        // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
        web.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.sin(this.yBodyRot * 0.017453292F),
                this.getEyeY() - 0.1D,
                this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.cos(this.yBodyRot * 0.017453292F));
        double dx = target.getX() - web.getX();
        double dy = target.getY(0.67D) - web.getY();
        double dz = target.getZ() - web.getZ();
        double dis = MathHelper.sqrt(dx * dx + dz * dz);
        web.shoot(dx, dy + dis * (double) 0.2F, dz, 1.14F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(web);
    }

    // Misc //

    @Override
    public CreatureAttribute getMobType() {
        return CreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean canBeAffected(EffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == Effects.POISON) {
            net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
                    this, potioneffectIn);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.canBeAffected(potioneffectIn);
    }

    private static boolean hasSilkstep(final LivingEntity player) {
        return player != null && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.SILKSTEP_ENCHANTMENT, player.getItemBySlot(EquipmentSlotType.FEET)) > 0;
    }

    //Boss //

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showArachneBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {
        public RangedAttackGoal(IRangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
            super(entity, moveSpeed, attackInterval, attackDistance);
        }

        @Override
        public boolean canUse() {
            return (super.canUse() && ArachneEntity.this.distanceToSqr(ArachneEntity.this.getTarget()) > 9.0D);
        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

        public MeleeAttackGoal(CreatureEntity entity, double moveSpeed, boolean useLongMemory) {
            super(entity, moveSpeed, useLongMemory);
        }

        @Override
        public boolean canUse() {
            return (super.canUse() && ArachneEntity.this.distanceToSqr(ArachneEntity.this.getTarget()) < 9.0D);
        }
    }

}
