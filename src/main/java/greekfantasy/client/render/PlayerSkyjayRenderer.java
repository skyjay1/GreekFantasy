package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PlayerSkyjayRenderer<T extends LivingEntity>  extends EntityRenderer<T> {
  
  private static final ResourceLocation DUMMY = new ResourceLocation(GreekFantasy.MODID, "textures/entity/sparti/sparti.png");
  private final PlayerModel<T> playerModel;
    
  public PlayerSkyjayRenderer(EntityRendererManager renderManagerIn) {
     super(renderManagerIn);
     playerModel = new PlayerModel<>(0.0F, false);
  }
  
  @Override
  public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    if (!entity.isInvisible()) {
      // prepare to render fire on top of head
      final float scale = 0.5F;
      matrixStackIn.push();
      playerModel.getModelHead().translateRotate(matrixStackIn);
      matrixStackIn.translate(0.5D * scale, -0.5, -0.5D * scale);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      matrixStackIn.scale(scale, -scale, -scale);
      // render fire here
      // note: packed light flag 15728640 uses world light, 15728880 uses constant/full light
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.SOUL_FIRE.getDefaultState(), 
          matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      // finish rendering
      matrixStackIn.pop();
    }
  }

  @Override
  public ResourceLocation getEntityTexture(T entity) {
    return DUMMY;
  }
}
