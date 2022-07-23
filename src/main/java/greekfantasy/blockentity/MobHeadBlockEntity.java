package greekfantasy.blockentity;

import greekfantasy.block.MobHeadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MobHeadBlockEntity extends BlockEntity {

    public MobHeadBlockEntity(BlockEntityType<? extends MobHeadBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean onWall() {
        return getBlockState().getValue(MobHeadBlock.WALL);
    }
}
