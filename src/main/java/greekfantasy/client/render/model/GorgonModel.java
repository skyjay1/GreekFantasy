package greekfantasy.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;

public class GorgonModel<T extends GorgonEntity> extends DrakainaModel<T> {
  private final ModelRenderer chest;
  
  private final ModelRenderer snakeHair;
  private final List<ModelRenderer> snakeHair1 = new ArrayList<>();
  private final List<ModelRenderer> snakeHair2 = new ArrayList<>();
  private final List<ModelRenderer> snakeHair3 = new ArrayList<>();

  public GorgonModel(final float modelSize) {
    super(modelSize);

    bipedHeadwear.showModel = false;

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -4.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 17).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);

    this.snakeHair = new ModelRenderer(this);
    this.snakeHair.setRotationPoint(0.0F, 0.0F, 0.0F);
  
    makeSnakes(snakeHair1, 3.8F, (float) Math.PI / 6.0F, modelSize);
    makeSnakes(snakeHair2, 2.25F, (float) Math.PI / 4.0F, modelSize);
    makeSnakes(snakeHair3, 1.25F, (float) Math.PI / 3.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.chest)); }
  
  @Override
  public boolean canAnimateBow(final T entity, final ItemStack heldItem) {
    return entity.isMedusa() && super.canAnimateBow(entity, heldItem);
  }
  
  public void renderSnakeHair(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, 
      final int packedOverlayIn, final float ticks, final float colorAlpha) {
    // living animations for each list
    animateSnakes(snakeHair1, ticks, 1.7F);
    animateSnakes(snakeHair2, ticks, 1.03F);
    animateSnakes(snakeHair3, ticks, 0.82F);
    // render each list
    this.snakeHair.copyModelAngles(this.bipedHead);
    this.snakeHair.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, colorAlpha);
  }
  
  private void animateSnakes(final List<ModelRenderer> list, final float ticks, final float baseAngleX) {
    int i = 0;
    for(final ModelRenderer m : list) {
      // update rotation angles
      m.rotateAngleX = baseAngleX + (float) Math.cos(ticks * 0.15 + i * 2.89F) * 0.08F;
      i++;
    }
  }
  
  private void makeSnakes(final List<ModelRenderer> list, final float radius, final float deltaAngle, final float modelSize) {
    for(double angle = 0.0D, count = 1.0D; angle < Math.PI * 2; angle += deltaAngle) {
      final float ptX = (float) (Math.cos(angle) * radius);
      final float ptZ = (float) (Math.sin(angle) * radius);
      final float angY = (float) (angle - (deltaAngle * 2 * count));
      final ModelRenderer snake = makeSnake(this, ptX, -8.5F, ptZ, 0, angY, 0, 46, 52);
      list.add(snake);
      this.snakeHair.addChild(snake);
      count++;
    }
  }
  
  public static ModelRenderer makeSnake(final Model model, final float rotX, final float rotY, final float rotZ, 
      final float angleX, final float angleY, final float angleZ, final int textureX, final int textureY) {
    final ModelRenderer snakeHair1 = new ModelRenderer(model);
    snakeHair1.setRotationPoint(rotX, rotY, rotZ);
    snakeHair1.rotateAngleX = angleX;
    snakeHair1.rotateAngleY = angleY;
    snakeHair1.rotateAngleZ = angleZ;
    snakeHair1.setTextureOffset(textureX, textureY).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F);

    final ModelRenderer snakeHair2 = new ModelRenderer(model);
    snakeHair2.setRotationPoint(0.0F, -3.0F, 0.0F);
    snakeHair1.addChild(snakeHair2);
    snakeHair2.rotateAngleX = 0.5236F;
    snakeHair2.setTextureOffset(textureX, textureY + 4).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F);

    final ModelRenderer snakeHair3 = new ModelRenderer(model);
    snakeHair3.setRotationPoint(0.0F, -3.0F, -0.5F);
    snakeHair2.addChild(snakeHair3);
    snakeHair3.rotateAngleX = 0.5236F;
    snakeHair3.setTextureOffset(textureX, textureY + 8).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F);
    
    return snakeHair1;
  }
}
