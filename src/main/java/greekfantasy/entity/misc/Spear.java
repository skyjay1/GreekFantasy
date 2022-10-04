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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class Spear extends AbstractArrow {
    protected static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(Spear.class, EntityDataSerializers.ITEM_STACK);
    protected static final String KEY_ITEM = "Item";
    protected static final String KEY_DAMAGE = "DealtDamage";
    protected static final String KEY_SET_FIRE = "SetFire";
    protected boolean dealtDamage;
    protected boolean enchanted;
    protected int loyaltyLevel;
    protected int setFire;
    protected int returningTicks;

    protected ResourceLocation texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/spear/wooden_spear.png");

    public Spear(final EntityType<? extends Spear> type, final Level world) {
        super(type, world);
    }

    public Spear(Level world, LivingEntity thrower, ItemStack item, int setFire) {
        super(GFRegistry.EntityReg.SPEAR.get(), thrower, world);
        setArrowStack(item);
        this.setFire = setFire;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (loyaltyLevel > 0 && !this.shouldReturnToThrower()) {
                if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else if (loyaltyLevel > 0) {
                this.setNoPhysics(true);
                Vec3 vector3d = new Vec3(entity.getX() - this.getX(), entity.getEyeY() - this.getY(),
                        entity.getZ() - this.getZ());
                this.setPosRaw(this.getX(), this.getY() + vector3d.y * 0.015D * loyaltyLevel, this.getZ());
                if (this.level.isClientSide()) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * loyaltyLevel;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returningTicks;
            }
        }

        super.tick();
    }

    protected boolean shouldReturnToThrower() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer) || !entity.isSpectator());
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.getEntityData().get(ITEM).copy();
    }

    protected void setArrowStack(final ItemStack stack) {
        this.getEntityData().set(ITEM, stack.copy());
        final ResourceLocation name = stack.getItem().getRegistryName();
        this.texture = new ResourceLocation(name.getNamespace(), "textures/entity/spear/" + name.getPath() + ".png");
        this.loyaltyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, stack);
        this.enchanted = stack.hasFoil();
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(ITEM)) {
            ItemStack stack = getPickupItem();
            final ResourceLocation name = stack.getItem().getRegistryName();
            this.texture = new ResourceLocation(name.getNamespace(), "textures/entity/spear/" + name.getPath() + ".png");
            this.loyaltyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, stack);
            this.enchanted = stack.hasFoil();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        Entity entity = raytrace.getEntity();
        dealtDamage = true;

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            this.setBaseDamage(this.getBaseDamage() + EnchantmentHelper.getDamageBonus(this.getPickupItem(), living.getMobType()));
        }

        Entity thrower = getOwner();
        DamageSource source = DamageSource.thrown(this, (thrower == null) ? this : thrower);
        SoundEvent sound = SoundEvents.TRIDENT_HIT;
        // attempt to set entity on fire
        if(setFire > 0) {
            entity.setSecondsOnFire(setFire);
        }

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
                doPostHurtEffects(living);
            }
        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        playSound(sound, 1.0F, 1.0F);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        ItemStack stack = getPickupItem();
        if (stack.hasTag() && stack.getTag().contains(SpearItem.KEY_MOB_EFFECT)) {
            final CompoundTag nbt = stack.getTag().getCompound(SpearItem.KEY_MOB_EFFECT).copy();
            nbt.putByte("Id", (byte) MobEffect.getId(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString(SpearItem.KEY_MOB_EFFECT)))));
            MobEffectInstance effectInstance = MobEffectInstance.load(nbt);
            if(effectInstance != null) {
                living.addEffect(effectInstance);
            }
        }
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item", 10)) {
            setArrowStack(ItemStack.of(tag.getCompound(KEY_ITEM)));
        }
        dealtDamage = tag.getBoolean(KEY_DAMAGE);
        setFire = tag.getInt(KEY_SET_FIRE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Item", getPickupItem().save(new CompoundTag()));
        tag.putBoolean(KEY_DAMAGE, dealtDamage);
        tag.putInt(KEY_SET_FIRE, setFire);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public boolean shouldRender(double dX, double dY, double dZ) {
        return true;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public boolean hasFoil() {
        return enchanted;
    }
}
