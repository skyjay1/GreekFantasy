package greekfantasy.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.gui.StatueContainer;
import greekfantasy.network.CUpdateStatuePosePacket;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraft.client.gui.widget.button.Button.IPressable;

public class StatueScreen extends ContainerScreen<StatueContainer> {
  
  // CONSTANTS
  private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/statue.png");

  private static final int SCREEN_WIDTH = 224;
  private static final int SCREEN_HEIGHT = 202;
  
  private static final int PREVIEW_WIDTH = 52;
  private static final int PREVIEW_HEIGHT = 86;
  private static final int PREVIEW_X = 8;
  private static final int PREVIEW_Y = 8;
  
  private static final int PARTS_X = 147;
  private static final int PARTS_Y = 7;
  
  private static final int GENDER_X = 26;
  private static final int GENDER_Y = 90;
  
  private static final int PRESET_X = 69;
  private static final int PRESET_Y = 87;
  
  private static final int RESET_X = 123;
  private static final int RESET_Y = 87;
  
  private static final int SLIDER_X = 69;
  private static final int SLIDER_Y = 14;
  private static final int SLIDER_HEIGHT = 20;
  private static final int SLIDER_SPACING = 4;

  private static final int BTN_WIDTH = 70;
  private static final int BTN_HEIGHT = 16;
  
  protected BlockPos blockPos = BlockPos.ZERO;
  protected Direction blockRotation = Direction.NORTH;
  protected StatuePose currentPose = StatuePoses.NONE;
  protected ModelPart selectedPart = ModelPart.BODY;
  protected boolean isStatueFemale = false;
  protected String textureName = "";
    
  protected AngleSlider sliderAngleX;
  protected AngleSlider sliderAngleY;
  protected AngleSlider sliderAngleZ;

  public StatueScreen(final StatueContainer screenContainer, final PlayerInventory inv, final ITextComponent title) {
    super(screenContainer, inv, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.inventoryLabelX = this.leftPos + StatueContainer.PLAYER_INV_X;
    this.inventoryLabelY = this.topPos + StatueContainer.PLAYER_INV_Y - 10;
    this.currentPose = screenContainer.getStatuePose();
    this.blockPos = screenContainer.getBlockPos();
    this.blockRotation = screenContainer.getBlockRotation();
    this.isStatueFemale = screenContainer.isStatueFemale();
    this.textureName = screenContainer.getProfile();
  }
  
  @Override
  public void init(Minecraft minecraft, int width, int height) {
    super.init(minecraft, width, height);
    // add part buttons
    for(int i = 0, l = ModelPart.values().length; i < l; i++) {
      final ModelPart p = ModelPart.values()[i];
      final ITextComponent title = new TranslationTextComponent("gui.statue." + p.getSerializedName());
      this.addButton(new StatueScreen.PartButton(this, this.leftPos + PARTS_X, this.topPos + PARTS_Y + (BTN_HEIGHT * i), title, button -> { 
          this.selectedPart = p;
          StatueScreen.this.updateSliders();
        }) {
        @Override
        protected boolean isSelected() { return this.isHovered() || (p == StatueScreen.this.selectedPart); }
      });
    }
    // add reset button
    final ITextComponent titleReset = new TranslationTextComponent("controls.reset");
    this.addButton(new StatueScreen.IconButton(this, this.leftPos + RESET_X, this.topPos + RESET_Y, 0, 234, titleReset, button -> {
      StatueScreen.this.currentPose.set(StatueScreen.this.selectedPart, 0, 0, 0);
      StatueScreen.this.updateSliders();
    }));
    // add gender button
    final ITextComponent titleGender = new TranslationTextComponent("gui.statue.gender");
    this.addButton(new StatueScreen.IconButton(this, this.leftPos + GENDER_X, this.topPos + GENDER_Y, 16, 234, titleGender, button -> StatueScreen.this.isStatueFemale = !StatueScreen.this.isStatueFemale) {
      @Override
      public int getIconX() { return super.getIconX() + (StatueScreen.this.isStatueFemale ? 0 : this.width); }
    });
    // add preset button
    final ITextComponent titlePreset = new TranslationTextComponent("gui.statue.preset");
    this.addButton(new StatueScreen.IconButton(this, this.leftPos + PRESET_X, this.topPos + PRESET_Y, 48, 234, titlePreset, button -> {
      StatueScreen.this.currentPose = StatuePoses.getRandomPose(StatueScreen.this.minecraft.level.random);
      StatueScreen.this.updateSliders();
    }));
    // add sliders
    this.sliderAngleX = (new StatueScreen.AngleSlider(this.leftPos + SLIDER_X, this.topPos + SLIDER_Y, "X") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setX((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).x()); }
    });
    this.sliderAngleY = (new StatueScreen.AngleSlider(this.leftPos + SLIDER_X, this.topPos + SLIDER_Y + (SLIDER_HEIGHT + SLIDER_SPACING), "Y") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setY((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).y()); }
    });
    this.sliderAngleZ = (new StatueScreen.AngleSlider(this.leftPos + SLIDER_X, this.topPos + SLIDER_Y + 2 * (SLIDER_HEIGHT + SLIDER_SPACING), "Z") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setZ((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).z()); }
    });
    this.addButton(sliderAngleX);
    this.addButton(sliderAngleY);
    this.addButton(sliderAngleZ);
    this.updateSliders();
  }

  @Override
  protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    this.renderBackground(matrixStack);
    RenderHelper.setupForFlatItems();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bind(SCREEN_TEXTURE);
    this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
  }
  
  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    // draw tile entity preview
    drawTileEntityOnScreen(matrixStack, this.leftPos + PREVIEW_X, this.topPos + PREVIEW_Y, mouseX, mouseY, partialTicks);
    // draw hovering text LAST
    for(final Widget b : this.buttons) {
      if(b.visible && b.isHovered()) {
        b.renderToolTip(matrixStack, mouseX, mouseY);
      }
    }
    this.renderTooltip(matrixStack, mouseX, mouseY);
  }
  
  @Override
  public void removed() {
    super.removed();
    // send update packet to server
    GreekFantasy.CHANNEL.sendToServer(new CUpdateStatuePosePacket(this.blockPos, this.currentPose, this.isStatueFemale, this.textureName));
  }
  
  protected void updateSliders() {
    if(sliderAngleX != null) {
      this.sliderAngleX.updateSlider();
      this.sliderAngleY.updateSlider();
      this.sliderAngleZ.updateSlider();
    }
  }
  
  public void setBlockPos(final BlockPos pos) {
    this.blockPos = pos;
  }
  
  public void setStatuePose(final StatuePose pose) {
    this.currentPose = pose;
    this.updateSliders();
  }
  
  @SuppressWarnings("deprecation")
  public void drawTileEntityOnScreen(final MatrixStack matrixStackIn, final int posX, final int posY, 
      final float mouseX, final float mouseY, final float partialTicks) {
    float margin = 12;
    float scale = PREVIEW_WIDTH - margin * 2;
    float rotX = (float)Math.atan((double)((mouseX - this.leftPos) / 40.0F));
    float rotY = (float)Math.atan((double)((mouseY - this.topPos - PREVIEW_HEIGHT / 2) / 40.0F));
    final TileEntity teMain = minecraft.level.getBlockEntity(this.blockPos);
    final boolean isUpper = teMain.getBlockState().getValue(StatueBlock.HALF) == DoubleBlockHalf.UPPER;
    final TileEntity teOther = minecraft.level.getBlockEntity(isUpper ? this.blockPos.below() : this.blockPos.above());
    // preview client-side tile entity information
    if(teMain instanceof StatueTileEntity && teOther instanceof StatueTileEntity) {
      final StatueTileEntity statueMain = (StatueTileEntity)teMain;
      final StatueTileEntity statueOther = (StatueTileEntity)teOther;
      updateStatueTileEntity(statueMain);
      updateStatueTileEntity(statueOther);
   
      // Render the Block with given scale
      RenderSystem.pushMatrix();
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef(posX + margin, posY + margin, 100.0F + 10.0F);
      RenderSystem.translatef(0.0F, PREVIEW_HEIGHT - margin * 1.75F, 0.0F);
      //RenderSystem.rotatef(this.blockRotation.getOpposite().getHorizontalAngle(), 0.0F, -1.0F, 0.0F);
      RenderSystem.scalef(1.0F, -1.0F, 1.0F);
      RenderSystem.scalef(scale, scale, scale);
      RenderSystem.rotatef(rotX * 15.0F, 0.0F, 1.0F, 0.0F);
      RenderSystem.rotatef(rotY * 15.0F, 1.0F, 0.0F, 0.0F);
      
      RenderHelper.setupForFlatItems();
  
      IRenderTypeBuffer.Impl bufferType = minecraft.renderBuffers().bufferSource();
      TileEntityRendererDispatcher.instance.getRenderer(statueMain).render(statueMain, partialTicks, matrixStackIn, bufferType, 15728880, OverlayTexture.NO_OVERLAY);
      bufferType.endBatch();

      RenderSystem.translatef(0.0F, 1.0F, 0.0F);
      TileEntityRendererDispatcher.instance.getRenderer(statueOther).render(statueOther, partialTicks, matrixStackIn, bufferType, 15728880, OverlayTexture.NO_OVERLAY);
      bufferType.endBatch();
      RenderSystem.enableDepthTest();
      RenderHelper.setupFor3DItems();
      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
    }
  }
  
  /**
   * Updates the client-side model for rendering in the GUI only.
   * Does not send anything to the server.
   * @param te the StatueTileEntity to update
   **/
  private void updateStatueTileEntity(final StatueTileEntity te) {
    te.setStatuePose(this.currentPose);
    te.setStatueFemale(this.isStatueFemale);
    te.setTextureName(this.textureName);
  }
 
  protected class PartButton extends Button {
            
    public PartButton(final StatueScreen screenIn, final int x, final int y, final ITextComponent title, final IPressable pressedAction) {
      super(x, y, BTN_WIDTH, BTN_HEIGHT, title, pressedAction);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        final boolean selected = isSelected();
        final int xOffset = 0;
        final int yOffset = SCREEN_HEIGHT + (selected ? this.height : 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        StatueScreen.this.getMinecraft().getTextureManager().bind(SCREEN_TEXTURE);
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        drawCenteredString(matrixStack, StatueScreen.this.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor() | MathHelper.ceil(this.alpha * 255.0F) << 24);
      }
    }
    
    protected boolean isSelected() {
      return this.isHovered();
    }
  }
  
  protected class IconButton extends Button {
    
    private final int textureX;
    private final int textureY;
    
    public IconButton(final StatueScreen screenIn, final int x, final int y, final int tX, final int tY,
        final ITextComponent title, final IPressable pressedAction) {
      super(x, y, BTN_HEIGHT, BTN_HEIGHT, StringTextComponent.EMPTY, pressedAction, (b, m, bx, by) -> screenIn.renderTooltip(m, screenIn.minecraft.font.split(title, Math.max(screenIn.width / 2 - 43, 170)), bx, by));
      this.textureX = tX;
      this.textureY = tY;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        int xOffset = BTN_WIDTH;
        int yOffset = SCREEN_HEIGHT + (this.isHovered() ? this.width : 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        StatueScreen.this.getMinecraft().getTextureManager().bind(SCREEN_TEXTURE);
        // draw button background
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        // draw button icon
        this.blit(matrixStack, this.x, this.y, getIconX(), getIconY(), this.width, this.height);
      }
    }
    
    public int getIconX() {
      return textureX;
    }
    
    public int getIconY() {
      return textureY;
    }
  }
  
  protected abstract class AngleSlider extends AbstractSlider {
    
    private final String rotationName;

    public AngleSlider(final int x, final int y, final String rName) {
      super(x, y, BTN_WIDTH, SLIDER_HEIGHT, StringTextComponent.EMPTY, 0.5D);
      rotationName = rName;
      this.updateMessage();
    }

    // called when the value is changed
    protected void updateMessage() {
      this.setMessage(new TranslationTextComponent("gui.statue.rotation", rotationName, Math.round(getAngleValue())));
    }

    // called when the value is changed and is different from its previous value
    protected void applyValue() {
      setAngleValue(Math.toRadians((this.value - 0.5D) * getAngleBounds()));
    }

    protected double getValueRadians() {
      return Math.toRadians((this.value - 0.5D) * getAngleBounds());
    }
    
    public void updateSlider() {
      this.value = MathHelper.clamp((getAngleValue() / getAngleBounds()) + 0.5D, 0.0D, 1.0D);
      this.updateMessage();
    }
    
    /** @return the range of angles that the slider outputs, in degrees **/
    protected double getAngleBounds() {
      return 360.0D;
    }

    /** @return the angle to display to the user, in degrees **/
    abstract double getAngleValue();

    /**
     * Updates this slider's value with an angle in radians
     * @param angRadians the angle in radians
     **/
    abstract void setAngleValue(final double angRadians);
  }

}
