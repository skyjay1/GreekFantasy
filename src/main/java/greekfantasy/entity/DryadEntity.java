package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.FindBlockGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

public class DryadEntity extends CreatureEntity implements IAngerable {
  
  private static final DataParameter<String> DATA_VARIANT = EntityDataManager.createKey(DryadEntity.class, DataSerializers.STRING);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_TREE_POS = "Tree";
  private static final String KEY_HIDING = "HidingTime";
  private static final String KEY_IMMUNE = "HidingImmuneTime";
  
  private Optional<BlockPos> treePos = Optional.empty();
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(8, 19);
  private int angerTime;
  private UUID angerTarget;
  
  // whether the entity is pathing to a tree
  private boolean isGoingToTree;
  // number of ticks the entity has been hiding
  private int hidingTime;
  // number of ticks left that the entity is immune to suffocation
  private int hidingImmuneTime;
  
  public DryadEntity(final EntityType<? extends DryadEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(1, new DryadEntity.FindTreeGoal(8, 28));
    this.goalSelector.addGoal(2, new DryadEntity.HideGoal(200));
    this.goalSelector.addGoal(3, new DryadEntity.GoToTreeGoal(0.9F, 200));
    this.goalSelector.addGoal(4, new DryadEntity.WalkingGoal(0.9F, 140));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, DryadEntity.Variant.OAK.getString());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // if entity has a tree position, make sure it still exists
    if(this.ticksExisted % 18 == 0 && this.treePos.isPresent() && !isTreeAt(getEntityWorld(), DryadEntity.this.treePos.get(), DryadEntity.this.getVariant().getBlocks())) {
      // if entity was hiding, exit the tree
      if(this.isHiding()) {
        this.exitTree();
        // set immune to suffocation for additional amount of time
        this.hidingImmuneTime = 50;
      }
      // update tree pos
      // DEBUG
      GreekFantasy.LOGGER.info("No tree found, removing tree pos at " + treePos.get().toString());
      this.setTreePos(Optional.empty());
      this.setHiding(false);
      this.isGoingToTree = false;
    }
    
    // hiding immune timer
    if(!isHiding() && hidingImmuneTime > 0) {
      hidingImmuneTime--;
    }
   
    // anger timer
    if (!this.world.isRemote()) {
      this.func_241359_a_((ServerWorld) this.world, true);
    }
  }

  @Override
  public void move(final MoverType type, final Vector3d vec) {
    super.move(type, vec);
    // copied from Vex code
    doBlockCollisions();
  }

  @Override
  public void tick() {
    // determine how close the entity is to the tree
    final boolean isHidingInTree = isHiding() && this.isWithinDistanceOfTree(2.05D);
    // set clip and gravity values
    if(isHidingInTree) {
      this.noClip = true;
      this.setMotion(this.getMotion().mul(1.0D, 0.0D, 1.0D));
      // snap to the tree's position if close enough
      final Optional<Vector3d> treeVec = getTreeVec();
      this.setPosition(treeVec.get().getX(), treeVec.get().getY(), treeVec.get().getZ());
    }
    // super method
    super.tick();   
    // reset values
    this.setNoGravity(isHidingInTree);
    this.noClip = false;
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    // immune to suffocation while hiding (presumably in a tree)
    if(source == DamageSource.IN_WALL && (isHiding() || hidingImmuneTime > 0)) {
      return true;
    }
    return super.isInvulnerableTo(source);
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putString(KEY_VARIANT, this.getDataManager().get(DATA_VARIANT));
    this.writeAngerNBT(compound);
    if(treePos.isPresent()) {
      compound.putInt(KEY_TREE_POS + ".x", treePos.get().getX());
      compound.putInt(KEY_TREE_POS + ".y", treePos.get().getY());
      compound.putInt(KEY_TREE_POS + ".z", treePos.get().getZ());
    }
    compound.putInt(KEY_HIDING, hidingTime);
    compound.putInt(KEY_IMMUNE, hidingImmuneTime);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setVariant(DryadEntity.Variant.getByName(compound.getString(KEY_VARIANT)));
    this.readAngerNBT((ServerWorld)this.world, compound);
    if(compound.contains(KEY_TREE_POS + ".x")) {
      final int x = compound.getInt(KEY_TREE_POS + ".x");
      final int y = compound.getInt(KEY_TREE_POS + ".y");
      final int z = compound.getInt(KEY_TREE_POS + ".z");
      this.setTreePos(Optional.of(new BlockPos(x, y, z)));
    }
    this.hidingTime = compound.getInt(KEY_HIDING);
    this.hidingImmuneTime = compound.getInt(KEY_IMMUNE);
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final DryadEntity.Variant variant = DryadEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  
  //IAngerable methods
  
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
 
  // End IAngerable methods
  
  @Override
  public float getBrightness() { return 1.0F; }
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return !treePos.isPresent() && this.ticksExisted > 2400; }


  public void setVariant(final DryadEntity.Variant variant) { this.getDataManager().set(DATA_VARIANT, variant.getString()); }
  public boolean isHiding() { return hidingTime > 0; }
  public void setHiding(final boolean hiding) { hidingTime = hiding ? 1 : 0; }public DryadEntity.Variant getVariant() { return DryadEntity.Variant.getByName(this.getDataManager().get(DATA_VARIANT)); }
  public Optional<BlockPos> getTreePos() { return treePos; }
  public Optional<Vector3d> getTreeVec() { return treePos.isPresent() ? Optional.of(new Vector3d(treePos.get().getX() + 0.5D, treePos.get().getY() + 1.0D, treePos.get().getZ() + 0.5D)) : Optional.empty(); }
  public void setTreePos(final Optional<BlockPos> pos) {
    treePos = pos;
    if(pos.isPresent()) {
      this.setHomePosAndDistance(pos.get(), (int)(this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
    } else {
      this.setHomePosAndDistance(BlockPos.ZERO, -1);
    }
  }

  /**
   * Checks if the given position is a dirt or other plant-sustaining
   * block that is underneath a full-size tree.
   * @param worldIn the world
   * @param pos the block pos beneath the tree
   * @param logs the blocks that are considered logs
   * @return if this block pos is supporting a likely tree
   **/
  public static boolean isTreeAt(final IWorldReader worldIn, final BlockPos pos, final ITag<Block> logs) {
    // a "tree" is considered two log blocks on top of a dirt block (or other plant-sustaining block)
    return worldIn.getBlockState(pos).canSustainPlant(worldIn, pos, Direction.UP, (IPlantable)Blocks.OAK_SAPLING)
        && worldIn.getBlockState(pos.up(1)).isIn(logs) && worldIn.getBlockState(pos.up(2)).isIn(logs);
  }
  
  /** 
   * Sets a path away from the tree.
   * @return if a path was set successfully
   **/
  public boolean exitTree() {
    int radius = 2;
    // choose several random positions to check
    for (int i = 0; i < 10; i++) {
      double x = this.getPosX() + rand.nextInt(radius * 2) - radius;
      double y = this.getPosY() + rand.nextInt(radius) - radius / 2;
      double z = this.getPosZ() + rand.nextInt(radius * 2) - radius;
      if(this.getNavigator().tryMoveToXYZ(x, y, z, 1.0D)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @param dis the maximum distance to the tree
   * @return whether the entity is near its tree
   **/
  public boolean isWithinDistanceOfTree(final double dis) {
    final Optional<Vector3d> treeVec = getTreeVec();
    if(!treeVec.isPresent()) {
      return false;
    }
    return treeVec.get().isWithinDistanceOf(getPositionVec(), dis);
  }
  
  class HideGoal extends Goal {
    
    private int maxHidingTime;
    
    public HideGoal(final int maxHidingTimeIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
      this.maxHidingTime = maxHidingTimeIn;
    }

    @Override
    public boolean shouldExecute() {
      if(DryadEntity.this.treePos.isPresent() && DryadEntity.this.isWithinDistanceOfTree(1.5D) && DryadEntity.this.getAttackTarget() == null) {
        return isTreeAt(DryadEntity.this.getEntityWorld(), DryadEntity.this.treePos.get(), DryadEntity.this.getVariant().getBlocks());
      }
      return false;      
    }
    
    @Override
    public void startExecuting() {
      DryadEntity.this.setHiding(true);
      DryadEntity.this.isGoingToTree = false;
    }
    
    @Override
    public void tick() {
      super.tick();
      DryadEntity.this.getNavigator().clearPath();
      DryadEntity.this.hidingTime++;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return (DryadEntity.this.hidingTime < maxHidingTime || DryadEntity.this.getRNG().nextInt(100) > 0) 
          && shouldExecute();
    }
    
    @Override
    public void resetTask() {
      if(DryadEntity.this.isWithinDistanceOfTree(2.0D)) {
        // get a path to leave the tree
        DryadEntity.this.exitTree();
        // set immune to suffocation for additional amount of time
        DryadEntity.this.hidingImmuneTime = 50;
      }
      DryadEntity.this.setHiding(false);
      DryadEntity.this.isGoingToTree = false;
    }
  }
  
  class FindTreeGoal extends FindBlockGoal {

    public FindTreeGoal(int radius, int cooldown) {
      super(DryadEntity.this, radius, cooldown);
    }
    
    @Override
    public boolean shouldExecute() {
      return !DryadEntity.this.getTreePos().isPresent() && super.shouldExecute();
    }
    
    @Override
    public boolean isTargetBlock(IWorldReader worldIn, BlockPos pos) {
      // valid block if there is a tree here and it has not been occupied by another dryad
      return isTreeAt(worldIn, pos, DryadEntity.this.getVariant().getBlocks()) 
          && DryadEntity.this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(DryadEntity.this, new AxisAlignedBB(pos)).isEmpty();
    }

    @Override
    public void onFoundBlock(final IWorldReader worldIn, final BlockPos target) {
      DryadEntity.this.setTreePos(Optional.of(target));
      // DEBUG:
      GreekFantasy.LOGGER.info("Dryad found tree at " + target.toString());
    }
  }
  
  class GoToTreeGoal extends Goal {
    
    private final double speed;
    private final int chance;
    
    public GoToTreeGoal(final double speedIn, final int chanceIn) {
      setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      speed = speedIn;
      chance = chanceIn;
    }

    @Override
    public boolean shouldExecute() {
      return !DryadEntity.this.isHiding() && DryadEntity.this.getTreePos().isPresent() 
            && DryadEntity.this.getAttackTarget() == null && DryadEntity.this.getRNG().nextInt(chance) == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
      return DryadEntity.this.isGoingToTree && DryadEntity.this.getTreePos().isPresent() 
          && !DryadEntity.this.getNavigator().noPath() && DryadEntity.this.getAttackTarget() == null ;
    }

    @Override
    public void startExecuting() {
      DryadEntity.this.isGoingToTree = true;
      final Optional<Vector3d> vec = DryadEntity.this.getTreeVec();
      DryadEntity.this.getNavigator().tryMoveToXYZ(vec.get().getX(), vec.get().getY(), vec.get().getZ(), this.speed);
      // DEBUG:
      GreekFantasy.LOGGER.info("Dryad trying to go to tree at " + vec.toString());
    }

    @Override
    public void resetTask() {
      DryadEntity.this.getNavigator().clearPath();
      DryadEntity.this.isGoingToTree = false;
    }
  }
  
  class WalkingGoal extends RandomWalkingGoal {

    public WalkingGoal(double speed, final int chance) {
      super(DryadEntity.this, speed, chance);
    }
    
    @Override
    public boolean shouldExecute() {
      return !DryadEntity.this.isHiding() && !DryadEntity.this.isGoingToTree && DryadEntity.this.getAttackTarget() == null
           && super.shouldExecute();
    }
  }
  
  public static enum Variant implements IStringSerializable {
    ACACIA(0, "acacia", () -> Blocks.ACACIA_SAPLING),
    BIRCH(1, "birch", () -> Blocks.BIRCH_SAPLING),
    DARK_OAK(2, "dark_oak", () -> Blocks.DARK_OAK_SAPLING),
    JUNGLE(3, "jungle", () -> Blocks.JUNGLE_SAPLING),
    OAK(4, "oak", () -> Blocks.OAK_SAPLING),
    SPRUCE(5, "spruce", () -> Blocks.SPRUCE_SAPLING);
    
    private final int id;
    private final String name;
    private final Supplier<Block> sapling;
    private final ResourceLocation tag;
    private final ResourceLocation texture;
    
    private Variant(final int idIn, final String nameIn, final Supplier<Block> saplingIn) {
      id = idIn;
      name = nameIn;
      sapling = saplingIn;
      tag = new ResourceLocation("minecraft", name + "_logs");
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/" + name + ".png");
    }
    
    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      final String biomeName = biome.isPresent() ? biome.get().getRegistryName().getPath() : "";
      if(biomeName.contains("birch")) {
        return BIRCH;
      }
      if(biomeName.contains("dark_forest")) {
        return DARK_OAK;
      }
      if(biomeName.contains("taiga")) {
        return SPRUCE;
      }
      if(biomeName.contains("jungle")) {
        return JUNGLE;
      }
      if(biomeName.contains("savanna")) {
        return ACACIA;
      }
      return OAK;
    }

    public static Variant getById(final byte i) {
      for (final Variant v : values()) {
        if (v.getId() == i) {
          return v;
        }
      }
      return OAK;
    }

    public static Variant getByName(final String n) {
      // check the given name against all types
      if(n != null && !n.isEmpty()) {
        for(final Variant t : values()) {
          if(t.getString().equals(n)) {
            return t;
          }
        }
      }
      // defaults to OAK
      return OAK;
    }
    
    public ResourceLocation getTexture() {
      return texture;
    }
    
    public ITag<Block> getBlocks() {
      return Optional.ofNullable(BlockTags.getCollection().get(tag)).orElse(BlockTags.LOGS);
    }
    
    public BlockState getSapling() {
      return sapling.get().getDefaultState();
    }
  
    public byte getId() {
      return (byte) this.id;
    }

    @Override
    public String getString() {
      return name;
    }
  }

}
