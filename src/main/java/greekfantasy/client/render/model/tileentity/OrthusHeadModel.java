package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.client.render.OrthusRenderer;
import greekfantasy.client.render.model.OrthusModel;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer.IWallModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class OrthusHeadModel extends Model implements IHasHead, IWallModel {

    protected ModelRenderer orthusHead;

    public OrthusHeadModel() {
        this(0.0F, 0.0F);
    }

    public OrthusHeadModel(final float modelSize, final float yOffset) {
        super(RenderType::entityCutoutNoCull);
        this.texWidth = 64;
        this.texHeight = 32;

        orthusHead = OrthusModel.getHeadModel(this, -5.55F, -3.0F, 0.0F, 0.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        orthusHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ModelRenderer getHead() {
        return this.orthusHead;
    }

    @Override
    public void setWallRotations(boolean onWall) {
        if (onWall) {
            orthusHead.y = -5.0F;
            orthusHead.z = 0.2F;
        } else {
            orthusHead.y = -3.0F;
            orthusHead.z = -3.0F;
        }
    }

    @Override
    public float getScale() {
        return OrthusRenderer.SCALE;
    }

    public void setGuiRotations(final float yaw, final float pitch) {
        this.orthusHead.xRot = pitch * 0.017453292F;
        this.orthusHead.yRot = yaw * 0.017453292F;
    }
}
