package greekfantasy.tileentity;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.deity.Deity;
import greekfantasy.deity.IDeity;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatueTileEntity extends TileEntity implements IClearable, IInventory {

  private static final String KEY_POSE = "Pose";
  private static final String KEY_UPPER = "Upper";
  private static final String KEY_FEMALE = "Female";
  private static final String KEY_NAME = "Name";
  private static final String KEY_DEITY = "Deity";

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

  private StatuePose statuePose = StatuePoses.NONE;
  private boolean upper = false;
  private boolean statueFemale = false;
  private String textureName = "";
  private String deityName = "";
  
  @Nullable
  private GameProfile playerProfile = null;
  @Nullable
  private IDeity deity = Deity.EMPTY;
  
  public StatueTileEntity() {
    this(GFRegistry.STATUE_TE);
  }
  
  public StatueTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }
  
  public void setUpper(final boolean isUpper) {
    this.upper = isUpper;
    this.markDirty();
  }
  
  public boolean isUpper() { return this.upper; }

  public StatuePose getStatuePose() { return this.statuePose; }
  
  public void setStatuePose(final StatuePose poseIn) { setStatuePose(poseIn, false); }
  
  public void setStatuePose(final StatuePose poseIn, final boolean refresh) {
    this.statuePose = poseIn;
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }

  public Vector3f getRotations(final ModelPart part) {
    return statuePose.getAngles(part);
  }
  
  public StatueMaterial getStatueMaterial() {
    if (this.getBlockState() != null && this.getBlockState().getBlock() instanceof StatueBlock) {
      return ((StatueBlock) this.getBlockState().getBlock()).getStatueMaterial();
    }
    return StatueMaterial.LIMESTONE;
  }
  
  // Is Female //

  public boolean isStatueFemale() { return statueFemale; }
  
  public void setStatueFemale(boolean statueFemaleIn) { setStatueFemale(statueFemaleIn, false); }

  public void setStatueFemale(boolean statueFemaleIn, final boolean refresh) {
    if(statueFemaleIn == this.statueFemale) {
      return;
    }
    this.statueFemale = statueFemaleIn;
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  // Texture Name //
  
  public String getTextureName() { return textureName; }

  public void setTextureName(final String nameIn) { setTextureName(nameIn, false); }
  
  public void setTextureName(final String nameIn, final boolean refresh) {
    if(nameIn.equals(this.textureName)) {
      return;
    }
    this.textureName = nameIn;
    final CompoundNBT profileNBT = new CompoundNBT();
    profileNBT.putString("Name", nameIn);
    setPlayerProfile(NBTUtil.readGameProfile(profileNBT));
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  // Deity Name //
  
  public String getDeityName() { return deityName; }
  
  public void setDeityName(final String deityIn) { setDeityName(deityIn, false); }
  
  public void setDeityName(final String deityIn, final boolean refresh) {
    this.deityName = deityIn;
    this.updateDeity();
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  // Deity //
  
  public IDeity getDeity() { return deity; }
  
  public boolean hasDeity() { return deity != null && deity != Deity.EMPTY; }

  public void updateDeity() {
    this.deity = deityName.isEmpty() ? Deity.EMPTY : GreekFantasy.PROXY.DEITY.get(new ResourceLocation(deityName)).orElse(Deity.EMPTY);
    this.markDirty();
  }
  
  // Player Profile //
  
  @Nullable
  public GameProfile getPlayerProfile() { return this.playerProfile; }
  
  private void setPlayerProfile(@Nullable GameProfile profile) {
    this.playerProfile = profile;
    this.updatePlayerProfile();
  }

  private void updatePlayerProfile() {
    this.playerProfile = SkullTileEntity.updateGameProfile(this.playerProfile);
    this.markDirty();
  }
  
  // NBT AND SAVING STUFF //

  @Override
  public CompoundNBT getUpdateTag() {
    return buildUpdateTag(super.getUpdateTag());
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    this.readUpdateTag(tag);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), -1, buildUpdateTag(new CompoundNBT()));
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    readUpdateTag(pkt.getNbtCompound());
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    readUpdateTag(nbt);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    return buildUpdateTag(super.write(compound));
  }

  public CompoundNBT buildUpdateTag(final CompoundNBT nbt) {
    nbt.putBoolean(KEY_UPPER, this.upper);
    nbt.putBoolean(KEY_FEMALE, statueFemale);
    nbt.put(KEY_POSE, this.statuePose.serializeNBT(new CompoundNBT()));
    nbt.put(KEY_NAME, StringNBT.valueOf(textureName));
    nbt.put(KEY_DEITY, StringNBT.valueOf(deityName));
    ItemStackHelper.saveAllItems(nbt, this.inventory, true);
    return nbt;
  }

  public void readUpdateTag(final CompoundNBT nbt) {
    this.setUpper(nbt.getBoolean(KEY_UPPER));
    this.setStatueFemale(nbt.getBoolean(KEY_FEMALE));
    this.setStatuePose(new StatuePose(nbt.getCompound(KEY_POSE)));
    this.setTextureName(nbt.getString(KEY_NAME));
    this.setDeityName(nbt.getString(KEY_DEITY));
    this.inventory.clear();
    ItemStackHelper.loadAllItems(nbt, this.inventory);
  }

  // INVENTORY //
  
  public void setItem(final ItemStack stack, final HandSide hand) {
    int i = hand.ordinal();
    this.inventory.set(i, stack.split(1));
    this.inventoryChanged();
  }
  
  public ItemStack getItem(final HandSide hand) {
    return this.inventory.get(hand.ordinal());
  }
  
  public NonNullList<ItemStack> getInventory() {
     return this.inventory;
  }

  private void inventoryChanged() {
    this.markDirty();
    if(this.world != null) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }
  }

  public void dropAllItems() {
    if (this.world != null) {
      if (!this.world.isRemote()) {
        InventoryHelper.dropItems(this.world, this.getPos(), this.getInventory());
      }
      this.inventoryChanged();
    }

  }
  
  @Override
  public void clear() {
    this.inventory.clear();
    this.inventoryChanged();
  }

  @Override
  public int getSizeInventory() {
    return this.inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return this.inventory.isEmpty();
  }

  /**
   * Returns the stack in the given slot.
   */
  public ItemStack getStackInSlot(int index) {
    return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  public ItemStack decrStackSize(int index, int count) {
    this.inventoryChanged();
    return ItemStackHelper.getAndSplit(this.inventory, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeStackFromSlot(int index) {
    this.inventoryChanged();
    return ItemStackHelper.getAndRemove(this.inventory, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setInventorySlotContents(int index, ItemStack stack) {
    if (index >= 0 && index < this.inventory.size()) {
      this.inventory.set(index, stack);
      this.inventoryChanged();
    }
  }

  @Override
  public boolean isUsableByPlayer(PlayerEntity player) {
    if (this.world.getTileEntity(this.pos) != this) {
      return false;
    } else {
      return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
          (double) this.pos.getZ() + 0.5D) > 64.0D);
    }
  }

  // OTHER //

  @OnlyIn(Dist.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return 256.0D;
  }
}
