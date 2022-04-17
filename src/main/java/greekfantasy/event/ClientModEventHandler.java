package greekfantasy.event;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.particle.GorgonParticle;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientModEventHandler {
  
  /**
   * Used to trigger several client-side registrations,
   * such as entity / tile-entity renders, container renders,
   * layers, etc.
   * @param event the client setup event
   **/
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    GreekFantasy.PROXY.registerEntityRenders();
    GreekFantasy.PROXY.registerTileEntityRenders();
    GreekFantasy.PROXY.registerContainerRenders();
    GreekFantasy.PROXY.registerRenderLayers();
    GreekFantasy.PROXY.registerModelProperties();
    GreekFantasy.PROXY.registerPlayerLayers();
  }
  
  /**
   * Used to register custom particle renders.
   * Currently handles the Gorgon particle
   * @param event the particle factory registry event
   **/
  @SubscribeEvent
  public static void registerParticleRenders(final ParticleFactoryRegisterEvent event) {
    GreekFantasy.LOGGER.debug("registerParticleRenders");
    final Minecraft mc = Minecraft.getInstance();
    mc.particleEngine.register(GFRegistry.GORGON_PARTICLE, new GorgonParticle.Factory());
  }
  
  /**
   * Used to register block color handlers.
   * Currently used to color leaves.
   * @param event the ColorHandlerEvent (Block)
   **/
  @SubscribeEvent
  public static void onBlockColors(final ColorHandlerEvent.Block event) {
    GreekFantasy.LOGGER.debug("registerBlockColors");
    event.getBlockColors().register(
        (BlockState stateIn, IBlockDisplayReader world, BlockPos pos, int color) -> 0xD8E3D0, GFRegistry.OLIVE_LEAVES);
    event.getBlockColors().register(
        (BlockState stateIn, IBlockDisplayReader world, BlockPos pos, int color) -> 0x80f66b, GFRegistry.GOLDEN_APPLE_LEAVES);
  }
  
  /**
   * Used to register item color handlers.
   * Currently used to color leaves.
   * @param event the ColorHandlerEvent (Item)
   **/
  @SubscribeEvent
  public static void onItemColors(final ColorHandlerEvent.Item event) {
    GreekFantasy.LOGGER.debug("registerItemColors");
    event.getItemColors().register((ItemStack item, int i) -> 0xD8E3D0, GFRegistry.OLIVE_LEAVES);
    event.getItemColors().register((ItemStack item, int i) -> 0x80f66b, GFRegistry.GOLDEN_APPLE_LEAVES);
  }  
}
