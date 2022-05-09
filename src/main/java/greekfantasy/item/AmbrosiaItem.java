package greekfantasy.item;

import greekfantasy.GFRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

public class AmbrosiaItem extends Item {

    public AmbrosiaItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(GFRegistry.ItemReg.HORN);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, World world, LivingEntity entity) {
        super.finishUsingItem(item, world, entity);
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (item.isEmpty())
            return this.getContainerItem(item);
        if (entity instanceof PlayerEntity && !((PlayerEntity) entity).abilities.instabuild) {
            ItemStack containerStack = this.getContainerItem(item);
            PlayerEntity player = (PlayerEntity) entity;
            if (!player.inventory.add(containerStack)) {
                player.drop(containerStack, false);
            }
        }

        return item;
    }

}
