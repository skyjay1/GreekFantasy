package greekfantasy.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.item.IInstrument;
import greekfantasy.item.PanfluteItem;
import greekfantasy.network.CUpdateInstrumentPacket;
import greekfantasy.util.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SongScreen extends Screen {
  
  private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/song_selection.png");

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
  
  private final List<Map.Entry<ResourceLocation, Optional<Song>>> songs = new ArrayList<>();
  private final List<SongScreen.SongButton> songButtons = new ArrayList<>();
  private final int itemSlot;
  private final ItemStack instrumentStack;
  private final IInstrument instrumentItem;
  
  private ResourceLocation selectedSong = PanfluteItem.DEFAULT_SONG; 
  private ScrollButton<SongScreen> scrollButton;
  
  /** Number of pixels between left side of screen and left side of gui **/
  private int guiLeft;
  /** Number of pixels between top of screen and top of gui **/
  private int guiTop;
  /** True if there are at least [BTN_VISIBLE] number of songs in the list **/
  private boolean scrollEnabled;

  public SongScreen(final int itemSlotIn, final ItemStack panfluteItemIn) {
    super(new TranslationTextComponent("gui.panflute.title"));
    itemSlot = itemSlotIn;
    instrumentStack = panfluteItemIn;
    if(instrumentStack.getItem() instanceof IInstrument) {
      instrumentItem = (IInstrument)instrumentStack.getItem();
    } else {
      instrumentItem = null;
      this.closeScreen();
    }
    // populate songs list (alphabetically)
    if(songs.isEmpty()) {
      songs.addAll(GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries());
      songs.sort((e1, e2) -> e1.getValue().orElse(Song.EMPTY).getName().getString()
          .compareTo(e2.getValue().orElse(Song.EMPTY).getName().getString()));
    }
    // determine currently selected song
    selectedSong = instrumentItem.readSong(instrumentStack);
    // determine scroll
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
    scrollButton = addButton(new ScrollButton<>(this, guiLeft + SCROLL_LEFT, guiTop + SCROLL_TOP, SCROLL_WIDTH, SCROLL_HEIGHT, 
        0, SCREEN_HEIGHT + 2 * BTN_HEIGHT, SCREEN_TEXTURE, s -> s.scrollEnabled, 4, b -> updateScroll(b.getScrollAmount())));
    // add deity buttons
    int i = 0;
    for(final Entry<ResourceLocation, Optional<Song>> e : songs) {
      final SongButton b = addButton(new SongButton(i, this, e.getValue().orElse(Song.EMPTY), e.getKey(), guiLeft + BTN_LEFT, 0));
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
    drawCenteredString(matrixStack, this.font, this.getTitle(), this.width / 2, this.guiTop + 8, 0xFFFFFF);
    // draw buttons
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
  
  /**
   * Called from the main game loop to update the screen.
   */
  @Override
  public void tick() {
    super.tick();
    if(scrollButton != null) {
      scrollButton.tick();
    }
  }
  
  @Override
  public void onClose() {
    // send update packet to server
    if(instrumentItem instanceof IInstrument) {
      GreekFantasy.CHANNEL.sendToServer(new CUpdateInstrumentPacket(this.itemSlot, this.selectedSong));
    }
    super.onClose();
  }
  
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
    double multiplier = 1.0F / this.songButtons.size();
    return this.scrollButton.mouseScrolled(mouseX, mouseY, scrollAmount * multiplier);
 }
  
  protected void updateScroll(final float amount) {
    final int startIndex = (int) Math.round(amount * (this.songButtons.size() - BTN_VISIBLE));
    this.songButtons.forEach(b -> b.updateLocation(startIndex));
  }
  
  protected class SongButton extends Button {
    
    private final int index;
    private final Song song;
    private final ResourceLocation songID;
    
    public SongButton(final int indexIn, final SongScreen gui, final Song songIn, 
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
        SongScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        // style the name and credits
        IFormattableTextComponent name = song.getName().deepCopy();
        IFormattableTextComponent credits = song.getCredits().deepCopy();
        if(selected) {
          name.mergeStyle(TextFormatting.GRAY);
          credits.mergeStyle(TextFormatting.GRAY);
        }
        // draw deity name string
        drawStringToFit(matrixStack, name, this.x + 3, this.y + 4, this.width - 6);
        // draw credits string
        drawStringToFit(matrixStack, credits, this.x + 3, this.y + SongScreen.this.font.FONT_HEIGHT + 5, this.width - 6);
      }
    }
    
    /**
     * Scales a text component until it fits the given wrap width, then draws
     * the text at the given location
     * @param matrixStack the matrix stack
     * @param text the text to scale and draw
     * @param x the text x-position
     * @param y the text y-position
     * @param maxWidth the maximum width of the text component
     **/
    protected void drawStringToFit(MatrixStack matrixStack, ITextComponent text, int x, int y, int maxWidth) {
      float scale = 1.0F;
      while(SongScreen.this.font.getWordWrappedHeight(text.getString(), (int) (maxWidth / scale)) > SongScreen.this.font.FONT_HEIGHT && scale > 0.25F) {
        scale -= 0.05F;
      }
      RenderSystem.pushMatrix();
      RenderSystem.scalef(scale, scale, scale);
      SongScreen.this.font.func_243248_b(matrixStack, text, x / scale, y / scale, 0);
      RenderSystem.popMatrix();
    }
    
    /** @return whether this button should render as selected **/
    protected boolean isSelected() {
      return this.isHovered() || (songID.equals(SongScreen.this.selectedSong));
    }
    
    /**
     * Determines whether this button should show on the screen, and if so,
     * what y-position to assign in order to align with the current scroll value.
     * @param startIndex the start index of the buttons that are currently visible,
     * based on the scroll value
     **/
    public void updateLocation(final int startIndex) {
      this.y = SongScreen.this.guiTop + SongScreen.BTN_TOP + SongScreen.BTN_HEIGHT * (index - startIndex);
      if(index < startIndex || index >= (startIndex + SongScreen.BTN_VISIBLE)) {
        this.visible = false;
        this.isHovered = false;
      } else {
        this.visible = true;
      }
    }
  }
}
