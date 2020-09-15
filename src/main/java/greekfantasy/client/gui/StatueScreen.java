package greekfantasy.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.client.network.CUpdateStatuePosePacket;
import greekfantasy.gui.StatueContainer;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class StatueScreen extends ContainerScreen<StatueContainer> {
  
  // CONSTANTS
  private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/statue.png");

  private static final int SCREEN_WIDTH = 224;
  private static final int SCREEN_HEIGHT = 208;
  
  private static final int PREVIEW_WIDTH = 52;
  private static final int PREVIEW_HEIGHT = 88;
  private static final int PREVIEW_X = 8;
  private static final int PREVIEW_Y = 8;
  
  private static final int PARTS_X = 147;
  private static final int PARTS_Y = 23;
  
  private static final int GENDER_X = 26;
  private static final int GENDER_Y = 90;
  
  private static final int RESET_X = 123;
  private static final int RESET_Y = 103;
  
  private static final int TEXT_X = 84;
  private static final int TEXT_Y = 7;
  private static final int TEXT_WIDTH = 116;
  private static final int TEXT_HEIGHT = 12;
  
  private static final int SLIDER_X = 69;
  private static final int SLIDER_Y = 30;
  private static final int SLIDER_HEIGHT = 20;
  private static final int SLIDER_SPACING = 4;

  private static final int BTN_WIDTH = 70;
  private static final int BTN_HEIGHT = 16;
  
  protected BlockPos blockPos = BlockPos.ZERO;
  protected StatuePose currentPose = StatuePoses.NONE;
  protected ModelPart selectedPart = ModelPart.HEAD;
  protected boolean isStatueFemale = false;
  protected String textureName;
  
  protected AngleSlider sliderAngleX;
  protected AngleSlider sliderAngleY;
  protected AngleSlider sliderAngleZ;

  public StatueScreen(final StatueContainer screenContainer, final PlayerInventory inv, final ITextComponent title) {
    super(screenContainer, inv, title);
    this.xSize = SCREEN_WIDTH;
    this.ySize = SCREEN_HEIGHT;
    this.playerInventoryTitleX = this.guiLeft + StatueContainer.PLAYER_INV_X;
    this.playerInventoryTitleY = this.guiTop + StatueContainer.PLAYER_INV_Y - 9;
    this.currentPose = screenContainer.getStatuePose();
    this.blockPos = screenContainer.getBlockPos();
    this.isStatueFemale = screenContainer.isStatueFemale();
    this.textureName = screenContainer.getTextureName();
    // send a request for updated pose information
    // GreekFantasy.CHANNEL.sendToServer(new CRequestStatuePoseUpdatePacket(screenContainer.getBlockPos()));
  }
  
  @Override
  public void init(Minecraft minecraft, int width, int height) {
    super.init(minecraft, width, height);
    // add part buttons
    for(int i = 0, l = ModelPart.values().length; i < l; i++) {
      final ModelPart p = ModelPart.values()[i];
      final ITextComponent title = new TranslationTextComponent("gui.statue." + p.getString());
      this.addButton(new StatueScreen.PartButton(this, this.guiLeft + PARTS_X, this.guiTop + PARTS_Y + (BTN_HEIGHT * i), title, button -> { 
          this.selectedPart = p;
          StatueScreen.this.updateSliders();
        }) {
        @Override
        protected boolean isSelected() { return this.isHovered() || (p == StatueScreen.this.selectedPart); }
      });
    }
    // add reset button
    final ITextComponent titleReset = new TranslationTextComponent("controls.reset");
    this.addButton(new StatueScreen.IconButton(this, this.guiLeft + RESET_X, this.guiTop + RESET_Y, 0, 240, titleReset, button -> {
      StatueScreen.this.currentPose.set(StatueScreen.this.selectedPart, 0, 0, 0);
      StatueScreen.this.updateSliders();
    }));
    // add gender button
    final ITextComponent titleGender = new TranslationTextComponent("gui.statue.gender");
    this.addButton(new StatueScreen.IconButton(this, this.guiLeft + GENDER_X, this.guiTop + GENDER_Y, 16, 240, titleGender, button -> StatueScreen.this.isStatueFemale = !StatueScreen.this.isStatueFemale) {
      @Override
      public int getIconX() { return super.getIconX() + (StatueScreen.this.isStatueFemale ? 0 : this.width); }
    });
    // add sliders
    this.sliderAngleX = (new StatueScreen.AngleSlider(this.guiLeft + SLIDER_X, this.guiTop + SLIDER_Y, "X") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setX((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).getX()); }
    });
    this.sliderAngleY = (new StatueScreen.AngleSlider(this.guiLeft + SLIDER_X, this.guiTop + SLIDER_Y + (SLIDER_HEIGHT + SLIDER_SPACING), "Y") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setY((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).getY()); }
    });
    this.sliderAngleZ = (new StatueScreen.AngleSlider(this.guiLeft + SLIDER_X, this.guiTop + SLIDER_Y + 2 * (SLIDER_HEIGHT + SLIDER_SPACING), "Z") {
      @Override
      void setAngleValue(double angRadians) { StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).setZ((float)angRadians); }
      @Override
      double getAngleValue() { return Math.toDegrees(StatueScreen.this.currentPose.getAngles(StatueScreen.this.selectedPart).getZ()); }
    });
    this.addButton(sliderAngleX);
    this.addButton(sliderAngleY);
    this.addButton(sliderAngleZ);
    this.updateSliders();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    this.renderBackground(matrixStack);
    RenderHelper.setupGuiFlatDiffuseLighting();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(SCREEN_TEXTURE);
    this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    // draw tile entity preview
    drawTileEntityOnScreen(matrixStack, this.guiLeft + PREVIEW_X, this.guiTop + PREVIEW_Y, 1, x, y, partialTicks);
  }
  
  @Override
  public void onClose() {
    super.onClose();
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
  
  public static void drawTileEntityOnScreen(final MatrixStack matrixStackIn, final int posX, final int posY, final int scale, 
      final float mouseX, final float mouseY, final float partialTicks) {
    // TODO
  }
 
  protected class PartButton extends Button {
            
    public PartButton(final StatueScreen screenIn, final int x, final int y, final ITextComponent title, final IPressable pressedAction) {
      super(x, y, BTN_WIDTH, BTN_HEIGHT, title, pressedAction);
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        final boolean selected = isSelected();
        final int xOffset = 0;
        final int yOffset = SCREEN_HEIGHT + (selected ? this.height : 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        StatueScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
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
      super(x, y, BTN_HEIGHT, BTN_HEIGHT, StringTextComponent.EMPTY, pressedAction, (button, matrix, mouseX, mouseY) -> {
        if(button.active) {
          screenIn.renderTooltip(matrix, screenIn.minecraft.fontRenderer.func_238425_b_(title, Math.max(screenIn.width / 2 - 43, 170)), mouseX, mouseY);
        }
      });
      this.textureX = tX;
      this.textureY = tY;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        int xOffset = BTN_WIDTH;
        int yOffset = SCREEN_HEIGHT + (this.isHovered() ? this.width : 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        StatueScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
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
      this.func_230979_b_();
    }

    // called when the value is changed
    protected void func_230979_b_() {
      this.setMessage(new TranslationTextComponent("gui.statue.rotation", rotationName, Math.round(getAngleValue())));
    }

    // called when the value is changed and is different from its previous value
    protected void func_230972_a_() {
      setAngleValue(Math.toRadians((this.sliderValue - 0.5D) * 180.0D));
    }

    protected double getValueRadians() {
      return Math.toRadians((this.sliderValue - 0.5D) * 180.0D);
    }
    
    public void updateSlider() {
      this.sliderValue = MathHelper.clamp((getAngleValue() / 180.0D) + 0.5D, 0.0D, 1.0D);
      this.func_230979_b_();
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
