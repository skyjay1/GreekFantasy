package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.SpearItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ThrowingAxe extends ThrowableItemProjectile {

    protected static final EntityDataAccessor<Boolean> PICKUP = SynchedEntityData.defineId(ThrowingAxe.class, EntityDataSerializers.BOOLEAN);

    private static final String KEY_DAMAGE = "BaseDamage";

    protected double baseDamage = 2.0D;

    public ThrowingAxe(final EntityType<? extends ThrowingAxe> type, final Level world) {
        super(type, world);
    }

    public ThrowingAxe(Level world, LivingEntity thrower, ItemStack item, boolean pickup) {
        super(GFRegistry.EntityReg.THROWING_AXE.get(), thrower, world);
        setItem(item);
        setPickup(pickup);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(PICKUP, true);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void setItem(ItemStack itemStack) {
        super.setItem(itemStack);
        if(itemStack.getItem() instanceof DiggerItem diggerItem) {
            setBaseDamage(diggerItem.getAttackDamage());
        }
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.THROWING_AXE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        Entity entity = raytrace.getEntity();

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            this.setBaseDamage(this.getBaseDamage() + EnchantmentHelper.getDamageBonus(this.getItem(), living.getMobType()));
        }

        Entity thrower = getOwner();
        DamageSource source = DamageSource.thrown(this, (thrower == null) ? this : thrower);
        SoundEvent sound = SoundEvents.PLAYER_ATTACK_STRONG;

        if (entity.hurt(source, (float) this.getBaseDamage())) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (thrower instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(living, thrower);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) thrower, living);
                }
            }
        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        playSound(sound, 1.0F, 1.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        // damage item
        ItemStack item = this.getItem();
        if(item.isDamageableItem()) {
            item.setDamageValue(item.getDamageValue() + 1);
        }
        // drop as item
        if(canPickup()) {
            ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), item);
            level.addFreshEntity(itemEntity);
        }
        discard();
    }

    @Override
    public void playerTouch(Player player) {
        Entity thrower = getOwner();
        if (thrower != null && thrower.getUUID() != player.getUUID()) {
            return;
        }
        super.playerTouch(player);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble(KEY_DAMAGE, getBaseDamage());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setBaseDamage(tag.getDouble(KEY_DAMAGE));
    }

    @Override
    public boolean shouldRender(double dX, double dY, double dZ) {
        return true;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public void setPickup(final boolean canPickup) {
        this.getEntityData().set(PICKUP, canPickup);
    }

    public boolean canPickup() {
        return getEntityData().get(PICKUP);
    }

    @Override
    protected float getGravity() {
        return 0.08F;
    }
}
