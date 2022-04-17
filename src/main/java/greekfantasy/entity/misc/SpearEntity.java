package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.SpearItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SpearEntity extends AbstractArrowEntity {
    protected static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(SpearEntity.class, DataSerializers.ITEM_STACK);
    protected static final String KEY_ITEM = "Item";
    protected static final String KEY_DAMAGE = "DealtDamage";
    protected boolean dealtDamage;
    protected boolean enchanted;
    protected int loyaltyLevel;
    protected int returningTicks;

    protected ResourceLocation texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/spear/wooden_spear.png");

    protected Consumer<Entity> onHitEntity = e -> {
    };

    public SpearEntity(EntityType<? extends SpearEntity> type, World world) {
        super(type, world);
    }

    public SpearEntity(World world, LivingEntity thrower, ItemStack item, Consumer<Entity> hitEntity) {
        super(GFRegistry.SPEAR_ENTITY, thrower, world);
        setArrowStack(item);
        onHitEntity = hitEntity;
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
                if (!this.level.isClientSide && this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.remove();
            } else if (loyaltyLevel > 0) {
                this.setNoPhysics(true);
                Vector3d vector3d = new Vector3d(entity.getX() - this.getX(), entity.getEyeY() - this.getY(),
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
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
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
    public void onSyncedDataUpdated(final DataParameter<?> key) {
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
    protected void onHitEntity(EntityRayTraceResult raytrace) {
        Entity entity = raytrace.getEntity();
        dealtDamage = true;

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            this.setBaseDamage(this.getBaseDamage() + EnchantmentHelper.getDamageBonus(this.getPickupItem(), living.getMobType()));
        }

        Entity thrower = getOwner();
        DamageSource source = DamageSource.thrown(this, (thrower == null) ? this : thrower);
        onHitEntity.accept(entity);
        SoundEvent sound = SoundEvents.TRIDENT_HIT;

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
        final CompoundNBT nbt = getPickupItem().getOrCreateTagElement(SpearItem.KEY_POTION).copy();
        if (nbt.contains(SpearItem.KEY_POTION)) {
            nbt.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString(SpearItem.KEY_POTION)))));
            living.addEffect(EffectInstance.load(nbt));
        }
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d startVec, Vector3d endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        Entity thrower = getOwner();
        if (thrower != null && thrower.getUUID() != player.getUUID()) {
            return;
        }
        super.playerTouch(player);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item", 10)) {
            setArrowStack(ItemStack.of(tag.getCompound(KEY_ITEM)));
        }
        dealtDamage = tag.getBoolean(KEY_DAMAGE);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Item", getPickupItem().save(new CompoundNBT()));
        tag.putBoolean(KEY_DAMAGE, dealtDamage);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double dX, double dY, double dZ) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture() {
        return texture;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnchanted() {
        return enchanted;
    }
}
