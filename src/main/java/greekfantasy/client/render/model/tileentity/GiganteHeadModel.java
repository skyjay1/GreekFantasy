package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer.IWallModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GiganteHeadModel extends Model implements IHasHead, IWallModel {

    protected ModelRenderer bipedHead;

    public GiganteHeadModel() {
        this(0.0F, 0.0F);
    }

    public GiganteHeadModel(final float modelSize, final float yOffset) {
        super(RenderType::entityCutoutNoCull);
        this.texWidth = 128;
        this.texHeight = 64;

        bipedHead = new ModelRenderer(this);
        bipedHead.setPos(-5.0F, -10.0F, -5.0F);
        bipedHead.texOffs(0, 0).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        bipedHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ModelRenderer getHead() {
        return this.bipedHead;
    }

    @Override
    public void setWallRotations(boolean onWall) {
        // do nothing
    }

    @Override
    public float getScale() {
        return 1.6F;
    }

}
