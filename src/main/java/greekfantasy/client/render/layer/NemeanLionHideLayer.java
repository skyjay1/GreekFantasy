package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GFRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class NemeanLionHideLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends BipedArmorLayer<T, M, A> {

//  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/nemean_lion_hide.png");

    public NemeanLionHideLayer(IEntityRenderer<T, M> ientityrenderer, A model1, A model2) {
        super(ientityrenderer, model1, model2);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn != null && entitylivingbaseIn.getItemBySlot(EquipmentSlotType.HEAD).getItem() == GFRegistry.NEMEAN_LION_HIDE) {
            super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    protected void setPartVisibility(A modelIn, EquipmentSlotType slotIn) {
        modelIn.setAllVisible(false);
        if (slotIn == EquipmentSlotType.HEAD) {
            modelIn.head.visible = true;
            modelIn.hat.visible = true;
            modelIn.body.visible = true;
            modelIn.rightArm.visible = true;
            modelIn.leftArm.visible = true;
            modelIn.body.visible = true;
            modelIn.rightLeg.visible = true;
            modelIn.leftLeg.visible = true;
        }
    }
}