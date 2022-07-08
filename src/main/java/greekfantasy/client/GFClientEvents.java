package greekfantasy.client;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.SpearRenderer;
import greekfantasy.entity.misc.SpearEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GFClientEvents {

    public static final class ForgeHandler {

    }

    public static final class ModHandler {

        private static final String MODID = GreekFantasy.MODID;

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ModHandler::registerRenderLayers);
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


        private static void registerRenderLayers() {
            // cutout mipped
            registerRenderLayer("olive_leaves", RenderType.cutoutMipped());
            registerRenderLayer("pomegranate_leaves", RenderType.cutoutMipped());
            registerRenderLayer("golden_leaves", RenderType.cutoutMipped());
            // cutout
            registerCutout("olive_sapling");
            registerCutout("olive_door");
            registerCutout("olive_trapdoor");
            registerCutout("pomegranate_sapling");
            registerCutout("pomegranate_door");
            registerCutout("pomegranate_trapdoor");
            registerCutout("golden_sapling");
            registerCutout("olive_oil");
        }

        private static void registerCutout(final String blockName) {
            registerRenderLayer(blockName, RenderType.cutout());
        }

        private static void registerRenderLayer(final String blockName, RenderType renderType) {
            ItemBlockRenderTypes.setRenderLayer(RegistryObject.create(new ResourceLocation(MODID, blockName), ForgeRegistries.BLOCKS).get(), renderType);
        }

        private static void registerContainerRenders() {

        }

        private static void registerModelProperties() {
            // register bows
            registerBowProperties(GFRegistry.ItemReg.AVERNAL_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.APOLLO_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.ARTEMIS_BOW.get());
            // register spears
            registerSpearProperties(GFRegistry.ItemReg.BIDENT.get());
            registerSpearProperties(GFRegistry.ItemReg.WOODEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.FLINT_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.STONE_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.GOLDEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.IRON_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.DIAMOND_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.NETHERITE_SPEAR.get());
        }

        private static void registerBowProperties(final Item bow) {
            ItemProperties.register(bow, new ResourceLocation("pull"),
                    (item, world, entity, tintIndex) -> {
                        if (entity == null) return 0.0F;
                        if (entity.getUseItem() != item) return 0.0F;
                        return (item.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
            ItemProperties.register(bow, new ResourceLocation("pulling"),
                    (item, world, entity, tintIndex) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerSpearProperties(final Item spear) {
            ItemProperties.register(spear, new ResourceLocation("throwing"), (item, world, entity, tintIndex) ->
                    (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

    }
}
