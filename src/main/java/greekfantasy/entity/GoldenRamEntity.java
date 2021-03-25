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
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(20, 39);
  private int angerTime;
  private UUID angerTarget;
    
  public GoldenRamEntity(final EntityType<? extends GoldenRamEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return SheepEntity.func_234225_eI_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
        .createMutableAttribute(Attributes.ARMOR, 4.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.54D, true));
    this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // when sheared, despawn and replace with regular sheep
    if(this.getSheared()) {
      SheepEntity entity = EntityType.SHEEP.create(world);
      entity.copyLocationAndAnglesFrom(this);
      entity.renderYawOffset = this.renderYawOffset;
      entity.func_242279_ag(); // setPortalCooldown
      entity.setFleeceColor(this.getFleeceColor());
      entity.setSheared(true);
      entity.setGrowingAge(this.getGrowingAge());
      world.addEntity(entity);
      // remove self
      this.remove();
    }
  }

  @Override
  public java.util.List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
    world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 0.8F);
    if (!world.isRemote()) {
      this.setSheared(true);
      // create a list of items to return
      List<ItemStack> items = new ArrayList<>();
      items.add(new ItemStack(GFRegistry.GOLDEN_FLEECE));
      if (rand.nextBoolean() || fortune > 0) {
        items.add(new ItemStack(GFRegistry.HORN));
      }
      return items;
    }
    return Collections.emptyList();
  }
  
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return false; }
  
  @Override
  public ResourceLocation getLootTable() { return this.getType().getLootTable(); }
  
  @Override
  public boolean isBreedingItem(ItemStack stack) { return false; }

  @Override
  public boolean canMateWith(AnimalEntity otherAnimal) { return false; }

  @Nullable
  public SheepEntity func_241840_a(ServerWorld world, AgeableEntity parentB) {
     return null;
  }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    this.setFleeceColor(DyeColor.YELLOW);
    return data;
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    this.writeAngerNBT(compound);
  }
  
  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  // IAngerable methods
  
  @Override
  public void func_230258_H__() { this.setAngerTime(ANGER_RANGE.getRandomWithinRange(this.rand)); }
  @Override
  public void setAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getAngerTime() { return this.angerTime; }
  @Override
  public void setAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getAngerTarget() { return this.angerTarget; }
}
