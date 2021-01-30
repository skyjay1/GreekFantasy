package greekfantasy.client.render.tileentity;

import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.client.gui.StatueScreen;
import greekfantasy.client.render.model.tileentity.StatueModel;
import greekfantasy.deity.Deity;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.ModelPart;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;


public class StatueTileEntityRenderer extends TileEntityRenderer<StatueTileEntity> {
  
  protected static final ResourceLocation STEVE_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/steve.png");
  protected static final ResourceLocation ALEX_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/alex.png");
    
  protected StatueModel<StatueTileEntity> model;

  public StatueTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
    this.model = new StatueModel<StatueTileEntity>();
  }

  @Override
  public void render(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    // get the right TileEntity (always use the lower one)
    final Minecraft mc = Minecraft.getInstance();
    final boolean gui = mc.currentScreen instanceof StatueScreen;
    final boolean upper = tileEntityIn.isUpper();
    StatueTileEntity te = tileEntityIn;
    if(upper) {
      final TileEntity tmp = tileEntityIn.getWorld().getTileEntity(tileEntityIn.getPos().down());
      if(tmp instanceof StatueTileEntity) {
        te = (StatueTileEntity)tmp;
      }
    }
    // determine texture, rotations, and style
    final float rotation = te.getBlockState().get(StatueBlock.HORIZONTAL_FACING).getHorizontalAngle();
    final boolean isFemaleModel = te.isStatueFemale();
    final float translateY = upper ? 0.95F : 1.95F;
    final StatueMaterial material = te.getStatueMaterial();
    final ResourceLocation textureStone = te.hasDeity() ? te.getDeity().getOverlay() : material.getStoneTexture();
    final ResourceLocation textureOverlay = getOverlayTexture(te);
    matrixStackIn.push();
    // render base
    if(!upper && !gui) {
      final BlockState base = te.hasDeity() ? te.getDeity().getBaseBlock() : material.getBase();
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(base, 
          matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, EmptyModelData.INSTANCE);
    }
    // prepare to render player texture
    matrixStackIn.translate(0.5D, (double)translateY, 0.5D);
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
    if(!gui) {
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
    }
    // set rotation angles
    model.setRotationAngles(te, partialTicks);
    this.model.rotateAroundBody(te.getRotations(ModelPart.BODY), matrixStackIn, partialTicks);
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(textureOverlay));  
    if(material.hasSkin()) {
      // render player texture
      this.model.render(te, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F, upper, isFemaleModel);
    }
    // prepare to render stone texture
    if(textureStone != null) {
      vertexBuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(textureStone));
      float alpha = 1.0F;
      if(material.hasSkin()) {
        alpha = 0.3F;
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.alphaFunc(516, 0.0F);
      }
      // render stone texture
      this.model.render(te, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 0.8F, 0.8F, 0.8F, alpha, upper, isFemaleModel);
      // reset RenderSystem values
      if(material.hasSkin()) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableBlend();
      }
    }
    // render held items
    renderHeldItems(te, partialTicks, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, upper);
    matrixStackIn.pop();
  }

  protected ResourceLocation getOverlayTexture(final StatueTileEntity te) {
    // return deity texture
    if(te.getDeity() != Deity.EMPTY) {
      return te.getDeity().getTexture();
    }
    // return player texture
    final GameProfile gameProfile = te.getPlayerProfile();
    final boolean isFemale = te.isStatueFemale();
    if(gameProfile != null) {
      Minecraft minecraft = Minecraft.getInstance();
      Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(gameProfile);
      if(map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
        return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
      }
    }
    // return default texture
    return isFemale ? ALEX_TEXTURE : STEVE_TEXTURE;
  }
  
  protected void renderHeldItems(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
      IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, boolean upper) {
    if(upper) {
      ItemStack itemstackRight = tileEntityIn.getItem(HandSide.RIGHT);
      ItemStack itemstackLeft = tileEntityIn.getItem(HandSide.LEFT);
      if (!itemstackLeft.isEmpty() || !itemstackRight.isEmpty()) {
         matrixStackIn.push();
         this.renderItem(tileEntityIn, itemstackRight, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
         this.renderItem(tileEntityIn, itemstackLeft, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
         matrixStackIn.pop();
      }
    }
  }

  protected void renderItem(StatueTileEntity tileEntity, ItemStack stack, ItemCameraTransforms.TransformType transform,
      HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn) {
    matrixStackIn.push();
    this.model.translateHand(handSide, matrixStackIn);
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
    boolean flag = handSide == HandSide.LEFT;
    matrixStackIn.translate((double) ((float) (flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
    Minecraft.getInstance().getItemRenderer().renderItem(null, stack, transform, flag, matrixStackIn, bufferIn,
        tileEntity.getWorld(), packedLightIn, packedOverlayIn);
    matrixStackIn.pop();
  }

}
