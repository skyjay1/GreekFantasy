package greekfantasy.client.gui;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class ScrollButton extends Button {
  
  /** A consumer to handle when the scroll starts **/
  final Consumer<ScrollButton> scrollStartHandler;
  /** A consumer to handle when the scroll ends **/
  final Consumer<ScrollButton> scrollEndHandler;
  /** Amount scrolled (0.0 = top, 1.0 = bottom) **/
  private float scrollAmount;
  /** If the scroll bar is enabled **/
  private boolean enabled;
  
  private final Screen screen;
  private final ResourceLocation texture;
  private final int u;
  private final int v;
  private final int uWidth = 12;
  private final int vHeight = 16;

  public ScrollButton(final Screen gui, final boolean isEnabled, final int x, final int y, 
      final int width, final int height, final int uX, final int vY, final ResourceLocation textureIn, 
      final Consumer<ScrollButton> onScrollStart, final Consumer<ScrollButton> onScrollEnd) {
    super(x, y, width, height, StringTextComponent.EMPTY, b -> {});
    screen = gui;
    u = uX;
    v = vY;
    texture = textureIn;
    enabled = isEnabled;
    scrollStartHandler = onScrollStart;
    scrollEndHandler = onScrollEnd;
    scrollAmount = 0;
    scrollEndHandler.accept(this);
  }
  
  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if(this.visible) {
      screen.getMinecraft().getTextureManager().bindTexture(texture);
      final int uOffset = this.enabled ? 0 : uWidth;
      final int yOffset = MathHelper.clamp((int)(scrollAmount * this.height - this.vHeight / 2), 0, this.height - vHeight + 1);
      this.blit(matrixStack, this.x + 1, this.y + yOffset, u + uOffset, v, uWidth, vHeight);
    }
  }
  
  @Override
  public void onClick(final double mouseX, final double mouseY) {
    if(enabled) {
      scrollStartHandler.accept(this);
      updateScrollAmount(mouseX, mouseY);
      scrollEndHandler.accept(this);
    }
  }
  
  @Override
  public void onDrag(final double mouseX, final double mouseY, final double dragX, final double dragY) {
    if(enabled) {
      scrollStartHandler.accept(this);
      updateScrollAmount(mouseX, mouseY);
    }
  }
  
  @Override
  public void onRelease(final double mouseX, final double mouseY) {
    if(enabled) {
      updateScrollAmount(mouseX, mouseY);
      scrollEndHandler.accept(this);
    }
  }
  
  private void updateScrollAmount(final double mouseX, final double mouseY) {
    if(enabled) {
      scrollAmount = MathHelper.clamp((float)(mouseY - this.y) / (float)this.height, 0.0F, 1.0F);
    }
  }
  
  public float getScrollAmount() {
    return scrollAmount;
  }
  
}