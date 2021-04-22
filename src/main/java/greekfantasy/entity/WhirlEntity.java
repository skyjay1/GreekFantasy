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
  
  protected static final DataParameter<Boolean> ATTRACT_MOBS = EntityDataManager.createKey(WhirlEntity.class, DataSerializers.BOOLEAN);
  protected static final String KEY_AFFECTS_MOBS = "AttractMobs";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";

  private static final double RANGE = 9.0D;
  
  private boolean swimmingUp;
  
  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  public WhirlEntity(final EntityType<? extends WhirlEntity> type, final World worldIn) {
    super(type, worldIn);
    this.experienceValue = 5;
  }

  //copied from DolphinEntity
  public static boolean canWhirlSpawnOn(final EntityType<? extends WaterMobEntity> entity, final IWorld world,
      final SpawnReason reason, final BlockPos pos, final Random rand) {
    if (pos.getY() <= 25 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }

    RegistryKey<Biome> biome = world.func_242406_i(pos).orElse(Biomes.PLAINS);
    return (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) && world.getFluidState(pos).isTagged(FluidTags.WATER);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.25D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(ATTRACT_MOBS, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new WhirlEntity.SwimToSurfaceGoal());
    this.goalSelector.addGoal(2, new WhirlEntity.SwirlGoal(this, 1000, 0, RANGE));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // remove if colliding with another whirl or a charybdis
    final List<WaterMobEntity> waterMobList = this.world.getEntitiesWithinAABB(WaterMobEntity.class, this.getBoundingBox().grow(1.0D), 
        e -> e != this && e.isAlive() && (e.getType() == GFRegistry.CHARYBDIS_ENTITY || e.getType() == GFRegistry.WHIRL_ENTITY));
    if(!waterMobList.isEmpty() && this.isAlive()) {
      this.attackEntityFrom(DamageSource.STARVE, this.getMaxHealth() * 2.0F);
      return;
    }
 
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 20;
      this.setHealth(this.getHealth() - 1.0F);
    }
    
    // attract nearby items
    final List<ItemEntity> itemEntityList = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox().grow(1.0), e -> e.isInWaterOrBubbleColumn() && (this.getPosY() + this.getHeight()) > e.getPosY());
    for(final ItemEntity e : itemEntityList) {
      // check for trigger items
      if(this.getEntityWorld() instanceof ServerWorld && !e.getItem().isEmpty() && e.getItem().getItem().isIn(TRIGGER)) {
        CharybdisEntity.spawnCharybdis((ServerWorld) this.getEntityWorld(), this);
        e.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
      }
      // start to remove items
      if(!e.cannotPickup()) {
        e.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
      }
      // play sound when item is removed
      if(!e.isAlive()) {
        this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.6F, 0.8F + this.getRNG().nextFloat() * 0.4F);
      }
    }
    
    // spawn particles
    if(this.world.isRemote() && ticksExisted % 3 == 0 && this.isInWaterOrBubbleColumn()) {
      // spawn particles in spiral
      float maxY = this.getHeight() * 1.65F;
      float y = 0;
      float nY = 90;
      float dY = maxY / nY;
      double posX = this.getPosX();
      double posY = this.getPosY();
      double posZ = this.getPosZ();
      // for each y-position, increase the angle and spawn particle here
      for(float a = 0, nA = 28 + rand.nextInt(4), dA = (2 * (float)Math.PI) / nA; y < maxY; a += dA) {
        float radius = y * 0.5F;
        float cosA = MathHelper.cos(a) * radius;
        float sinA = MathHelper.sin(a) * radius;
        //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
        world.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
        y += dY;
      }
    }
  }
  
  @Override
  protected void collideWithEntity(final Entity entityIn) {
    super.collideWithEntity(entityIn);
  }

  // Misc //
  
  @Override
  protected boolean isDespawnPeaceful() { return true; }

  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return GreekFantasy.CONFIG.isWhirlInvulnerable()
        || (!source.isCreativePlayer() && !source.isDamageAbsolute() && this.limitedLifespan) 
        || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isDamageAbsolute() && getAttractMobs()) {
      amount *= 0.25F;
    }
    super.damageEntity(source, amount);
  }
  
  // Prevent entity collisions //
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  protected void collideWithNearbyEntities() { }
  
  // Lifespan and Attract Mobs //
  
  public void setAttractMobs(final boolean attractsMobs) {
    this.getDataManager().set(ATTRACT_MOBS, attractsMobs);
  }
  
  public boolean getAttractMobs() { return this.getDataManager().get(ATTRACT_MOBS); }

  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }
  
  @Override
  public ResourceLocation getLootTable() {
    return limitedLifespan ? LootTables.EMPTY : super.getLootTable();
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_AFFECTS_MOBS, getAttractMobs());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setAttractMobs(compound.getBoolean(KEY_AFFECTS_MOBS));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
  }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_HOSTILE_SPLASH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected float getSoundPitch() { return 0.8F + rand.nextFloat() * 0.2F; }
  
  // Swimming //
  
  @Override
  public void setSwimmingUp(boolean swimmingUp) { this.swimmingUp = swimmingUp; }

  @Override
  public boolean isSwimmingUp() { return swimmingUp; }
  
  @Override
  public void travel(final Vector3d vec) {
    if (isServerWorld() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getMotion());
      setMotion(getMotion().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public boolean isPushedByWater() { return false; }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getAttackTarget();
    return e != null && e.isInWater();
  }

  // Goals //
  
  class SwimToSurfaceGoal extends SwimGoal {

    public SwimToSurfaceGoal() {
      super(WhirlEntity.this);
    }

    @Override
    public boolean shouldExecute() {
      BlockPos pos = WhirlEntity.this.getPosition().up((int) Math.ceil(WhirlEntity.this.getHeight()));
      BlockState state = WhirlEntity.this.world.getBlockState(pos);
      return state.getBlock() == Blocks.WATER && super.shouldExecute();
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
        e.attackEntityFrom(DamageSource.causeMobDamage(entity), 1.0F);
      }
    }

    @Override
    protected boolean canSwirl(Entity e) {
      return target.test(e) && ((e instanceof LivingEntity && entity.getAttractMobs()) || e instanceof ItemEntity)
          && (!(e instanceof PlayerEntity) || !GreekFantasy.PROXY.getFavorConfiguration().getEnchantmentRange(FavorConfiguration.LORD_OF_THE_SEA_RANGE).isInFavorRange((PlayerEntity)e));
    }
  }
}
