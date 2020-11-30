package greekfantasy.client.render.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public final class CharybdisModelHelper {
  
  private CharybdisModelHelper() { }

  protected static ModelRenderer makeBody1FringeModel(final Model model, final float angleY, 
      final ModelRenderer[] body1Fringe, final int index) {
    ModelRenderer body1fringe = new ModelRenderer(model);
    body1fringe.setRotationPoint(0.0F, 0.0F, 0.0F);
    body1fringe.rotateAngleY = angleY;
    
    ModelRenderer body1fringe1 = new ModelRenderer(model);
    body1fringe1.setRotationPoint(0.0F, 0.0F, -12.0F);
    body1fringe.addChild(body1fringe1);
    body1fringe1.setTextureOffset(0, 84).addBox(-12.0F, 0.0F, -4.0F, 24.0F, 1.0F, 5.0F, 0.0F, false);
    body1Fringe[index] = body1fringe1;
    return body1fringe;
  }
  
  protected static ModelRenderer makeBody2FringeModel(final Model model, final float angleY, 
      final ModelRenderer[] body2Fringe, final int index) {
    ModelRenderer body2fringe = new ModelRenderer(model);
    body2fringe.setRotationPoint(0.0F, 6.0F, 0.0F);
    body2fringe.rotateAngleY = angleY;

    ModelRenderer body2fringe1 = new ModelRenderer(model);
    body2fringe1.setRotationPoint(-5.0F, 0.0F, -8.0F);
    body2fringe.addChild(body2fringe1);
    body2fringe1.setTextureOffset(0, 89).addBox(-1.0F, -6.0F, -3.0F, 1.0F, 10.0F, 4.0F, 0.0F, false);
    body2Fringe[index] = body2fringe1;

    ModelRenderer body2fringe2 = new ModelRenderer(model);
    body2fringe2.setRotationPoint(0.0F, 0.0F, -8.0F);
    body2fringe.addChild(body2fringe2);
    body2fringe2.setTextureOffset(0, 89).addBox(-1.0F, -6.0F, -4.0F, 1.0F, 10.0F, 4.0F, 0.0F, false);
    body2Fringe[index + 1] = body2fringe2;

    ModelRenderer body2fringe3 = new ModelRenderer(model);
    body2fringe3.setRotationPoint(5.0F, 0.0F, -8.0F);
    body2fringe.addChild(body2fringe3);
    body2fringe3.setTextureOffset(0, 89).addBox(-1.0F, -6.0F, -3.0F, 1.0F, 10.0F, 4.0F, 0.0F, false);
    body2Fringe[index + 2] = body2fringe3;
    return body2fringe;
  }
  
  protected static ModelRenderer makeBody3FringeModel(final Model model, final float angleY, 
      final ModelRenderer[] body3Fringe, final int index) {
    ModelRenderer body3fringe = new ModelRenderer(model);
    body3fringe.setRotationPoint(-1.0F, -12.0F, 0.0F);
    body3fringe.rotateAngleY = angleY;
    
    ModelRenderer body3fringe1 = new ModelRenderer(model);
    body3fringe1.setRotationPoint(-2.0F, 0.0F, -5.0F);
    body3fringe.addChild(body3fringe1);
    body3fringe1.setTextureOffset(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
    body3Fringe[index] = body3fringe1;

    ModelRenderer body3fringe2 = new ModelRenderer(model);
    body3fringe2.setRotationPoint(1.0F, 0.0F, -5.0F);
    body3fringe.addChild(body3fringe2);
    body3fringe2.setTextureOffset(9, 89).addBox(-1.0F, -4.0F, -4.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
    body3Fringe[index + 1] = body3fringe2;
    
    ModelRenderer body3fringe3 = new ModelRenderer(model);
    body3fringe3.setRotationPoint(4.0F, 0.0F, -5.0F);
    body3fringe.addChild(body3fringe3);
    body3fringe3.setTextureOffset(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
    body3Fringe[index + 2] = body3fringe3;
    return body3fringe;
  }
  
  protected static ModelRenderer makeBody4FringeModel(final Model model, final float angleY, 
      final ModelRenderer[] body4Fringe, final int index) {
    ModelRenderer body4fringe = new ModelRenderer(model);
    body4fringe.setRotationPoint(0.0F, -4.0F, 0.0F);
    body4fringe.setTextureOffset(0, 57).addBox(-7.0F, -22.0F, -8.0F, 16.0F, 10.0F, 16.0F, 0.0F, false);
    body4fringe.rotateAngleY = angleY;

    ModelRenderer body4fringe1 = new ModelRenderer(model);
    body4fringe1.setRotationPoint(0.0F, 0.0F, -3.0F);
    body4fringe.addChild(body4fringe1);
    body4fringe1.setTextureOffset(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, 0.0F, false);
    body4Fringe[index] = body4fringe1;
    
    return body4fringe;
  }
  
  protected static ModelRenderer makeTeethModel(final Model model, final float angleY) {
    ModelRenderer teeth = new ModelRenderer(model);
    teeth.setRotationPoint(0.0F, -30.0F, 0.0F);
    teeth.rotateAngleY = angleY;
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, 8.5F, -0.5672F, -0.1309F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, 6.0F, -0.4363F, -0.0873F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, 3.5F, -0.2618F, 0.2182F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, 0.5F, -0.0436F, -0.1309F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, -2.5F, 0.2618F, 0.1745F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, -5.0F, 0.4363F, 0.1309F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -10.0F, 0.0F, -7.5F, 0.5672F, -0.0873F, -2.0944F, false));
    teeth.addChild(makeToothModel(model, -9.0F, -0.5F, -10.0F, 0.7854F, 0.0873F, -2.0944F, true));
    return teeth;
  }
  
  protected static ModelRenderer makeToothModel(final Model model, final float rotX, final float rotY, final float rotZ, 
      final float angleX, final float angleY, final float angleZ, final boolean isLong) {
    ModelRenderer tooth = new ModelRenderer(model);
    tooth.setRotationPoint(rotX, rotY, rotZ);
    tooth.rotateAngleX = angleX;
    tooth.rotateAngleY = angleY;
    tooth.rotateAngleZ = angleZ;
    tooth.setTextureOffset(61, 31);
    if(isLong) {
      tooth.addBox(0.0F, -0.5F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
    } else {
      tooth.addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
    }
    return tooth;
  }
  
  protected static ModelRenderer makeArmsModel(final Model model, final float angleY, final ArmModel[] armsArray, int startIndex) {
    ModelRenderer armsTrio = new ModelRenderer(model);
    armsTrio.setRotationPoint(0.0F, 0.0F, 0.0F);
    armsTrio.rotateAngleY = angleY;
    armsArray[startIndex] = new ArmModel(model, -12.0F, -1.0F, 8.0F, -0.157F);
    armsArray[startIndex + 1] = new ArmModel(model, -12.0F, -1.0F, 0.0F, 0);
    armsArray[startIndex + 2] = new ArmModel(model, -12.0F, -1.0F, -8.0F, 0.157F);
    armsTrio.addChild(armsArray[startIndex]);
    armsTrio.addChild(armsArray[startIndex + 1]);
    armsTrio.addChild(armsArray[startIndex + 2]);
    return armsTrio;
  }
  
  /**
   * Wrapper class that holds a few models that make up
   * an arm for the entity. Also handles animation.
   **/
  protected static class ArmModel extends ModelRenderer {
    
    private float rotateAngleX;
    private final ModelRenderer armLower;
    private final ModelRenderer armMiddle;
    private final ModelRenderer armEnd;

    public ArmModel(Model model, final float rotX, final float rotY, final float rotZ, final float angleX) {
      super(model);
      rotateAngleX = angleX;
      this.setRotationPoint(0, 28, 0);
      
      armLower = new ModelRenderer(model);
      armLower.setRotationPoint(rotX, rotY, rotZ);
      armLower.rotateAngleX = angleX;
      this.addChild(armLower);
      armLower.setTextureOffset(73, 0).addBox(-1.0F, -5.0F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);

      armMiddle = new ModelRenderer(model);
      armMiddle.setRotationPoint(0.5F, -5.0F, 0.0F);
      armLower.addChild(armMiddle);
      armMiddle.setTextureOffset(86, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

      armEnd = new ModelRenderer(model);
      armEnd.setRotationPoint(0.0F, -5.0F, 0.0F);
      armMiddle.addChild(armEnd);
      armEnd.setTextureOffset(95, 0).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);
      armEnd.setTextureOffset(73, 10).addBox(-1.0F, -8.0F, -2.5F, 1.0F, 8.0F, 5.0F, 0.0F, false);
    }
    
    public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float idleSwingCos) {
      // animate arms
      armLower.rotateAngleX = rotateAngleX - idleSwingCos * 0.14F;
      armLower.rotateAngleZ = -1.2217F + idleSwingCos * 0.09F;
      armMiddle.rotateAngleZ = idleSwingCos * 0.14F;
      armEnd.rotateAngleZ = idleSwingCos * 0.14F;
    }
    
  }
}
