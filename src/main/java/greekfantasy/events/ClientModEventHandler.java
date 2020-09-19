package greekfantasy.events;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.particle.GorgonParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientModEventHandler {
  
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    GreekFantasy.PROXY.registerEntityRenders();
    GreekFantasy.PROXY.registerTileEntityRenders();
    GreekFantasy.PROXY.registerContainerRenders();
  }
  
  @SubscribeEvent
  public static void registerParticleRenders(final ParticleFactoryRegisterEvent event) {
    GreekFantasy.LOGGER.info("registerParticleRenders");
    final Minecraft mc = Minecraft.getInstance();
    mc.particles.registerFactory(GFRegistry.GORGON_PARTICLE, new GorgonParticle.Factory());
  }
}
