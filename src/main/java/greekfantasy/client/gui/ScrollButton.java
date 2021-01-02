package greekfantasy.client.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class ScrollButton<T extends Screen> extends Button {
  
  /** A consumer to handle when the scroll starts **/
  private final Consumer<ScrollButton<T>> scrollStartHandler;
  /** A consumer to handle when the scroll ends **/
  private final Consumer<ScrollButton<T>> scrollEndHandler;
  /** Amount scrolled (0.0 = top, 1.0 = bottom) **/
  private float scrollAmount;
  /** If the scroll bar is enabled **/
  private final Predicate<T> enabled;
  
  private final T screen;
  private final ResourceLocation texture;
  private final int u;
  private final int v;
  private final int uWidth = 12;
  private final int vHeight = 16;

  public ScrollButton(final T gui, final int x, final int y, 
      final int width, final int height, final int uX, final int vY, 
      final ResourceLocation textureIn, final Predicate<T> isEnabled, 
      final Consumer<ScrollButton<T>> onScrollStart, final Consumer<ScrollButton<T>> onScrollEnd) {
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
      final boolean isEnabled = enabled.test(screen);
      final float scroll = isEnabled ? scrollAmount : 0.0F;
      final int uOffset = isEnabled ? 0 : uWidth;
      final int yOffset = MathHelper.clamp((int)(scroll * this.height - this.vHeight / 2), 0, this.height - vHeight + 1);
      this.blit(matrixStack, this.x + 1, this.y + yOffset, u + uOffset, v, uWidth, vHeight);
    }
  }
  
  @Override
  public void onClick(final double mouseX, final double mouseY) {
    if(enabled.test(screen)) {
      scrollStartHandler.accept(this);
      updateScrollAmount(mouseX, mouseY);
      scrollEndHandler.accept(this);
    }
  }
  
  @Override
  public void onDrag(final double mouseX, final double mouseY, final double dragX, final double dragY) {
    if(enabled.test(screen)) {
      scrollStartHandler.accept(this);
      updateScrollAmount(mouseX, mouseY);
    }
  }
  
  @Override
  public void onRelease(final double mouseX, final double mouseY) {
    if(enabled.test(screen)) {
      updateScrollAmount(mouseX, mouseY);
      scrollEndHandler.accept(this);
    }
  }
  
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if(enabled.test(screen)) {
      scrollStartHandler.accept(this);
      scrollAmount = MathHelper.clamp((float)(scrollAmount - delta), 0.0F, 1.0F);
      scrollEndHandler.accept(this);
      return true;
    }
    return false;
  }
  
  private void updateScrollAmount(final double mouseX, final double mouseY) {
    scrollAmount = MathHelper.clamp((float)(mouseY - this.y) / (float)this.height, 0.0F, 1.0F);
  }
  
  public float getScrollAmount() {
    return scrollAmount;
  }
  
  public void resetScroll() {
    scrollAmount = 0.0F;
    scrollEndHandler.accept(this);
  }
}