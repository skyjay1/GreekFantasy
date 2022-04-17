package greekfantasy.tileentity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.deity.Deity;
import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.FavorConfiguration;
import greekfantasy.deity.favor.FavorRange;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.event.FavorChangedEvent;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
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
    this.setChanged();
  }
  
  public boolean isUpper() { return this.upper; }

  public StatuePose getStatuePose() { return this.statuePose; }
  
  public void setStatuePose(final StatuePose poseIn) { setStatuePose(poseIn, false); }
  
  public void setStatuePose(final StatuePose poseIn, final boolean refresh) {
    this.statuePose = poseIn;
    this.setChanged();
    if(refresh) {
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
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
    this.setChanged();
    if(refresh) {
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
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
    this.setChanged();
    if(refresh) {
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  // Deity Name //
  
  public String getDeityName() { return deityName; }
  
  public void setDeityName(final String deityIn) { setDeityName(deityIn, false); }
  
  public void setDeityName(final String deityIn, final boolean refresh) {
    this.deityName = deityIn;
    this.updateDeity();
    this.setChanged();
    if(refresh) {
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  // Deity //
  
  public IDeity getDeity() { return deity; }
  
  public boolean hasDeity() { return deity != null && deity != Deity.EMPTY; }

  public void updateDeity() {
    this.deity = deityName.isEmpty() ? Deity.EMPTY : GreekFantasy.PROXY.DEITY.get(new ResourceLocation(deityName)).orElse(Deity.EMPTY);
    this.setChanged();
  }
  
  public ItemStack handleItemInteraction(final PlayerEntity player, final IDeity deity, final IFavor favor, final ItemStack item) {
    if(!favor.isEnabled()) {
      return item;
    }
    // attempt to enchant the item
    final FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    if(GreekFantasy.CONFIG.isFlyingEnabled() && tryEnchant(player, deity, favor, item, FavorConfiguration.FLYING_RANGE, GFRegistry.FLYING_ENCHANTMENT)) {
      return item;
    }
    if(GreekFantasy.CONFIG.isLordOfTheSeaEnabled() && tryEnchant(player, deity, favor, item, FavorConfiguration.LORD_OF_THE_SEA_RANGE, GFRegistry.LORD_OF_THE_SEA_ENCHANTMENT)) {
      return item;
    }
    if(GreekFantasy.CONFIG.isFireflashEnabled() && tryEnchant(player, deity, favor, item, FavorConfiguration.FIREFLASH_RANGE, GFRegistry.FIREFLASH_ENCHANTMENT)) {
      return item;
    }
    if(GreekFantasy.CONFIG.isRaisingEnabled() && tryEnchant(player, deity, favor, item, FavorConfiguration.RAISING_RANGE, GFRegistry.RAISING_ENCHANTMENT)) {
      return item;
    }
    if(GreekFantasy.CONFIG.isDaybreakEnabled() && tryEnchant(player, deity, favor, item, FavorConfiguration.DAYBREAK_RANGE, GFRegistry.DAYBREAK_ENCHANTMENT)) {
      return item;
    }
    // attempt to give the Apollo Bow item
    FavorRange range = favorConfig.getEnchantmentRange(FavorConfiguration.APOLLO_BOW_RANGE);
    if(item.getItem() == GFRegistry.CURSED_BOW && deity != Deity.EMPTY 
        && deity.getName().equals(range.getDeity().getName())
        && range.isInFavorRange(player, favor)) {
      ItemStack bow = new ItemStack(GFRegistry.APOLLO_BOW);
      bow.getItem().onCraftedBy(bow, player.getCommandSenderWorld(), player);
      return bow;
    }
    // attempt to give the Artemis Bow item
    range = favorConfig.getEnchantmentRange(FavorConfiguration.ARTEMIS_BOW_RANGE);
    if(item.getItem() == GFRegistry.CURSED_BOW && deity != Deity.EMPTY 
        && deity.getName().equals(range.getDeity().getName())
        && range.isInFavorRange(player, favor)) {
      ItemStack bow = new ItemStack(GFRegistry.ARTEMIS_BOW);
      bow.getItem().onCraftedBy(bow, player.getCommandSenderWorld(), player);
      return bow;
    }
    // attempt to handle apple of discord
    IDeity aphrodite = GreekFantasy.PROXY.DEITY.get(new ResourceLocation(GreekFantasy.MODID, "aphrodite")).orElse(Deity.EMPTY);
    IDeity athena = GreekFantasy.PROXY.DEITY.get(new ResourceLocation(GreekFantasy.MODID, "athena")).orElse(Deity.EMPTY);
    IDeity hera = GreekFantasy.PROXY.DEITY.get(new ResourceLocation(GreekFantasy.MODID, "hera")).orElse(Deity.EMPTY);
    if(item.getItem() == GFRegistry.GOLDEN_APPLE_OF_DISCORD && deity != Deity.EMPTY
        && aphrodite.isEnabled() && athena.isEnabled() && hera.isEnabled()) {
      // determine amount of favor to add/subtract
      int dFavor = GreekFantasy.PROXY.getFavorConfiguration().getAppleOfDiscordAmount();
      int dFavorMinus = -dFavor * 5 / 4;
      int dAphrodite = dFavorMinus;
      int dAthena = dFavorMinus;
      int dHera = dFavorMinus;
      if(deity.getName().equals(aphrodite.getName())) {
        dAphrodite = dFavor;
      } else if(deity.getName().equals(athena.getName())) {
        dAthena = dFavor;
      } else if(deity.getName().equals(hera.getName())) {
        dHera = dFavor;
      }
      // actually update the favor
      favor.getFavor(aphrodite).addFavor(player, aphrodite, dAphrodite, FavorChangedEvent.Source.GIVE_ITEM);
      favor.getFavor(athena).addFavor(player, athena, dAthena, FavorChangedEvent.Source.GIVE_ITEM);
      favor.getFavor(hera).addFavor(player, hera, dHera, FavorChangedEvent.Source.GIVE_ITEM);
      item.shrink(1);
      return item;
    }
    // return the original item
    return item;
  }
  
  /**
   * Attempts to enchant the given item if the player
   * has a favor range as specified by the key
   * @param player the player
   * @param deity the deity of this altar
   * @param favor the player's favor
   * @param stack the item to enchant
   * @param rangeKey a key to look up the FavorRange for the given enchantment
   * @param enchant the enchantment to apply
   * @return true if the item was successfully enchanted
   */
  protected boolean tryEnchant(final PlayerEntity player, final IDeity deity, final IFavor favor, final ItemStack stack, 
      final String rangeKey, final Enchantment enchant) {
    final FavorRange range = GreekFantasy.PROXY.getFavorConfiguration().getEnchantmentRange(rangeKey);
    if(enchant.canEnchant(stack) && deity != Deity.EMPTY && deity.getName().equals(range.getDeity().getName())
        && range.isInFavorRange(player, favor) && EnchantmentHelper.getItemEnchantmentLevel(enchant, stack) < 1) {
      // if the item has size 1, enchant directly
      if(stack.getCount() == 1) {
        stack.enchant(enchant, 1);
        stack.setDamageValue(0);
      } else {
        // drop one enchanted item from the itemstack
        final ItemStack enchantedItem = stack.split(1);
        enchantedItem.enchant(enchant, 1);
        ItemEntity drop = player.drop(enchantedItem, false);
        if(drop != null) {
          drop.setNoPickUpDelay();
        }
      }
      return true;
    }
    return false;
  }
  
  // Player Profile //
  
  @Nullable
  public GameProfile getPlayerProfile() { return this.playerProfile; }
  
  private void setPlayerProfile(@Nullable GameProfile profile) {
    this.playerProfile = profile;
    this.updatePlayerProfile();
  }

  private void updatePlayerProfile() {
    this.playerProfile = SkullTileEntity.updateGameprofile(this.playerProfile);
    this.setChanged();
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
    return new SUpdateTileEntityPacket(getBlockPos(), -1, buildUpdateTag(new CompoundNBT()));
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    readUpdateTag(pkt.getTag());
  }

  @Override
  public void load(BlockState state, CompoundNBT nbt) {
    super.load(state, nbt);
    readUpdateTag(nbt);
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
    return buildUpdateTag(super.save(compound));
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
    this.inventory.set(i, stack);
    this.inventoryChanged();
  }
  
  public ItemStack getItem(final HandSide hand) {
    return this.inventory.get(hand.ordinal());
  }
  
  public NonNullList<ItemStack> getInventory() {
     return this.inventory;
  }

  private void inventoryChanged() {
    this.setChanged();
    if(this.level != null) {
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }

  public void dropAllItems() {
    if (this.level != null) {
      if (!this.level.isClientSide()) {
        InventoryHelper.dropContents(this.level, this.getBlockPos(), this.getInventory());
      }
      this.inventoryChanged();
    }

  }
  
  @Override
  public void clearContent() {
    this.inventory.clear();
    this.inventoryChanged();
  }

  @Override
  public int getContainerSize() {
    return this.inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return this.inventory.isEmpty();
  }

  /**
   * Returns the stack in the given slot.
   */
  public ItemStack getItem(int index) {
    return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  public ItemStack removeItem(int index, int count) {
    this.inventoryChanged();
    return ItemStackHelper.removeItem(this.inventory, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeItemNoUpdate(int index) {
    this.inventoryChanged();
    return ItemStackHelper.takeItem(this.inventory, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setItem(int index, ItemStack stack) {
    if (index >= 0 && index < this.inventory.size()) {
      this.inventory.set(index, stack);
      this.inventoryChanged();
    }
  }

  @Override
  public boolean stillValid(PlayerEntity player) {
    if (this.level.getBlockEntity(this.worldPosition) != this) {
      return false;
    } else {
      return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D,
          (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
    }
  }

  // OTHER //

  @OnlyIn(Dist.CLIENT)
  public double getViewDistance() {
    return 256.0D;
  }
}
