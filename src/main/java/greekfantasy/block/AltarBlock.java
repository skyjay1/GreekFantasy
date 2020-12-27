package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.favor.DeityManager;
import greekfantasy.favor.FavorManager;
import greekfantasy.favor.IDeity;
import greekfantasy.favor.IFavor;
import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class AltarBlock extends StatueBlock {
  
  private final IDeity deity;
   
  public AltarBlock(final IDeity deityIn, final StatueBlock.StatueMaterial material) {
    super(material, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).hardnessAndResistance(30.0F, 1200.0F).sound(SoundType.STONE).setLightLevel(b -> material.getLightLevel()).notSolid());
    deity = deityIn;
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    // prepare to interact with this block
    final BlockPos tePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
    final TileEntity te = worldIn.getTileEntity(tePos);
    if (playerIn instanceof ServerPlayerEntity && te instanceof AltarTileEntity) {
      final AltarTileEntity teStatue = (AltarTileEntity)te;
      final ItemStack stack = playerIn.getHeldItem(handIn);
      final IDeity teDeity = teStatue.getDeity();
      if(teDeity != null && teDeity == deity) {
        // TODO handle quests and items here
        LazyOptional<IFavor> favor = playerIn.getCapability(GreekFantasy.FAVOR);
        favor.ifPresent(f -> {
          FavorManager.onGiveItem(teStatue, teDeity, playerIn, f.getFavor(teDeity), stack);
          // DEBUG
          playerIn.sendStatusMessage(new StringTextComponent("favor: " + f.getFavor(teDeity).getFavor()), false);
        });
      }      
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public boolean canDropItems(final BlockState state, final IBlockReader world) {
    return false;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final AltarTileEntity te = GFRegistry.ALTAR_TE.create();
    te.setUpper(state.get(HALF) == DoubleBlockHalf.UPPER);
    te.setDeity(deity);
    deity.initAltar().accept(te);
    return te;
  }
}
