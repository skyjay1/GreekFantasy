package greekfantasy.entity;

import java.util.List;
import java.util.Random;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
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
import net.minecraft.item.Item;
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

  private static final double RANGE = 9.0D;
  
  private boolean swimmingUp;
  
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
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new WhirlEntity.SwimToSurfaceGoal());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // remove if colliding with another whirl or a charybdis
    final List<WaterMobEntity> waterMobList = this.world.getEntitiesWithinAABB(WaterMobEntity.class, this.getBoundingBox().grow(1.0D), 
        e -> e != this && e.isAlive() && (e.getType() == GFRegistry.CHARYBDIS_ENTITY || e.getType() == GFRegistry.WHIRL_ENTITY));
    if(!waterMobList.isEmpty()) {
      this.attackEntityFrom(DamageSource.causeMobDamage(this), this.getMaxHealth() * 2.0F);
      return;
    }
    // check for trigger items
    final List<ItemEntity> triggerList = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox(), e -> e.isInWaterOrBubbleColumn() && !e.getItem().isEmpty() && e.getItem().getItem().isIn(TRIGGER));
    if(this.getEntityWorld() instanceof ServerWorld && !triggerList.isEmpty()) {
      CharybdisEntity.spawnCharybdis((ServerWorld) this.getEntityWorld(), this);
      return;
    }
    // attract nearby items
    final List<ItemEntity> itemEntityList = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox().grow(RANGE), e -> e.isInWaterOrBubbleColumn() && (this.getPosY() + this.getHeight()) > e.getPosY());
    for(final ItemEntity e : itemEntityList) {
      final double motion = 0.07D;
      final Vector3d vec = this.getPositionVec().subtract(e.getPositionVec())
          .normalize().scale(motion);
      e.setMotion(e.getMotion().add(vec).mul(0.5D, 1.0D, 0.5D));
      e.addVelocity(0, 0.001D, 0);
      e.velocityChanged = true;
      if(e.getBoundingBox().intersects(this.getBoundingBox())) {
        e.attackEntityFrom(DamageSource.causeMobDamage(this), 0.25F);
        if(!e.isAlive()) {
          this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.6F, 0.8F + this.getRNG().nextFloat() * 0.4F);
        }
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
  protected boolean isDespawnPeaceful() {
    return true;
  }

  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return GreekFantasy.CONFIG.isWhirlInvulnerable() || super.isInvulnerableTo(source);
  }
  
  // Prevent entity collisions //
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  protected void collideWithNearbyEntities() { }
  
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
}
