package greekfantasy.client;

import greekfantasy.GFRegistry;
import greekfantasy.client.entity.SpearRenderer;
import greekfantasy.entity.misc.SpearEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class GFClientEvents {

    public static final class ForgeHandler {

    }

    public static final class ModHandler {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ModHandler::registerContainerRenders);
            event.enqueueWork(ModHandler::registerModelProperties);
        }

        @SubscribeEvent
        public static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            // register layer definitions
            event.registerLayerDefinition(SpearRenderer.SPEAR_MODEL_RESOURCE, TridentModel::createLayer);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer((EntityType<? extends SpearEntity>) GFRegistry.EntityReg.SPEAR.get(), SpearRenderer::new);
        }

        private static void registerContainerRenders() {

        }

        private static void registerModelProperties() {

        }
    }
}
