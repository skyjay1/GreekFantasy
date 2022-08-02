package greekfantasy.entity;

import com.google.common.collect.ImmutableList;
import greekfantasy.GFRegistry;
import greekfantasy.block.PalladiumLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class Palladium extends LivingEntity {

    public long lastHit;

    public Palladium(final EntityType<? extends Palladium> entityType, final Level world) {
        super(entityType, world);
        this.maxUpStep = 0.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F);
    }

    public static Palladium createPalladium(final Level level, final BlockPos pos, Direction facing) {
        Palladium entity = GFRegistry.EntityReg.PALLADIUM.get().create(level);
        float f = facing.toYRot();
        entity.absMoveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0);
        entity.yHeadRot = f;
        entity.yBodyRot = f;
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (DamageSource.OUT_OF_WORLD.equals(source)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(source)) {
                if (source.isExplosion()) {
                    this.brokenByAnything(source);
                    this.kill();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(source)) {
                    if (this.isOnFire()) {
                        this.causeDamage(source, 0.15F);
                    } else {
                        this.setSecondsOnFire(5);
                    }

                    return false;
                } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
                    this.causeDamage(source, 4.0F);
                    return false;
                } else {
                    boolean flag = source.getDirectEntity() instanceof AbstractArrow;
                    boolean flag1 = flag && ((AbstractArrow) source.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(source.getMsgId());
                    if (!flag2 && !flag) {
                        return false;
                    } else if (source.getEntity() instanceof Player && !((Player) source.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (source.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.kill();
                        return flag1;
                    } else {
                        long i = this.level.getGameTime();
                        if (i - this.lastHit > 5L && !flag) {
                            this.level.broadcastEntityEvent(this, (byte) 32);
                            this.lastHit = i;
                        } else {
                            this.brokenByPlayer(source);
                            this.showBreakingParticles();
                            this.kill();
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
        // do nothing
    }

    @Override
    public void aiStep() {
        // parent tick
        super.aiStep();
        // server-side tick logic
        if (!level.isClientSide && tickCount % 4 == 1) {
            // attempt to place light block
            BlockPos posIn = getOnPos().above();
            BlockState blockIn = level.getBlockState(posIn);
            // check if current block can be replaced
            if ((blockIn.getMaterial() == Material.AIR || blockIn.getMaterial().isLiquid())
                    && !GFRegistry.BlockReg.LIGHT.get().defaultBlockState().is(blockIn.getBlock())) {
                // determine waterlog value
                boolean waterlogged = blockIn.getFluidState().isSource() && blockIn.getFluidState().is(FluidTags.WATER);
                // create light block
                BlockState lightBlock = GFRegistry.BlockReg.LIGHT.get()
                        .defaultBlockState()
                        .setValue(PalladiumLightBlock.LEVEL, 11)
                        .setValue(PalladiumLightBlock.WATERLOGGED, waterlogged);
                // place light block
                level.setBlock(posIn, lightBlock, Block.UPDATE_ALL);
            }
        }
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel) this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), this.getX(), this.getY(0.66D), this.getZ(), 10, (double) (this.getBbWidth() / 4.0F), (double) (this.getBbHeight() / 4.0F), (double) (this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    private void causeDamage(DamageSource source, float amount) {
        float f = this.getHealth();
        f = f - amount;
        if (f <= 0.5F) {
            this.brokenByAnything(source);
            this.kill();
        } else {
            this.setHealth(f);
        }
    }

    private void brokenByPlayer(DamageSource p_213815_1_) {
        // drop altar
        final ItemStack altarItem = new ItemStack(GFRegistry.ItemReg.PALLADIUM.get());
        Block.popResource(level, blockPosition().above(), altarItem);
        // drop other
        this.brokenByAnything(p_213815_1_);
    }

    private void brokenByAnything(DamageSource source) {
        this.playBrokenSound();
        this.dropAllDeathLoot(source);
    }

    private void playBrokenSound() {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }
}
