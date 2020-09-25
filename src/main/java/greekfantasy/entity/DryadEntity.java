package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToBlockGoal;
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
import net.minecraft.entity.ai.goal.ResetAngerGoal;
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
  
  private static final DataParameter<Byte> DATA_VARIANT = EntityDataManager.createKey(DryadEntity.class, DataSerializers.BYTE);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_TREE_POS = "Tree";
  
  private Optional<BlockPos> treePos = Optional.empty();
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(8, 19);
  private int angerTime;
  private UUID angerTarget;
  
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
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(2, new HideGoal(600));
    this.goalSelector.addGoal(3, new GoToTreeGoal(0.9F));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, Byte.valueOf((byte) 0));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // if entity has a tree, check if the tree is still there
    if(treePos.isPresent() && !isTreeAt(getEntityWorld(), treePos.get(), this.getVariant().getBlocks())) {
      // if entity was hiding, exit the tree
      if(isHiding()) {
        exitTree();
        // set immune to suffocation for additional amount of time
        hidingImmuneTime = 50;
      }
      // update tree pos
      setTreePos(Optional.empty());
      setHiding(false);
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
    final Vector3d treeVec = treePos.isPresent() ? new Vector3d(treePos.get().getX() + 0.5D, treePos.get().getY() + 1.0D, treePos.get().getZ() + 0.5D) : Vector3d.ZERO;
    final boolean isHidingInTree = isHiding() && treePos.isPresent() && this.getPositionVec().isWithinDistanceOf(treeVec, 2.0D);
    // set clip and gravity values
    if(isHidingInTree) {
      this.noClip = true;
      this.setMotion(this.getMotion().mul(1.0D, 0.0D, 1.0D));
      // snap to the tree's position if close enough
      this.setPosition(treeVec.getX(), treeVec.getY(), treeVec.getZ());
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
    compound.putByte(KEY_VARIANT, this.getDataManager().get(DATA_VARIANT).byteValue());
    this.writeAngerNBT(compound);
    if(treePos.isPresent()) {
      compound.putInt(KEY_TREE_POS + ".x", treePos.get().getX());
      compound.putInt(KEY_TREE_POS + ".y", treePos.get().getY());
      compound.putInt(KEY_TREE_POS + ".z", treePos.get().getZ());
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setVariant(DryadEntity.Variant.getById(compound.getByte(KEY_VARIANT)));
    this.readAngerNBT((ServerWorld)this.world, compound);
    if(compound.contains(KEY_TREE_POS + ".x")) {
      final int x = compound.getInt(KEY_TREE_POS + ".x");
      final int y = compound.getInt(KEY_TREE_POS + ".y");
      final int z = compound.getInt(KEY_TREE_POS + ".z");
      this.setTreePos(Optional.of(new BlockPos(x, y, z)));
    }
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

  public void setVariant(final DryadEntity.Variant variant) { this.getDataManager().set(DATA_VARIANT, variant.getId()); }
  public DryadEntity.Variant getVariant() { return DryadEntity.Variant.getById(this.getDataManager().get(DATA_VARIANT).byteValue()); }
  public Optional<BlockPos> getTreePos() { return treePos; }
  public void setTreePos(final Optional<BlockPos> pos) { treePos = pos; }
  public boolean isHiding() { return hidingTime > 0; }
  public void setHiding(final boolean hiding) { hidingTime = hiding ? 1 : 0; }

  /**
   * Checks if the given position is a dirt or other plant-sustaining
   * block that is underneath a full-size tree.
   * @param worldIn the world
   * @param pos the block pos beneath the tree
   * @param logs the blocks that are considered logs
   * @return if this block pos is supporting a likely tree
   **/
  public static boolean isTreeAt(final IWorldReader worldIn, final BlockPos pos, final ITag<Block> logs) {
    return worldIn.getBlockState(pos).canSustainPlant(worldIn, pos, Direction.UP, (IPlantable)Blocks.OAK_SAPLING)
        && worldIn.getBlockState(pos.up(1)).isIn(logs) && worldIn.getBlockState(pos.up(2)).isIn(logs);
  }
  
  /** 
   * Sets a path away from the tree.
   * @return if a path was set successfully
   **/
  public boolean exitTree() {
    int radius = 2;
    // choose 20 random positions to check
    for (int i = 0; i < 20; i++) {
      double x = this.getPosX() + rand.nextInt(radius * 2) - radius;
      double y = this.getPosY() + rand.nextInt(radius) - radius / 2;
      double z = this.getPosZ() + rand.nextInt(radius * 2) - radius;
      if(this.getNavigator().tryMoveToXYZ(x, y, z, 1.0D)) {
        return true;
      }
    }
    return false;
  }
  
  class HideGoal extends Goal {
    
    private int maxHidingTime;
    
    public HideGoal(final int maxHidingTimeIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
      this.maxHidingTime = maxHidingTimeIn;
    }

    @Override
    public boolean shouldExecute() {
      if(DryadEntity.this.treePos.isPresent()) {
        final Vector3d treeVec = new Vector3d(treePos.get().getX() + 0.5D, treePos.get().getY() + 1.0D, treePos.get().getZ() + 0.5D);
        return DryadEntity.this.getAttackTarget() == null && treeVec.isWithinDistanceOf(DryadEntity.this.getPositionVec(), 1.5D)
            && isTreeAt(DryadEntity.this.getEntityWorld(), DryadEntity.this.treePos.get(), DryadEntity.this.getVariant().getBlocks());
      }
      return false;      
    }
    
    @Override
    public void startExecuting() {
      DryadEntity.this.setHiding(true);
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
      if(DryadEntity.this.hidingTime > 10) {
        // get a path to leave the tree
        DryadEntity.this.exitTree();
        // set immune to suffocation for additional amount of time
        DryadEntity.this.hidingImmuneTime = 50;
      }
      DryadEntity.this.setHiding(false);
    }
  }
  
  class GoToTreeGoal extends GoToBlockGoal {

    public GoToTreeGoal(double speed) {
      super(DryadEntity.this, 10, speed);
    }
    
    @Override
    public boolean shouldExecute() {
      final boolean hasTreePos = DryadEntity.this.treePos.isPresent() 
          && DryadEntity.isTreeAt(DryadEntity.this.getEntityWorld(), DryadEntity.this.treePos.get(), DryadEntity.this.getVariant().getBlocks());
      return !hasTreePos && DryadEntity.this.getAttackTarget() == null
          && DryadEntity.this.getRNG().nextInt(20) == 1 && super.shouldExecute();
    }

    @Override
    public boolean shouldMoveTo(final IWorldReader worldIn, final BlockPos pos) {
     if(isTreeAt(worldIn, pos, DryadEntity.this.getVariant().getBlocks())) {
       // check if this tree is already occupied
       if(DryadEntity.this.getEntityWorld().getEntitiesWithinAABB(DryadEntity.class, new AxisAlignedBB(pos)).isEmpty()) {
         DryadEntity.this.setTreePos(Optional.of(pos));
         return true;
       }
     }
     return false;
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
