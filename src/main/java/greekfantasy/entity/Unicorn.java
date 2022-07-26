package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class Unicorn extends AbstractHorse {

    public Unicorn(EntityType<? extends Unicorn> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes().add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 16.0F, 1.1D, 0.95D, (entity) -> {
            return !entity.isDiscrete() && !this.isVehicle() &&
                    (!this.isTamed() || this.getOwnerUUID() == null || !entity.getUUID().equals(this.getOwnerUUID()));
        }));
    }

    // CALLED FROM ON INITIAL SPAWN //

    @Override
    protected void randomizeAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
    }

    @Override
    protected float generateRandomMaxHealth() {
        return super.generateRandomMaxHealth() + 28.0F;
    }

    @Override
    protected double generateRandomJumpStrength() {
        return super.generateRandomJumpStrength() + 0.22F;
    }

    @Override
    protected double generateRandomSpeed() {
        return super.generateRandomSpeed() + 0.16F;
    }

    // MISC //


    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide() && random.nextFloat() < 0.25F) {
            // spawn particles
            float radius = getBbWidth();
            level.addParticle(ParticleTypes.INSTANT_EFFECT,
                    this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                    this.getY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                    this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                    0, 0, 0);
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void die(DamageSource cause) {
        super.die(cause);
        if (cause.getEntity() instanceof LivingEntity) {
            LivingEntity killer = (LivingEntity) cause.getEntity();
            killer.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 12_000, 0, false, false, true, new MobEffectInstance(MobEffects.BAD_OMEN, 12_000, 0, false, false, false)));
        }
    }

    @Override
    protected void actuallyHurt(final DamageSource source, final float amountIn) {
        super.actuallyHurt(source, source.isBypassMagic() || source.isBypassArmor() ? amountIn : amountIn * 0.5F);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, effect);
            event.setResult(Event.Result.DENY);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.385D;
    }

    @Override
    protected void playGallopSound(SoundType sound) {
        super.playGallopSound(sound);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
        }

        ItemStack stack = this.inventory.getItem(1);
        if (isArmor(stack))
            stack.onHorseArmorTick(level, this);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openInventory(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
            }

            if ((itemstack.isEmpty() && this.isTamed()) || itemstack.is(GFRegistry.ItemReg.GOLDEN_BRIDLE.get())) {
                this.doPlayerRide(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(player, itemstack);
            }

            InteractionResult actionresulttype = itemstack.interactLivingEntity(player, this, hand);
            if (actionresulttype.consumesAction()) {
                return actionresulttype;
            }

            if (!this.isTamed()) {
                this.makeMad();
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }

            boolean isUsableSaddle = !this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE);
            if (this.isArmor(itemstack) || isUsableSaddle) {
                this.openInventory(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public int getMaxTemper() {
        return 160;
    }

    @Override
    public boolean canMate(final Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else {
            return otherAnimal instanceof Unicorn && this.canParent() && ((Unicorn) otherAnimal).canParent();
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
        Unicorn unicorn = GFRegistry.EntityReg.UNICORN.get().create(world);
        this.setOffspringAttributes(mate, unicorn);
        return unicorn;
    }

}
