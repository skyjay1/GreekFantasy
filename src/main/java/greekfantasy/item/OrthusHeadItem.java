package greekfantasy.item;

import greekfantasy.entity.misc.OrthusHeadItemEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OrthusHeadItem extends MobHeadItem {

    public OrthusHeadItem(final Block blockIn, final Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItem() == this;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        final OrthusHeadItemEntity e = OrthusHeadItemEntity.create(world, location.getX(), location.getY(), location.getZ(), itemstack);
        e.setDeltaMovement(location.getDeltaMovement());
        e.setPickUpDelay(40);
        return e;
    }

}
