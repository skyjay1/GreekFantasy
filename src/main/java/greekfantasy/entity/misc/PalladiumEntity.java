package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public class PalladiumEntity extends LivingEntity {

    public long lastHit;

    public PalladiumEntity(final EntityType<? extends PalladiumEntity> entityType, final World world) {
        super(entityType, world);
        this.maxUpStep = 0.0F;
    }

    public static PalladiumEntity createPalladium(final World world, final BlockPos pos, Direction facing) {
        PalladiumEntity entity = new PalladiumEntity(GFRegistry.PALLADIUM_ENTITY, world);
        float f = facing.toYRot();
        entity.absMoveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0);
        entity.yHeadRot = f;
        entity.yBodyRot = f;
        return entity;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 1.0F);
    }

    @Override
    public HandSide getMainArm() {
        return HandSide.RIGHT;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        super.tick();
        // attempt to place glow block
        BlockPos posIn = getOnPos().above();
        BlockState block = level.getBlockState(posIn);
        if((block.getMaterial() == Material.AIR || block.getMaterial().isLiquid()) && !GFRegistry.GLOW.is(block.getBlock())) {
            level.setBlock(posIn, GFRegistry.GLOW.defaultBlockState(), Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.removed) {
            if (DamageSource.OUT_OF_WORLD.equals(source)) {
                this.remove();
                return false;
            } else if (!this.isInvulnerableTo(source)) {
                if (source.isExplosion()) {
                    this.brokenByAnything(source);
                    this.remove();
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
                    boolean flag = source.getDirectEntity() instanceof AbstractArrowEntity;
                    boolean flag1 = flag && ((AbstractArrowEntity) source.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(source.getMsgId());
                    if (!flag2 && !flag) {
                        return false;
                    } else if (source.getEntity() instanceof PlayerEntity && !((PlayerEntity) source.getEntity()).abilities.mayBuild) {
                        return false;
                    } else if (source.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.remove();
                        return flag1;
                    } else {
                        long i = this.level.getGameTime();
                        if (i - this.lastHit > 5L && !flag) {
                            this.level.broadcastEntityEvent(this, (byte) 32);
                            this.lastHit = i;
                        } else {
                            this.brokenByPlayer(source);
                            this.showBreakingParticles();
                            this.remove();
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
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
        // do nothing
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerWorld) {
            ((ServerWorld) this.level).sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STRIPPED_OAK_LOG.defaultBlockState()), this.getX(), this.getY(0.66D), this.getZ(), 10, (double) (this.getBbWidth() / 4.0F), (double) (this.getBbHeight() / 4.0F), (double) (this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    private void causeDamage(DamageSource source, float amount) {
        float f = this.getHealth();
        f = f - amount;
        if (f <= 0.5F) {
            this.brokenByAnything(source);
            this.remove();
        } else {
            this.setHealth(f);
        }
    }

    private void brokenByPlayer(DamageSource p_213815_1_) {
        // drop altar
        final ItemStack altarItem = new ItemStack(GFRegistry.PALLADIUM);
        Block.popResource(level, blockPosition().above(), altarItem);
        // drop other
        this.brokenByAnything(p_213815_1_);
    }

    private void brokenByAnything(DamageSource source) {
        this.playBrokenSound();
        this.dropAllDeathLoot(source);
        BlockPos pos = blockPosition().above();
    }

    private void playBrokenSound() {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_BREAK, this.getSoundSource(), 1.0F, 1.0F);
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
