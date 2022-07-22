package greekfantasy.blockentity;

import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MobHeadBlockEntity extends BlockEntity {

    private static final String KEY_WALL = "Wall";
    private boolean wall = false;

    public MobHeadBlockEntity(BlockEntityType<? extends MobHeadBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean onWall() {
        return wall;
    }

    public void setWall(final boolean isOnWall) {
        if (wall != isOnWall) {
            wall = isOnWall;
            setChanged();
        }
    }

    // CLIENT-SERVER SYNC

    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag nbt = super.getUpdateTag();
        nbt.putBoolean(KEY_WALL, wall);
        return nbt;
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    // NBT / SAVING

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        setWall(tag.getBoolean(KEY_WALL));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(KEY_WALL, wall);
    }

    public static class CerberusBlockEntity extends MobHeadBlockEntity {

        public CerberusBlockEntity(BlockPos pos, BlockState state) {
            super(GFRegistry.BlockEntityReg.CERBERUS_HEAD.get(), pos, state);
        }
    }

    public static class GiganteBlockEntity extends MobHeadBlockEntity {

        public GiganteBlockEntity(BlockPos pos, BlockState state) {
            super(GFRegistry.BlockEntityReg.GIGANTE_HEAD.get(), pos, state);
        }
    }

    public static class OrthusBlockEntity extends MobHeadBlockEntity {

        public OrthusBlockEntity(BlockPos pos, BlockState state) {
            super(GFRegistry.BlockEntityReg.ORTHUS_HEAD.get(), pos, state);
        }
    }
}
