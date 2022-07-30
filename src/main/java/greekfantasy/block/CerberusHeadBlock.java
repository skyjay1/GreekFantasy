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

public class CerberusHeadBlock extends MobHeadBlock {

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(3.25D, 0.0D, 0.0D, 12.75D, 11.4D, 9.5D),
            Block.box(5.25D, 0.0D, 9.0D, 10.75D, 5.5D, 16.0D));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(0.0D, 0.0D, 3.25D, 9.5D, 11.4D, 12.75D),
            Block.box(9.0D, 0.0D, 5.25D, 16.0D, 5.5D, 10.75D));
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3.25D, 0.0D, 6.5D, 12.75D, 11.4D, 16.0D),
            Block.box(5.25D, 0.0D, 0.0D, 10.75D, 5.5D, 7.0D));
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6.5D, 0.0D, 3.25D, 16.0D, 11.4D, 12.75D),
            Block.box(0.0D, 0.0D, 5.25D, 7.0D, 5.5D, 10.75D));

    private static final VoxelShape[] FLOOR_SHAPES = {SHAPE_SOUTH, SHAPE_WEST, SHAPE_NORTH, SHAPE_EAST};

    public CerberusHeadBlock(RegistryObject<BlockEntityType<MobHeadBlockEntity>> typeSupplier, Properties prop) {
        super(typeSupplier, prop);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
        int facing = state.getValue(FACING).get2DDataValue();
        return FLOOR_SHAPES[facing];
    }
}
