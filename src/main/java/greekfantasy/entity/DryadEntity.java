package greekfantasy.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.EffectGoal;
import greekfantasy.entity.ai.FavorableResetTargetGoal;
import greekfantasy.entity.ai.FindBlockGoal;
import greekfantasy.entity.misc.IFavorable;
import greekfantasy.favor.Deity;
import greekfantasy.util.BiomeHelper;
import greekfantasy.util.FavorRange;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
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
import net.minecraft.potion.Effects;
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

public class DryadEntity extends CreatureEntity implements IAngerable, IFavorable {
  
  private static final DataParameter<String> DATA_VARIANT = EntityDataManager.createKey(DryadEntity.class, DataSerializers.STRING);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_TREE_POS = "Tree";
  private static final String KEY_HIDING = "HidingTime";
  
  private static final Map<String, FavorRange> FAVOR_RANGE_MAP = new HashMap<>();
  static {
    FAVOR_RANGE_MAP.put(CAN_ATTACK, new FavorRange(Deity.ZEUS, -10, -2));
  }
  
  private Optional<BlockPos> treePos = Optional.empty();
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(4, 10);
  private int angerTime;
  private UUID angerTarget;
  
  // whether the entity is pathing to a tree
  private boolean isGoingToTree;
  // number of ticks the entity has been hiding
  private int hidingTime;
  
  public DryadEntity(final EntityType<? extends DryadEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(1, new DryadEntity.FindTreeGoal(8, 28));
    this.goalSelector.addGoal(2, new DryadEntity.HideGoal(640));
    this.goalSelector.addGoal(3, new DryadEntity.GoToTreeGoal(0.9F, 320));
    this.goalSelector.addGoal(4, new EffectGoal<>(this, () -> Effects.REGENERATION, 60, 120, 0, 1, 
        EffectGoal.randomPredicate(400).and(e -> ((DryadEntity)e).isHiding())));
    this.goalSelector.addGoal(4, new DryadEntity.WalkingGoal(0.8F, 140));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::targetFavorAttackable));
    this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
    this.targetSelector.addGoal(3, new FavorableResetTargetGoal<>(this));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, DryadEntity.Variant.OAK.getString());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // if entity has a tree position, check if it no longer exists
    if(this.ticksExisted % 28 == 0 && treePos.isPresent() && !isTreeAt(getEntityWorld(), treePos.get(), getVariant().getBlocks())) {
      // if entity was hiding, exit the tree
      this.tryExitTree();
      // update tree pos
      this.setTreePos(Optional.empty());
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
    final boolean isHidingInTree = isHiding() && this.isWithinDistanceOfTree(2.05D) && getNavigator().noPath();
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
    if(source == DamageSource.IN_WALL) {
      return !this.world.getBlockState(this.getPosition().up()).isIn(this.getVariant().getBlocks());
    }
    return super.isInvulnerableTo(source);
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // reset anger
      if(entity.getUniqueID().equals(this.getAngerTarget())) {
        this.setAngerTarget(null);
      }
      return true;
    }
    return false;
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
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final DryadEntity.Variant variant;
    if(reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
      variant = DryadEntity.Variant.getRandom(worldIn.getRandom());
    } else {
      variant = DryadEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    }
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  public ResourceLocation getLootTable() {
    return this.getVariant().getLootTable();
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
 
  // IFavorable methods
  @Override
  public Map<String, FavorRange> getFavorRangeMap() { return FAVOR_RANGE_MAP; }
  
  @Override
  public float getBrightness() { return 1.0F; }
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return !treePos.isPresent() && this.ticksExisted > 4800; }

  // Variant methods

  public void setVariant(final DryadEntity.Variant variant) { this.getDataManager().set(DATA_VARIANT, variant.getString()); }
  
  public boolean isHiding() { return hidingTime > 0; }
  
  public void setHiding(final boolean hiding) { hidingTime = hiding ? 1 : 0; }
  
  public DryadEntity.Variant getVariant() { return DryadEntity.Variant.getByName(this.getDataManager().get(DATA_VARIANT)); }
  
  // Tree methods
  
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
   * Attempt to exit the tree, if the entity
   * is inside a tree. Also resets hiding values.
   * @return if a path was set successfully
   **/
  public boolean tryExitTree() {
    this.isGoingToTree = false;
    this.setHiding(false);
    if(this.treePos.isPresent() && this.isWithinDistanceOfTree(2.0D)) {
      if(this.getNavigator().noPath()) {
        // choose several random positions to check
        int radius = 2;
        for (int i = 0; i < 10; i++) {
          double x = this.getPosX() + rand.nextInt(radius * 2) - radius;
          double y = this.getPosY() + rand.nextInt(radius) - radius / 2;
          double z = this.getPosZ() + rand.nextInt(radius * 2) - radius;
          // try to path to the position
          if(this.getNavigator().tryMoveToXYZ(x, y, z, 1.0D)) {
            return true;
          }
        }
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
    
    private final int maxHidingTime;
    private final int maxCooldown;
    private int cooldown;
    
    public HideGoal(final int maxHidingTimeIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
      this.maxHidingTime = maxHidingTimeIn;
      this.maxCooldown = 120;
    }

    @Override
    public boolean shouldExecute() {
      if(cooldown > 0) {
        cooldown--;
      } else if(DryadEntity.this.treePos.isPresent() && DryadEntity.this.isWithinDistanceOfTree(1.5D) && DryadEntity.this.getAttackTarget() == null) {
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
      if(DryadEntity.this.hidingTime++ > maxHidingTime && DryadEntity.this.getRNG().nextInt(100) == 0) {
        resetTask();
      }
    }
    
    @Override
    public void resetTask() {
      DryadEntity.this.tryExitTree();
      cooldown = maxCooldown;
    }
  }
  
  class FindTreeGoal extends FindBlockGoal {

    public FindTreeGoal(int radius, int cooldown) {
      super(DryadEntity.this, radius, cooldown);
    }
    
    @Override
    public boolean shouldExecute() {
      return (!DryadEntity.this.getTreePos().isPresent() || DryadEntity.this.getRNG().nextInt(500) == 0) && super.shouldExecute();
    }
    
    @Override
    public boolean isTargetBlock(IWorldReader worldIn, BlockPos pos) {
      // valid block if there is a tree here and it has not been occupied by another dryad
      return isTreeAt(worldIn, pos, DryadEntity.this.getVariant().getBlocks()) 
          && DryadEntity.this.getEntityWorld().getEntitiesWithinAABB(DryadEntity.class, new AxisAlignedBB(pos.up()).grow(0.5D)).isEmpty();
    }

    @Override
    public void onFoundBlock(final IWorldReader worldIn, final BlockPos target) {
      DryadEntity.this.setTreePos(Optional.of(target));
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
    ACACIA("acacia", () -> Blocks.ACACIA_SAPLING),
    BIRCH("birch", () -> Blocks.BIRCH_SAPLING),
    DARK_OAK("dark_oak", () -> Blocks.DARK_OAK_SAPLING),
    JUNGLE("jungle", () -> Blocks.JUNGLE_SAPLING),
    OAK("oak", () -> Blocks.OAK_SAPLING),
    SPRUCE("spruce", () -> Blocks.SPRUCE_SAPLING),
    OLIVE(GreekFantasy.MODID, "olive", () -> GFRegistry.OLIVE_SAPLING);
    
    private final String name;
    private final Supplier<Block> sapling;
    private final ResourceLocation tag;
    private final ResourceLocation lootTable;
    
    private Variant(final String nameIn, final Supplier<Block> saplingIn) {
      this("minecraft", nameIn, saplingIn);
    }
    
    private Variant(final String modid, final String nameIn, final Supplier<Block> saplingIn) {
      name = nameIn;
      sapling = saplingIn;
      tag = new ResourceLocation(modid, name + "_logs");
      lootTable = new ResourceLocation(GreekFantasy.MODID, "entities/dryad/" + name);
    }
    
    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      return BiomeHelper.getDryadVariantForBiome(biome);
    }
    
    public static Variant getRandom(final Random rand) {
      int len = values().length;
      return values()[rand.nextInt(len)];
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
    
    public ITag<Block> getBlocks() {
      return Optional.ofNullable(BlockTags.getCollection().get(tag)).orElse(BlockTags.LOGS);
    }
    
    public BlockState getSapling() {
      return sapling.get().getDefaultState();
    }
    
    public ResourceLocation getLootTable() {
      return lootTable;
    }
  
    @Override
    public String getString() {
      return name;
    }
  }
}
