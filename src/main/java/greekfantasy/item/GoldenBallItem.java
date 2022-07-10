package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;

public class GoldenBallItem extends Item {

    public GoldenBallItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        // attempt to place golden string when holding golden string
        // TODO also check if entity is in a maze structure
        boolean mainhand;
        if(entity instanceof LivingEntity livingEntity && livingEntity.tickCount % 4 == 0
                && !level.isOutsideBuildHeight(livingEntity.blockPosition())
                && ((mainhand = livingEntity.getMainHandItem().getItem() == this) || (livingEntity.getOffhandItem().getItem() == this))) {
            // determine hand
            InteractionHand hand = mainhand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            // determine position and state
            BlockPos pos = livingEntity.blockPosition();
            BlockState current = level.getBlockState(pos);
            BlockState string = GFRegistry.BlockReg.GOLDEN_STRING.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, current.getFluidState().is(FluidTags.WATER));
            boolean replaceable = current.getMaterial() == Material.AIR || current.getMaterial() == Material.WATER;
            if (replaceable && current.getBlock() != GFRegistry.BlockReg.GOLDEN_STRING.get()
                     && string.canSurvive(level, pos)) {
                // place golden string
                level.setBlock(pos, string, Block.UPDATE_ALL);
                // damage item
                livingEntity.getItemInHand(hand).hurtAndBreak(1, livingEntity, e -> e.broadcastBreakEvent(hand));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameIgnoreDurability(oldStack, newStack);
    }
}
