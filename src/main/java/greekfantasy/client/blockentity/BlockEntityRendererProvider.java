package greekfantasy.client.blockentity;

import greekfantasy.client.entity.SpearRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityRendererProvider {

    private static BlockEntityWithoutLevelRenderer orthusHead;
    private static BlockEntityWithoutLevelRenderer cerberusHead;
    private static BlockEntityWithoutLevelRenderer giganteHead;
    private static final Map<ResourceLocation, BlockEntityWithoutLevelRenderer> spearMap = new HashMap<>();

    public static BlockEntityWithoutLevelRenderer getOrthusHead() {
        if (orthusHead == null) {
            final BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
            orthusHead = new OrthusHeadBlockEntityRenderer.OrthusHeadItemStackRenderer(dispatcher, entityModelSet);
        }
        return orthusHead;
    }

    public static BlockEntityWithoutLevelRenderer getGiganteHead() {
        if (giganteHead == null) {
            final BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
            giganteHead = new GiganteHeadBlockEntityRenderer.GiganteHeadItemStackRenderer(dispatcher, entityModelSet);
        }
        return giganteHead;
    }

    public static BlockEntityWithoutLevelRenderer getCerberusHead() {
        if (cerberusHead == null) {
            final BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
            cerberusHead = new CerberusHeadBlockEntityRenderer.CerberusHeadItemStackRenderer(dispatcher, entityModelSet);
        }
        return cerberusHead;
    }

    public static BlockEntityWithoutLevelRenderer getSpear(final ResourceLocation key) {
        if(net.minecraftforge.fml.loading.FMLEnvironment.dist != net.minecraftforge.api.distmarker.Dist.CLIENT) {
            throw new IllegalStateException("Attempted to access BlockEntityRendererProvider on the server, aborting.");
        }
        if (!spearMap.containsKey(key)) {
            final BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            final EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
            spearMap.put(key, new SpearRenderer.SpearItemStackRenderer(dispatcher, entityModelSet, key));
        }
        return spearMap.get(key);
    }
}
