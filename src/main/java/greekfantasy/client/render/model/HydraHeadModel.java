package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HydraHeadModel<T extends HydraHeadEntity> extends EntityModel<T> {

    private final ModelRenderer neck;
    private final ModelRenderer neckSevered;
    private final ModelRenderer neck1;
    private final ModelRenderer neck2;
    private final ModelRenderer neck3;
    private final ModelRenderer head;
    private final ModelRenderer mouth;

    private float spawnPercent;
    private boolean severed;
    private boolean charred;

    public HydraHeadModel() {
        super();
        texWidth = 64;
        texHeight = 32;

        neckSevered = new ModelRenderer(this);
        neckSevered.setPos(0.0F, 24.0F, 1.0F);
        neckSevered.texOffs(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
        neckSevered.texOffs(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        neck = new ModelRenderer(this);
        neck.setPos(0.0F, 22.0F, 0.0F); // neck.setRotationPoint(0.0F, 22.0F, 4.0F);

        neck1 = new ModelRenderer(this);
        neck1.setPos(0.0F, 2.0F, -3.0F);
        neck1.xRot = 0.5236F;
        neck1.texOffs(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
        neck1.texOffs(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        neck.addChild(neck1);

        neck2 = new ModelRenderer(this);
        neck2.setPos(0.0F, -9.0F, 0.0F);
        neck2.xRot = -0.3491F;
        neck2.texOffs(0, 16).addBox(-2.01F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
        neck2.texOffs(25, 22).addBox(-1.0F, -10.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        neck1.addChild(neck2);

        neck3 = new ModelRenderer(this);
        neck3.setPos(0.0F, -10.0F, 0.0F);
        neck3.xRot = -0.1745F;
        neck3.texOffs(0, 16).addBox(-2.0F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
        neck3.texOffs(25, 22).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        neck2.addChild(neck3);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -9.0F, 3.0F);
        neck3.addChild(head);
        head.texOffs(0, 0).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 8.0F, 8.0F, 0.0F, false);
        head.texOffs(33, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
        head.texOffs(38, 25).addBox(-2.5F, -3.25F, -9.5F, 5.0F, 3.0F, 4.0F, 0.0F, false);
        head.texOffs(25, 22).addBox(-1.0F, -8.0F, 4.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        mouth = new ModelRenderer(this);
        mouth.setPos(0.0F, -1.0F, -4.0F);
        mouth.texOffs(33, 9).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        head.addChild(mouth);

        ModelRenderer leftHorn = new ModelRenderer(this);
        leftHorn.setPos(3.0F, -6.0F, 1.0F);
        leftHorn.zRot = 0.5236F;
        leftHorn.texOffs(56, 27).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
        head.addChild(leftHorn);

        ModelRenderer leftHorn2 = new ModelRenderer(this);
        leftHorn2.setPos(0.0F, -3.0F, 0.0F);
        leftHorn2.xRot = 0.5236F;
        leftHorn2.yRot = 0.0873F;
        leftHorn2.texOffs(56, 22).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
        leftHorn.addChild(leftHorn2);

        ModelRenderer leftHorn3 = new ModelRenderer(this);
        leftHorn3.setPos(0.0F, -3.0F, 0.0F);
        leftHorn3.xRot = 1.0472F;
        leftHorn3.yRot = 0.0873F;
        leftHorn3.texOffs(56, 17).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        leftHorn2.addChild(leftHorn3);

        ModelRenderer rightHorn1 = new ModelRenderer(this);
        rightHorn1.setPos(-3.0F, -6.0F, 1.0F);
        rightHorn1.zRot = -0.5236F;
        rightHorn1.texOffs(56, 27).addBox(0.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
        head.addChild(rightHorn1);

        ModelRenderer rightHorn2 = new ModelRenderer(this);
        rightHorn2.setPos(1.0F, -3.0F, 0.0F);
        rightHorn2.xRot = 0.5236F;
        rightHorn2.yRot = -0.0873F;
        rightHorn2.texOffs(56, 22).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
        rightHorn1.addChild(rightHorn2);

        ModelRenderer rightHorn3 = new ModelRenderer(this);
        rightHorn3.setPos(0.0F, -3.0F, 0.0F);
        rightHorn3.xRot = 1.0472F;
        rightHorn3.yRot = -0.0873F;
        rightHorn3.texOffs(56, 17).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        rightHorn2.addChild(rightHorn3);

    }


    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // rotate head angles
        float yaw = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = yaw * 0.4F;
        neck1.yRot = yaw * 0.1F;
        neck2.yRot = yaw * 0.2F;
        neck3.yRot = yaw * 0.3F;
        // copy head angles to severed portion
        neckSevered.xRot = neck1.xRot + neck.xRot;
        neckSevered.yRot = neck1.yRot + neck.yRot;
        neckSevered.zRot = neck1.zRot + neck.zRot;
        neckSevered.setPos(neck.x, neck.y + 2, neck.z - 3);
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        final float idleSwingCos = MathHelper.cos((31 * entityIn.getId() + entityIn.tickCount + partialTick) * 0.19F);
        // used to move the entire neck in a swinging motion (bite attack)
        final float swingPercent = entityIn.getAttackAnim(partialTick);
        // alternative that does not use cos: swing = 2.0D * Math.min(swingPercent - 0.5D, -(swingPercent - 0.5D)) + 1.0D;
        final float swing = MathHelper.cos((swingPercent - 0.5F) * (float) Math.PI);
        // animate mouth and neck parts
        mouth.xRot = (0.2618F + 0.45F * swing + 0.08F * idleSwingCos);
        neck1.xRot = 0.5236F + swing * 0.78F;
        neck2.xRot = -0.3491F + idleSwingCos * 0.04F;
        neck3.xRot = -0.1745F + idleSwingCos * 0.02F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        matrixStackIn.translate(0, 0.0D, 0.25D);
        // render the severed neck portion
        neckSevered.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        // render the rest of the head if growing
        if (!severed && !charred && spawnPercent > 0.0F) {
            // scale according to spawn percent and render normally
            float scale = spawnPercent;
            matrixStackIn.translate(0, (1.0F - scale), 0);
            matrixStackIn.scale(scale, scale, scale);
            // render neck (and all children)
            neck.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        }
    }

    public void setSpawnPercent(final float spawnPercentIn) {
        spawnPercent = spawnPercentIn;
    }

    public void setSevered(final boolean severedIn) {
        severed = severedIn;
    }

    public void setCharred(final boolean charredIn) {
        charred = charredIn;
    }

}
