package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.NymphModel;
import greekfantasy.entity.DryadEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DryadRenderer<T extends DryadEntity> extends BipedRenderer<T, NymphModel<T>> {

    public static final Map<DryadEntity.Variant, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    static {
        TEXTURE_MAP.put(DryadEntity.Variant.ACACIA, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/acacia.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.BIRCH, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/birch.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.DARK_OAK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/dark_oak.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.JUNGLE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/jungle.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.OAK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/oak.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.OLIVE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/olive.png"));
        TEXTURE_MAP.put(DryadEntity.Variant.SPRUCE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/spruce.png"));
    }

    public DryadRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new NymphModel<T>(0.0F), 0.25F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE_MAP.get(entity.getVariant());
    }
}
