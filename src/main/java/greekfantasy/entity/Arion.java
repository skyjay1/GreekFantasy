package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

public class Arion extends Horse {

    public static final TagKey<Item> FOOD = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "arion_food"));
    public static final TagKey<Item> TRIGGER = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "arion_trigger"));

    public Arion(EntityType<? extends Arion> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.ARMOR, 2.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
    }

    public static Arion spawnArion(final ServerLevel level, final Player player, final Horse horse) {
        Arion entity = GFRegistry.EntityReg.ARION.get().create(level);
        entity.copyPosition(horse);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(horse.blockPosition()), MobSpawnType.CONVERSION, null, null);
        if (horse.hasCustomName()) {
            entity.setCustomName(horse.getCustomName());
            entity.setCustomNameVisible(horse.isCustomNameVisible());
        }
        entity.setOwnerUUID(horse.getOwnerUUID());
        entity.setTamed(horse.isTamed());
        entity.setPersistenceRequired();
        entity.yBodyRot = horse.yBodyRot;
        entity.setPortalCooldown();
        entity.setAge(1);
        level.addFreshEntity(entity);
        // copy inventory and remove the old horse
        entity.copyInventory(horse);
        horse.inventory.clearContent();
        horse.discard();
        // give potion effects
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600));
        // play sound
        level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, entity.getSoundSource(), 1.0F, 1.0F, false);
        return entity;
    }

    public void copyInventory(final Horse horse) {
        for(int i = 0, n = horse.inventory.getContainerSize(); i < n; i++) {
            this.inventory.setItem(i, horse.inventory.getItem(i).copy());
        }
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public Variant getVariant() {
        return Variant.WHITE;
    }

    @Override
    public Markings getMarkings() {
        return Markings.NONE;
    }

    @Override
    public int getMaxTemper() {
        return 200;
    }

    // CALLED FROM ON INITIAL SPAWN //

    @Override
    protected float generateRandomMaxHealth() {
        return super.generateRandomMaxHealth() + 30.0F;
    }

    @Override
    protected double generateRandomJumpStrength() {
        return super.generateRandomJumpStrength() + 0.25F;
    }

    @Override
    protected double generateRandomSpeed() {
        return super.generateRandomSpeed() + 0.21F;
    }

    // MISC //

    @Override
    protected boolean handleEating(final Player player, final ItemStack stack) {
        if (stack.is(FOOD)) {
            return super.handleEating(player, stack);
        }
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FOOD);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.15D;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Most of this is copied from HorseEntity
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openInventory(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
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
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE);
            if (this.isArmor(itemstack) || flag) {
                this.openInventory(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        // Only mount if already tame
        if (this.isTamed() && !this.isBaby()) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        // DO NOT CALL SUPER METHOD
        // return super.getEntityInteractionResult(player, hand);
        return InteractionResult.PASS;
    }
}
