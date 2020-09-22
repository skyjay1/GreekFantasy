package greekfantasy.entity;

import java.util.Random;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class GiganteEntity extends CreatureEntity {
  
  public GiganteEntity(final EntityType<? extends GiganteEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 56.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.32D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 1.5D);
  }
  
  public static boolean canGiganteSpawnOn(final EntityType<? extends MobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    return world.canBlockSeeSky(pos.up()) && MobEntity.canSpawnOn(entity, world, reason, pos, rand);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (GreekFantasy.CONFIG.GIGANTE_RESISTANCE.get()) {
      amount *= 0.6F;
    }
    super.damageEntity(source, amount);
  }
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    if(this.rand.nextBoolean()) {
      final ItemStack club = new ItemStack(rand.nextBoolean() ? GFRegistry.STONE_CLUB : GFRegistry.WOODEN_CLUB);
      this.setHeldItem(Hand.MAIN_HAND, club);
    }
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
}
