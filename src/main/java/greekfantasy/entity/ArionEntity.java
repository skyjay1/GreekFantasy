package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.passive.horse.CoatTypes;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ArionEntity extends HorseEntity {

    protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "arion_food"));

    public ArionEntity(EntityType<? extends ArionEntity> type, World worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.5F;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractHorseEntity.createBaseHorseAttributes().add(Attributes.ARMOR, 2);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
    }

    public static ArionEntity spawnArion(final ServerWorld world, final PlayerEntity player, final HorseEntity horse) {
        ArionEntity entity = GFRegistry.ARION_ENTITY.create(world);
        entity.copyPosition(horse);
        entity.finalizeSpawn(world, world.getCurrentDifficultyAt(horse.blockPosition()), SpawnReason.CONVERSION, null, null);
        if (horse.hasCustomName()) {
            entity.setCustomName(horse.getCustomName());
            entity.setCustomNameVisible(horse.isCustomNameVisible());
        }
        entity.tameWithName(player);
        entity.setPersistenceRequired();
        entity.yBodyRot = horse.yBodyRot;
        entity.setPortalCooldown();
        entity.setAge(horse.getAge());
        world.addFreshEntity(entity);
        // drop the old horse items
        if (horse.inventory != null) {
            for (int i = 0; i < horse.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = horse.inventory.getItem(i);
                if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
                    horse.spawnAtLocation(itemstack);
                }
            }
        }
        // remove the old horse
        horse.remove();
        // play sound
        world.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, entity.getSoundSource(), 1.0F, 1.0F, false);
        return entity;
    }

    @Override
    public boolean canMate(AnimalEntity otherAnimal) {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public CoatColors getVariant() {
        return CoatColors.BLACK;
    }

    @Override
    public CoatTypes getMarkings() {
        return CoatTypes.NONE;
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
    protected boolean handleEating(final PlayerEntity player, final ItemStack stack) {
        if (stack.getItem().is(FOOD)) {
            return super.handleEating(player, stack);
        }
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem().is(FOOD);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.25D;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        // Most of this is copied from HorseEntity
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openInventory(player);
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(player, itemstack);
            }

            ActionResultType actionresulttype = itemstack.interactLivingEntity(player, this, hand);
            if (actionresulttype.consumesAction()) {
                return actionresulttype;
            }

            if (!this.isTamed()) {
                this.makeMad();
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }

            boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
            if (this.isArmor(itemstack) || flag) {
                this.openInventory(player);
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }
        }

        // Only mount if already tame
        if (this.isTamed() && !this.isBaby()) {
            this.doPlayerRide(player);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }
        // DO NOT CALL SUPER METHOD
        // return super.getEntityInteractionResult(player, hand);
        return ActionResultType.PASS;
    }
}
