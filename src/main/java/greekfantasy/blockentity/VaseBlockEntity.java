package greekfantasy.blockentity;

import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class VaseBlockEntity extends BlockEntity implements Container, Nameable {

    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> createUnSidedHandler());
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    protected Component name;

    public VaseBlockEntity(BlockPos pos, BlockState state) {
        super(GFRegistry.BlockEntityReg.VASE.get(), pos, state);
    }

    // CLIENT-SERVER SYNC

    /**
     * Called when the chunk is saved
     * @return the compound tag to use in #handleUpdateTag
     */
    @Override
    public CompoundTag getUpdateTag() {
        return ContainerHelper.saveAllItems(super.getUpdateTag(), inventory);
    }

    /**
     * Called when the chunk is loaded
     * @param tag the compound tag
     */
    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        inventoryChanged();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //INVENTORY //

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    public void dropAllItems() {
        if (this.level != null && !this.level.isClientSide()) {
            Containers.dropContents(this.level, this.getBlockPos(), this.getInventory());
        }
        this.inventoryChanged();
    }

    public void inventoryChanged() {
        if (getLevel() != null && !getLevel().isClientSide()) {
            // GreekFantasy.LOGGER.debug("sending update... ");
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        ItemStack itemStack = getItem(0);
        this.name = itemStack.hasCustomHoverName() ? itemStack.getHoverName() : null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventoryChanged();
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
        this.setChanged();
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
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory, index, count);
        return itemStack;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = ContainerHelper.takeItem(this.inventory, index);
        return itemStack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.inventory.size()) {
            this.inventory.set(index, stack);
            this.inventoryChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D,
                    (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    // NBT / SAVING

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory.clear();
        ContainerHelper.loadAllItems(nbt, this.inventory);
        this.inventoryChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory, true);
    }

    // NAMEABLE

    protected Component getDefaultName() {
        return Component.translatable("container.vase");
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    // CAPABILITY

    protected IItemHandler createUnSidedHandler() {
        return new InvWrapper(this);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> createUnSidedHandler());
    }
}
