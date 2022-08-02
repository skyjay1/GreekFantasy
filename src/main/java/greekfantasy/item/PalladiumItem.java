package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.entity.Palladium;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PalladiumItem extends Item {

    public PalladiumItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level world = context.getLevel();
            BlockPlaceContext blockitemusecontext = new BlockPlaceContext(context);
            BlockPos blockpos = blockitemusecontext.getClickedPos();
            ItemStack itemstack = context.getItemInHand();
            Vec3 vector3d = Vec3.atBottomCenterOf(blockpos);
            AABB aabb = GFRegistry.EntityReg.PALLADIUM.get().getDimensions().makeBoundingBox(vector3d.x(), vector3d.y(), vector3d.z());
            if (world.noCollision(aabb) && world.getEntities(null, aabb).isEmpty()) {
                if (world instanceof ServerLevel serverLevel) {
                    // create palladium entity
                    Palladium palladium = Palladium.createPalladium(world, blockpos, context.getHorizontalDirection().getOpposite());
                    if (palladium == null) {
                        return InteractionResult.FAIL;
                    }
                    // add palladium entity and play sound
                    serverLevel.addFreshEntity(palladium);
                    world.playSound(null, palladium.getX(), palladium.getY(), palladium.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, palladium);
                }
                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

}
