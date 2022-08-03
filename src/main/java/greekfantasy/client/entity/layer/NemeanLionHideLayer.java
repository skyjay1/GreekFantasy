package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GFRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class NemeanLionHideLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {

    public NemeanLionHideLayer(RenderLayerParent<T, M> parent, A model1, A model2) {
        super(parent, model1, model2);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.NEMEAN_LION_HIDE.get())) {
            super.render(poseStack, multiBufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    protected void setPartVisibility(A modelIn, EquipmentSlot slotIn) {
        modelIn.setAllVisible(true);
        modelIn.head.visible = false;
        modelIn.leftLeg.visible = false;
        modelIn.rightLeg.visible = false;
    }
}