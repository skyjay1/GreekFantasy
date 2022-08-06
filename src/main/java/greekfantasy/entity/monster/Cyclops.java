package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.item.ClubItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class Cyclops extends Monster {

    public Cyclops(final EntityType<? extends Cyclops> type, final Level level) {
        super(type, level);
        // avoid water because this entity does not swim or float
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.5D)
                .add(Attributes.ARMOR, 3.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Mob.class, 16.0F, 1.2D, 1.1D, (entity) -> {
            return !entity.isSpectator() && entity.hasCustomName() && "Nobody".equals(entity.getCustomName().getString());
        }));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(damageSource, p_21386_, p_21387_);
    }

    @Override
    protected void dropEquipment() {
        // damage held equipment before dropping it
        ItemStack mainhand = this.getMainHandItem();
        if(mainhand.isDamageableItem()) {
            // set item damage to some value between 30% and 50%
            int damage = Mth.floor((0.30F + 0.20F * random.nextFloat()) * mainhand.getMaxDamage());
            mainhand.setDamageValue(damage);
        }
        super.dropEquipment();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // note: do not call super method because this entity cannot wear armor
        if (this.random.nextBoolean()) {
            // determine club type
            Item club = this.random.nextBoolean() ? GFRegistry.ItemReg.STONE_CLUB.get() : GFRegistry.ItemReg.WOODEN_CLUB.get();
            ItemStack itemStack = new ItemStack(club);
            // randomly enchant club
            if(this.random.nextFloat() < 0.10F * difficulty.getSpecialMultiplier()) {
                itemStack.enchant(Enchantments.KNOCKBACK, 1);
            }
            // update held item and drop chance
            this.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
            this.setDropChance(EquipmentSlot.MAINHAND, 0.1985F);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(worldIn, difficulty, mobSpawnType, spawnDataIn, dataTag);
    }

    @Override
    protected float getJumpPower() {
        return 1.1F * super.getJumpPower();
    }
}
