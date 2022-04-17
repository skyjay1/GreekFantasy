package greekfantasy.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GoldenRamEntity extends SheepEntity implements IAngerable {
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(20, 39);
  private int angerTime;
  private UUID angerTarget;
    
  public GoldenRamEntity(final EntityType<? extends GoldenRamEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return SheepEntity.createAttributes()
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.ARMOR, 4.0D)
        .add(Attributes.ATTACK_DAMAGE, 3.0D)
        .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.54D, true));
    this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    // when sheared, despawn and replace with regular sheep
    if(this.isSheared()) {
      SheepEntity entity = EntityType.SHEEP.create(level);
      entity.copyPosition(this);
      entity.yBodyRot = this.yBodyRot;
      entity.setPortalCooldown();
      entity.setColor(this.getColor());
      entity.setSheared(true);
      entity.setAge(this.getAge());
      level.addFreshEntity(entity);
      // remove self
      this.remove();
    }
  }

  @Override
  public java.util.List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
    world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 0.8F);
    if (!world.isClientSide()) {
      this.setSheared(true);
      // create a list of items to return
      List<ItemStack> items = new ArrayList<>();
      items.add(new ItemStack(GFRegistry.GOLDEN_FLEECE));
      if (random.nextBoolean() || fortune > 0) {
        items.add(new ItemStack(GFRegistry.HORN));
      }
      return items;
    }
    return Collections.emptyList();
  }
  
  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }
  
  @Override
  public ResourceLocation getDefaultLootTable() { return this.getType().getDefaultLootTable(); }
  
  @Override
  public boolean isFood(ItemStack stack) { return false; }

  @Override
  public boolean canMate(AnimalEntity otherAnimal) { return false; }

  @Nullable
  public SheepEntity getBreedOffspring(ServerWorld world, AgeableEntity parentB) {
     return null;
  }
  
  @Nullable
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    this.setColor(DyeColor.YELLOW);
    this.setBaby(false);
    return data;
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    this.addPersistentAngerSaveData(compound);
  }
  
  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
  }
  
  // IAngerable methods
  
  @Override
  public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(ANGER_RANGE.randomValue(this.random)); }
  @Override
  public void setRemainingPersistentAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getRemainingPersistentAngerTime() { return this.angerTime; }
  @Override
  public void setPersistentAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getPersistentAngerTarget() { return this.angerTarget; }
}
