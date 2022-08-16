package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.block.GoldenStringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class GoldenBallItem extends Item {

    public GoldenBallItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        // attempt to place golden string when holding golden string
        boolean mainhand;
        if(!level.isClientSide() && level instanceof ServerLevel serverLevel
                && entity instanceof LivingEntity livingEntity && livingEntity.tickCount % 4 == 0
                && !level.isOutsideBuildHeight(livingEntity.blockPosition())
                && ((mainhand = livingEntity.getMainHandItem().getItem() == this) || (livingEntity.getOffhandItem().getItem() == this))
                && serverLevel.structureFeatureManager().hasAnyStructureAt(entity.blockPosition())) {
            // determine hand
            InteractionHand hand = mainhand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            // determine position and state
            BlockPos pos = livingEntity.blockPosition();
            BlockState current = level.getBlockState(pos);
            BlockState string = GFRegistry.BlockReg.GOLDEN_STRING.get().defaultBlockState();
            // determine whether to remove existing string
            boolean isString = current.is(GFRegistry.BlockReg.GOLDEN_STRING.get());
            if(isString && current.getValue(GoldenStringBlock.AGE) > 0) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            } else if(!isString && current.getMaterial().isReplaceable() && string.canSurvive(level, pos)) {
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
