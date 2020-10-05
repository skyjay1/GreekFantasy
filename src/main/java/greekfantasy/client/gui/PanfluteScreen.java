package greekfantasy.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.client.network.CUpdatePanflutePacket;
import greekfantasy.item.PanfluteItem;
import greekfantasy.util.PanfluteSong;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PanfluteScreen extends Screen {
  
  private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/panflute.png");

  private static final int SCREEN_WIDTH = 154;
  private static final int SCREEN_HEIGHT = 150;
  
  private static final int BTN_LEFT = 8;
  private static final int BTN_TOP = 22;
  private static final int BTN_WIDTH = 120;
  private static final int BTN_HEIGHT = 24;

  private static final int BTN_VISIBLE = 5;
  
  private static final int SCROLL_LEFT = 133;
  private static final int SCROLL_TOP = BTN_TOP;
  private static final int SCROLL_WIDTH = 14;
  private static final int SCROLL_HEIGHT = BTN_HEIGHT * BTN_VISIBLE;
  
  private final List<Map.Entry<ResourceLocation, PanfluteSong>> songs = new ArrayList<>();
  private final List<PanfluteScreen.SongButton> songButtons = new ArrayList<>();
  private final int itemSlot;
  private final ItemStack panfluteItem;
  
  private ResourceLocation selectedSong; 
  private ScrollButton scrollButton;
  
  /** Number of pixels between left side of screen and left side of gui **/
  private int guiLeft;
  /** Number of pixels between top of screen and top of gui **/
  private int guiTop;
  /** True if the scrollbar is being dragged **/
  private boolean isScrolling;
  /** True if there are at least [BTN_VISIBLE] number of songs in the list **/
  private boolean scrollEnabled;

  protected PanfluteScreen(final int itemSlotIn, final ItemStack panfluteItemIn) {
    super(new TranslationTextComponent("gui.panflute.title"));
    itemSlot = itemSlotIn;
    panfluteItem = panfluteItemIn;
    // populate songs list (alphabetically)
    if(songs.isEmpty()) {
      songs.addAll(GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries());
      songs.sort((e1, e2) -> e1.getValue().getName().getUnformattedComponentText().compareTo(e2.getValue().getName().getUnformattedComponentText()));
    }
    // determine currently selected song
    if(panfluteItem.getOrCreateTag().contains(PanfluteItem.KEY_SONG)) {
      selectedSong = new ResourceLocation(panfluteItem.getTag().getString(PanfluteItem.KEY_SONG));
    } else {
      selectedSong = PanfluteItem.DEFAULT_SONG;
    }
    // determine default scroll
    isScrolling = false;
    scrollEnabled = songs.size() > BTN_VISIBLE;
  }
  
  @Override
  public void init(Minecraft minecraft, int width, int height) {
    super.init(minecraft, width, height);
    this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
    this.guiTop = (this.height - SCREEN_HEIGHT) / 2 - 10;
    // add 'done' button
    addButton(new Button(guiLeft, guiTop + SCREEN_HEIGHT + 4, SCREEN_WIDTH, 20, new TranslationTextComponent("gui.done"), c -> this.minecraft.displayGuiScreen(null)));
    // add scroll button
    scrollButton = addButton(new ScrollButton(this, scrollEnabled, guiLeft + SCROLL_LEFT, guiTop + SCROLL_TOP, SCROLL_WIDTH, SCROLL_HEIGHT, 
        0, SCREEN_HEIGHT + 2 * BTN_HEIGHT, SCREEN_TEXTURE, b -> isScrolling = true, b -> {
          updateScroll(b.getScrollAmount());
          isScrolling = false;
        }));
    // add song buttons
    int i = 0;
    for(final Map.Entry<ResourceLocation, PanfluteSong> e : songs) {
      final SongButton b = addButton(new SongButton(i, this, e.getValue(), e.getKey(), guiLeft + BTN_LEFT, 0));
      b.updateLocation(0);
      songButtons.add(b);      
      i++;
    }
    
  }
  
  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    // draw background image
    this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
    this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    // draw title
    drawCenteredString(matrixStack, this.font, this.getTitle(), this.width / 2, this.guiTop + 8, 16777215);
    // update scroll bar
    if (this.isScrolling && this.scrollEnabled) {
      updateScroll(this.scrollButton.getScrollAmount());
    }
    // draw buttons
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
  
  @Override
  public void onClose() {
    // send update packet to server
    GreekFantasy.CHANNEL.sendToServer(new CUpdatePanflutePacket(this.itemSlot, this.selectedSong.toString()));
    super.onClose();
  }
  
  protected void updateScroll(final float amount) {
    final int startIndex = (int) Math.round(amount * (this.songButtons.size() - BTN_VISIBLE));
    this.songButtons.forEach(b -> b.updateLocation(startIndex));
  }
  
  protected class SongButton extends Button {
    
    private final int index;
    private final PanfluteSong song;
    private final ResourceLocation songID;
    
    public SongButton(final int indexIn, final PanfluteScreen gui, final PanfluteSong songIn, 
        final ResourceLocation songInID, final int x, final int y) {
      super(x, y, BTN_WIDTH, BTN_HEIGHT, StringTextComponent.EMPTY, (b) -> gui.selectedSong = songInID);
      index = indexIn;
      song = songIn;
      songID = songInID;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        final boolean selected = isSelected();
        final int xOffset = 0;
        final int yOffset = SCREEN_HEIGHT + (selected ? this.height : 0);
        // draw button background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        PanfluteScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        // draw song name string
        int textX = this.x + 3;
        int textY = this.y + 4;
        PanfluteScreen.this.font.func_243248_b(matrixStack, this.song.getName(), textX, textY, 0);
        // draw credits string
        final IFormattableTextComponent text = this.song.getCredits();
        final int wrap = this.width - 6;
        float scale = 1.0F;
        int textHeight =  PanfluteScreen.this.font.getWordWrappedHeight(text.getString(), wrap);
        if (textHeight > PanfluteScreen.this.font.FONT_HEIGHT) {
          scale = 0.75F;
          textHeight = (int) (scale * PanfluteScreen.this.font.getWordWrappedHeight(text.getString(), (int) (wrap / scale)));
        }
        textX = this.x + 3;
        textY = this.y + PanfluteScreen.this.font.FONT_HEIGHT + (int) (4.0F / scale);
        // re-scale and draw the credits string
        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, scale);
        PanfluteScreen.this.font.func_243248_b(matrixStack, text, textX / scale, textY / scale, 0);
        RenderSystem.popMatrix();
      }
    }
    
    protected boolean isSelected() {
      return this.isHovered() || (songID.equals(PanfluteScreen.this.selectedSong));
    }
    
    public void updateLocation(final int startIndex) {
      this.y = PanfluteScreen.this.guiTop + PanfluteScreen.BTN_TOP + PanfluteScreen.BTN_HEIGHT * (index - startIndex);
      if(index < startIndex || index >= (startIndex + PanfluteScreen.BTN_VISIBLE)) {
        this.visible = false;
        this.isHovered = false;
      } else {
        this.visible = true;
      }
    }
  }
}
