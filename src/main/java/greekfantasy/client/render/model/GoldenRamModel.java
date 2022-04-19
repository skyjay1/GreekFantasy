package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.entity.GoldenRamEntity;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GoldenRamModel<T extends GoldenRamEntity> extends SheepModel<T> {

    protected final ModelRenderer horns;

    public GoldenRamModel() {
        super();
        // left horn
        ModelRenderer leftHorn = new ModelRenderer(this);
        leftHorn.setPos(3.0F, -2.0F, -4.0F);
        leftHorn.xRot = 0.0873F;
        leftHorn.yRot = 0.1745F;
        leftHorn.zRot = 0.4363F;
        leftHorn.texOffs(56, 0).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        ModelRenderer leftHorn2 = new ModelRenderer(this);
        leftHorn2.setPos(-2.0F, -4.0F, -1.0F);
        leftHorn2.xRot = -0.7854F;
        leftHorn2.yRot = 0.2618F;
        leftHorn2.texOffs(56, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        leftHorn.addChild(leftHorn2);

        ModelRenderer leftHorn3 = new ModelRenderer(this);
        leftHorn3.setPos(0.0F, -4.0F, 0.0F);
        leftHorn3.xRot = -1.2217F;
        leftHorn3.yRot = 0.2618F;
        leftHorn3.texOffs(56, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        leftHorn2.addChild(leftHorn3);

        ModelRenderer leftHorn4 = new ModelRenderer(this);
        leftHorn4.setPos(1.0F, -4.0F, 0.0F);
        leftHorn4.xRot = -1.2217F;
        leftHorn4.yRot = 0.2618F;
        leftHorn4.texOffs(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        leftHorn3.addChild(leftHorn4);

        ModelRenderer leftHorn5 = new ModelRenderer(this);
        leftHorn5.setPos(0.0F, -4.0F, 0.0F);
        leftHorn5.xRot = -1.0472F;
        leftHorn5.yRot = 0.2618F;
        leftHorn5.texOffs(58, 6).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        leftHorn4.addChild(leftHorn5);

        // right horn
        ModelRenderer rightHorn = new ModelRenderer(this);
        rightHorn.setPos(-3.0F, -2.0F, -4.0F);
        rightHorn.xRot = 0.0873F;
        rightHorn.yRot = -0.1745F;
        rightHorn.zRot = -0.4363F;
        rightHorn.texOffs(56, 0).addBox(0.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        ModelRenderer rightHorn2 = new ModelRenderer(this);
        rightHorn2.setPos(2.0F, -4.0F, -1.0F);
        rightHorn2.xRot = -0.7854F;
        rightHorn2.yRot = -0.2618F;
        rightHorn2.texOffs(56, 0).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        rightHorn.addChild(rightHorn2);

        ModelRenderer rightHorn3 = new ModelRenderer(this);
        rightHorn3.setPos(0.0F, -4.0F, 0.0F);
        rightHorn3.xRot = -1.2217F;
        rightHorn3.yRot = -0.2618F;
        rightHorn3.texOffs(56, 0).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        rightHorn2.addChild(rightHorn3);

        ModelRenderer rightHorn4 = new ModelRenderer(this);
        rightHorn4.setPos(-1.0F, -4.0F, 0.0F);
        rightHorn4.xRot = -1.2217F;
        rightHorn4.yRot = -0.2618F;
        rightHorn4.texOffs(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        rightHorn3.addChild(rightHorn4);

        ModelRenderer rightHorn5 = new ModelRenderer(this);
        rightHorn5.setPos(1.0F, -4.0F, 0.0F);
        rightHorn5.xRot = -1.0472F;
        rightHorn5.yRot = -0.2618F;
        rightHorn5.texOffs(58, 6).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        rightHorn4.addChild(rightHorn5);

        // combine horns
        horns = new ModelRenderer(this);
        horns.addChild(leftHorn);
        horns.addChild(rightHorn);
    }

    public void renderHorns(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, final int packedOverlayIn) {
        horns.copyFrom(head);
        horns.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    }

}
