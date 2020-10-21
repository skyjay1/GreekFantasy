package greekfantasy.entity;

import java.util.EnumSet;
import java.util.function.Supplier;

import greekfantasy.GFRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElpisEntity extends CreatureEntity implements IFlyingAnimal {
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(ElpisEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "ElpisState";
  private static final String KEY_HOME = "ElpisHome";
  private static final String KEY_AGE = "ElpisAge";
  private static final String KEY_DESPAWN_TIMER = "DespawnTimer";
  
  public static final int wanderDistance = 8;
  private static final int maxAge = 4800;
  private static final int maxDespawnTime = 40;
  
  // byte flags to use with the STATE data parameter
  protected static final byte STATE_NONE = 5;
  protected static final byte STATE_TRADING = 6;
  protected static final byte STATE_DESPAWNING = 7;
  
  private int despawnTime;
  private int age;
  
  public ElpisEntity(final EntityType<? extends CreatureEntity> type, final World world) {
    super(type, world);
    this.moveController = new FlyingMovementController(this, 20, true);
    this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
    this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.5D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 0.4D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(STATE_NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new ElpisEntity.DoNothingGoal());
    this.goalSelector.addGoal(2, new ElpisEntity.TradeGoal(() -> new ItemStack(GFRegistry.ICHOR), 80));
    this.goalSelector.addGoal(3, new ElpisEntity.PanicGoal(1.0D));
    this.goalSelector.addGoal(4, new ElpisEntity.MoveRandomGoal(20, wanderDistance, 0.75D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    if (this.world.isRemote() && rand.nextInt(12) == 0) {
      spawnParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, true);
    }
    // update age
    ++age;
    // update despawn time
    if(despawnTime > 0) {
      if(despawnTime > (maxDespawnTime / 2) && rand.nextInt(3) == 0) {
        spawnParticle(ParticleTypes.PORTAL, false);
      }
      if(despawnTime++ >= maxDespawnTime) {
        remove();
      }
    }
    // spawn particles when trading
    if (this.isTrading() && rand.nextInt(5) == 0) {
      spawnParticle(ParticleTypes.HAPPY_VILLAGER, false);
    }
  }

  @Override
  public void tick() {
    super.tick();
    setNoGravity(true);
  }
  
  @Override
  protected ActionResultType func_230254_b_(final PlayerEntity player, final Hand hand) { // processInteract
    ItemStack stack = player.getHeldItem(hand);
    if(stack.getItem() == Items.HONEY_BOTTLE) {
      this.setState(STATE_TRADING);
      // reduce stack size
      if(!player.isCreative()) {
        stack.shrink(1);
      }
      player.setHeldItem(hand, stack);
      return ActionResultType.CONSUME;
    }
    
    return super.func_230254_b_(player, hand);
  }
  
  @Override
  public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
    return worldIn.isAirBlock(pos) ? 10.0F : 0.0F;
  }

  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) {
    return false;
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
  }
  
  @Override
  protected PathNavigator createNavigator(World worldIn) {
    FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
    flyingpathnavigator.setCanOpenDoors(false);
    flyingpathnavigator.setCanSwim(false);
    flyingpathnavigator.setCanEnterDoors(true);
    return flyingpathnavigator;
  }

  @Override
  public SoundCategory getSoundCategory() {
    return SoundCategory.NEUTRAL;
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_STATE, this.getDataManager().get(STATE).byteValue());
    compound.putInt(KEY_AGE, age);
    compound.putInt(KEY_DESPAWN_TIMER, despawnTime);
    if(this.getHomePosition() != BlockPos.ZERO && this.getMaximumHomeDistance() > -1.0F) {
      compound.putInt(KEY_HOME + ".x", getHomePosition().getX());
      compound.putInt(KEY_HOME + ".y", getHomePosition().getY());
      compound.putInt(KEY_HOME + ".z", getHomePosition().getZ());
    }
  }
  
  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.getDataManager().set(STATE, compound.getByte(KEY_STATE));
    age = compound.getInt(KEY_AGE);
    despawnTime = compound.getInt(KEY_DESPAWN_TIMER);
    if(compound.contains(KEY_HOME + ".x")) {
      final int x = compound.getInt(KEY_HOME + ".x");
      final int y = compound.getInt(KEY_HOME + ".y");
      final int z = compound.getInt(KEY_HOME + ".z");
      this.setHomePosAndDistance(new BlockPos(x, y, z), wanderDistance);
    }
  }
  
  @Override
  public boolean canDespawn(final double disToPlayer) {
    return this.age > maxAge && disToPlayer > 8.0D;
  }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == STATE && world.isRemote()) {
      if(isDespawning()) {
        despawnTime = 1;
      }
    }
  }
 
  public BlockPos getWanderCenter() {
    final BlockPos home = this.getHomePosition();
    return this.getMaximumHomeDistance() > -1.0F && home != BlockPos.ZERO ? home : this.getPosition();
  }
  
  protected void spawnParticle(final IParticleData particle, final boolean colored) {
    if(world.isRemote()) {
      final double motion = 0.09D;
      final double radius = 0.25D;
      world.addParticle(particle, 
          this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
          this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
          this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
          colored ? 1.0F : (world.rand.nextDouble() - 0.5D) * motion, 
          colored ? 0.60F : (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
          colored ? 0.92F : (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  // state methods
  
  public void setState(final byte state) { this.getDataManager().set(STATE, state); }
  
  public byte getState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public boolean isTrading() { return getState() == STATE_TRADING; }
  
  public boolean isDespawning() { return getState() == STATE_DESPAWNING; }
  
  // Trading goal
  
  class TradeGoal extends Goal {
    
    final Supplier<ItemStack> result;
    final int duration;
    PlayerEntity player;
    int progress;
    
    public TradeGoal(final Supplier<ItemStack> resultStack, final int durationIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      result = resultStack;
      duration = durationIn;
    }

    @Override
    public boolean shouldExecute() {
      player = ElpisEntity.this.world.getClosestPlayer(ElpisEntity.this, 8.0D);
      return player != null && ElpisEntity.this.isTrading();
    }
    
    @Override
    public void tick() {
      ElpisEntity.this.getLookController().setLookPositionWithEntity(player, ElpisEntity.this.getHorizontalFaceSpeed(), ElpisEntity.this.getVerticalFaceSpeed());
      ElpisEntity.this.getNavigator().clearPath();
      if(progress++ >= duration) {
        // finish trading and spawn an item
        final ItemEntity item = new ItemEntity(ElpisEntity.this.world, ElpisEntity.this.getPosX(), 
            ElpisEntity.this.getPosY(), ElpisEntity.this.getPosZ(), result.get());
        ElpisEntity.this.getEntityWorld().addEntity(item);
        ElpisEntity.this.playSound(SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, 0.8F, 1.0F);
        // start despawning
        ElpisEntity.this.despawnTime = 1;
        ElpisEntity.this.setState(STATE_DESPAWNING);
      }
    }
    
    @Override
    public void resetTask() {
      ElpisEntity.this.setState(STATE_NONE);
      progress = 0;
    }    
  }
  
  // Despawning goal
  
  class DoNothingGoal extends Goal {
    public DoNothingGoal() { this.setMutexFlags(EnumSet.allOf(Goal.Flag.class)); }
    @Override
    public boolean shouldExecute() { return ElpisEntity.this.isDespawning(); }
  }
  
  // TODO not doing anything...
  class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {

    public PanicGoal(double speed) {
      super(ElpisEntity.this, speed);
    }
    
  }

  class MoveRandomGoal extends Goal {
    
    private final int chance;
    private final int radius;
    private final double speed;
    
    public MoveRandomGoal(final int chanceIn, final int radiusIn, final double speedIn) {
      setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      chance = chanceIn;
      radius = radiusIn;
      speed = speedIn;
    }

    @Override
    public boolean shouldExecute() {
      return (ElpisEntity.this.getNavigator().noPath() && ElpisEntity.this.rand.nextInt(chance) == 0);
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void tick() {
      BlockPos pos = ElpisEntity.this.getWanderCenter();

      for (int checks = 0; checks < 3; checks++) {
        BlockPos posCheck = pos.add(ElpisEntity.this.rand.nextInt(radius * 2) - radius, ElpisEntity.this.rand.nextInt(radius) - (radius / 2), ElpisEntity.this.rand.nextInt(radius * 2) - radius);
        if (ElpisEntity.this.world.isAirBlock(posCheck)) {
          ElpisEntity.this.getNavigator().tryMoveToXYZ(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
          //ElpisEntity.this.moveController.setMoveTo(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
          if (ElpisEntity.this.getAttackTarget() == null) {
            ElpisEntity.this.getLookController().setLookPosition(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, 180.0F, 20.0F);
          }
          break;
        }
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  public float getAlpha(final float partialTick) {
    final byte state = this.getState();
    switch(state) {
    case STATE_TRADING: return 1.0F;
    case STATE_DESPAWNING: return 1.0F - getDespawnPercent(partialTick);
    default:
      final float minAlpha = 0.18F;
      final float cosAlpha = 0.5F + 0.5F * MathHelper.cos((this.getEntityId() + this.ticksExisted + partialTick) * 0.025F);
      return MathHelper.clamp(cosAlpha, minAlpha, 1.0F);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getDespawnPercent(final float partialTick) {
    return (float)despawnTime / (float)maxDespawnTime;
  }

}
