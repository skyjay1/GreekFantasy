package greekfantasy.block;

import greekfantasy.blockentity.MobHeadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

public class OrthusHeadBlock extends MobHeadBlock {

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(5.0D, 0.0D, 6.0D, 11.0D, 6.0D, 10.0D),
            Block.box(6.5D, 0.0D, 10.0D, 9.5D, 3.0D, 13.0D));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(6.0D, 0.0D, 5.0D, 10.0D, 6.0D, 11.0D),
            Block.box(10.0D, 0.0D, 6.5D, 13.0D, 3.0D, 9.5D));
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(5.0D, 0.0D, 6.0D, 11.0D, 6.0D, 10.0D),
            Block.box(6.5D, 0.0D, 3.0D, 9.5D, 3.0D, 6.0D));

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6.0D, 0.0D, 5.0D, 10.0D, 6.0D, 11.0D),
            Block.box(3.0D, 0.0D, 6.5D, 6.0D, 3.0D, 9.5D));

    private static final VoxelShape[] FLOOR_SHAPES = {SHAPE_SOUTH, SHAPE_WEST, SHAPE_NORTH, SHAPE_EAST};
    private static final VoxelShape[] WALL_SHAPES = {
            SHAPE_SOUTH.move(0.0D, 5.0D / 16.0D, -6.0D / 16.0D),
            SHAPE_WEST.move(6.0D / 16.0D, 5.0D / 16.0D, 0.0D),
            SHAPE_NORTH.move(0.0D, 5.0D / 16.0D, 6.0D / 16.0D),
            SHAPE_EAST.move(-6.0D / 16.0D, 5.0D / 16.0D, 0.0D)};

    public OrthusHeadBlock(RegistryObject<BlockEntityType<MobHeadBlockEntity>> typeSupplier, Properties prop) {
        super(typeSupplier, prop);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
        int facing = state.getValue(FACING).get2DDataValue();
        return state.getValue(WALL) ? WALL_SHAPES[facing] : FLOOR_SHAPES[facing];
    }
}
