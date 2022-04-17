package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.SatyrGroverLayer;
import greekfantasy.client.render.layer.SatyrPanfluteLayer;
import greekfantasy.client.render.layer.SatyrShamanLayer;
import greekfantasy.client.render.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

public class SatyrRenderer<T extends SatyrEntity> extends BipedRenderer<T, SatyrModel<T>> {

    private static final ResourceLocation TEXTURE_GROVER = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/grover.png");

    public static final EnumMap<CoatColors, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(CoatColors.class);

    static {
        BODY_TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/black.png"));
        BODY_TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/brown.png"));
        BODY_TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/chestnut.png"));
        BODY_TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/creamy.png"));
        BODY_TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/darkbrown.png"));
        BODY_TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/gray.png"));
        BODY_TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/white.png"));
    }

    public SatyrRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SatyrModel<T>(0.0F), 0.5F);
        this.addLayer(new SatyrShamanLayer<>(this));
        this.addLayer(new SatyrPanfluteLayer<>(this));
        this.addLayer(new SatyrGroverLayer<>(this));
    }

    @Override
    public void render(final T entityIn, final float rotationYawIn, final float partialTick,
                       final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
        super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        if (entity.hasCustomName() && "Grover".equals(entity.getCustomName().getContents())) {
            return TEXTURE_GROVER;
        } else {
            return BODY_TEXTURE_MAP.get(entity.getCoatColor());
        }
    }
}
