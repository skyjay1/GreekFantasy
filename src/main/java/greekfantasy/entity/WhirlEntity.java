package greekfantasy.entity;

import java.util.List;
import java.util.Random;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.FavorConfiguration;
import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class WhirlEntity extends WaterMobEntity implements ISwimmingMob {

  protected static final IOptionalNamedTag<Item> TRIGGER = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "charybdis_trigger"));
  
  protected static final DataParameter<Boolean> ATTRACT_MOBS = EntityDataManager.defineId(WhirlEntity.class, DataSerializers.BOOLEAN);
  protected static final String KEY_AFFECTS_MOBS = "AttractMobs";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";

  private static final double RANGE = 9.0D;
  
  private boolean swimmingUp;
  
  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  public WhirlEntity(final EntityType<? extends WhirlEntity> type, final World worldIn) {
    super(type, worldIn);
    this.xpReward = 5;
  }

  //copied from DolphinEntity
  public static boolean canWhirlSpawnOn(final EntityType<? extends WaterMobEntity> entity, final IWorld world,
      final SpawnReason reason, final BlockPos pos, final Random rand) {
    if (pos.getY() <= 25 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }

    RegistryKey<Biome> biome = world.getBiomeName(pos).orElse(Biomes.PLAINS);
    return (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) && world.getFluidState(pos).is(FluidTags.WATER);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 10.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.15D)
        .add(Attributes.ATTACK_DAMAGE, 0.25D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .add(Attributes.FOLLOW_RANGE, 32.0D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(ATTRACT_MOBS, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new WhirlEntity.SwimToSurfaceGoal());
    this.goalSelector.addGoal(2, new WhirlEntity.SwirlGoal(this, 1000, 0, RANGE));
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    
    // remove if colliding with another whirl or a charybdis
    final List<WaterMobEntity> waterMobList = this.level.getEntitiesOfClass(WaterMobEntity.class, this.getBoundingBox().inflate(1.0D), 
        e -> e != this && e.isAlive() && (e.getType() == GFRegistry.CHARYBDIS_ENTITY || e.getType() == GFRegistry.WHIRL_ENTITY));
    if(!waterMobList.isEmpty() && this.isAlive()) {
      this.hurt(DamageSource.STARVE, this.getMaxHealth() * 2.0F);
      return;
    }
 
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 20;
      this.setHealth(this.getHealth() - 1.0F);
    }
    
    // attract nearby items
    final List<ItemEntity> itemEntityList = this.level.getEntities(EntityType.ITEM, this.getBoundingBox().inflate(1.0), e -> e.isInWaterOrBubble() && (this.getY() + this.getBbHeight()) > e.getY());
    for(final ItemEntity e : itemEntityList) {
      // check for trigger items
      if(this.getCommandSenderWorld() instanceof ServerWorld && !e.getItem().isEmpty() && e.getItem().getItem().is(TRIGGER)) {
        CharybdisEntity.spawnCharybdis((ServerWorld) this.getCommandSenderWorld(), this);
        e.hurt(DamageSource.mobAttack(this), 1.0F);
      }
      // start to remove items
      if(!e.hasPickUpDelay()) {
        e.hurt(DamageSource.mobAttack(this), 1.0F);
      }
      // play sound when item is removed
      if(!e.isAlive()) {
        this.playSound(SoundEvents.GENERIC_DRINK, 0.6F, 0.8F + this.getRandom().nextFloat() * 0.4F);
      }
    }
    
    // spawn particles
    if(this.level.isClientSide() && tickCount % 3 == 0 && this.isInWaterOrBubble()) {
      // spawn particles in spiral
      float maxY = this.getBbHeight() * 1.65F;
      float y = 0;
      float nY = 90;
      float dY = maxY / nY;
      double posX = this.getX();
      double posY = this.getY();
      double posZ = this.getZ();
      // for each y-position, increase the angle and spawn particle here
      for(float a = 0, nA = 28 + random.nextInt(4), dA = (2 * (float)Math.PI) / nA; y < maxY; a += dA) {
        float radius = y * 0.35F;
        float cosA = MathHelper.cos(a) * radius;
        float sinA = MathHelper.sin(a) * radius;
        //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
        level.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
        y += dY;
      }
    }
  }
  
  @Override
  protected void doPush(final Entity entityIn) {
    super.doPush(entityIn);
  }

  // Misc //
  
  @Override
  protected boolean shouldDespawnInPeaceful() { return true; }

  @Override
  protected boolean canRide(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return GreekFantasy.CONFIG.isWhirlInvulnerable()
        || (!source.isCreativePlayer() && !source.isBypassMagic() && this.limitedLifespan) 
        || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void actuallyHurt(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isBypassMagic() && getAttractMobs()) {
      amount *= 0.25F;
    }
    super.actuallyHurt(source, amount);
  }
  
  // Prevent entity collisions //
  
  @Override
  public boolean isPushable() { return false; }
  
  @Override
  protected void pushEntities() { }
  
  // Lifespan and Attract Mobs //
  
  public void setAttractMobs(final boolean attractsMobs) {
    this.getEntityData().set(ATTRACT_MOBS, attractsMobs);
  }
  
  public boolean getAttractMobs() { return this.getEntityData().get(ATTRACT_MOBS); }

  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }
  
  @Override
  public ResourceLocation getDefaultLootTable() {
    return limitedLifespan ? LootTables.EMPTY : super.getDefaultLootTable();
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putBoolean(KEY_AFFECTS_MOBS, getAttractMobs());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    setAttractMobs(compound.getBoolean(KEY_AFFECTS_MOBS));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
  }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.HOSTILE_SPLASH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected float getVoicePitch() { return 0.8F + random.nextFloat() * 0.2F; }
  
  // Swimming //
  
  @Override
  public void setSwimmingUp(boolean swimmingUp) { this.swimmingUp = swimmingUp; }

  @Override
  public boolean isSwimmingUp() { return swimmingUp; }
  
  @Override
  public void travel(final Vector3d vec) {
    if (isEffectiveAi() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getDeltaMovement());
      setDeltaMovement(getDeltaMovement().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public boolean isPushedByFluid() { return false; }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getTarget();
    return e != null && e.isInWater();
  }

  // Goals //
  
  class SwimToSurfaceGoal extends SwimGoal {

    public SwimToSurfaceGoal() {
      super(WhirlEntity.this);
    }

    @Override
    public boolean canUse() {
      BlockPos pos = WhirlEntity.this.blockPosition().above((int) Math.ceil(WhirlEntity.this.getBbHeight()));
      BlockState state = WhirlEntity.this.level.getBlockState(pos);
      return state.getBlock() == Blocks.WATER && super.canUse();
    }
  }
  
  private static class SwirlGoal extends greekfantasy.entity.ai.SwirlGoal<WhirlEntity> {
  
    public SwirlGoal(final WhirlEntity entity, final int lDuration, final int lCooldown, final double lRange) {
      super(entity, lDuration, lCooldown, lRange, false);
    }

    @Override
    protected void onCollideWith(Entity e) {
      // attack living entities, if enabled
      if(entity.getAttractMobs() && e instanceof LivingEntity) {
        e.hurt(DamageSource.mobAttack(entity), 1.0F);
      }
    }

    @Override
    protected boolean canSwirl(Entity e) {
      return target.test(e) && ((e instanceof LivingEntity && entity.getAttractMobs()) || e instanceof ItemEntity)
          && (!(e instanceof PlayerEntity) || !GreekFantasy.PROXY.getFavorConfiguration().getEnchantmentRange(FavorConfiguration.LORD_OF_THE_SEA_RANGE).isInFavorRange((PlayerEntity)e));
    }
  }
}
