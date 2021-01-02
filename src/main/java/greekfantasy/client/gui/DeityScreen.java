package greekfantasy.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.favor.Deity;
import greekfantasy.favor.FavorLevel;
import greekfantasy.favor.IDeity;
import greekfantasy.favor.IFavor;
import greekfantasy.gui.DeityContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class DeityScreen extends ContainerScreen<DeityContainer> {
  
  private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/deity.png");
  private static final ResourceLocation TABS_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/gui/deity_tabs.png");

  private static final int SCREEN_WIDTH = 195;
  private static final int SCREEN_HEIGHT = 160;
  
  private static final int TAB_WIDTH = 28;
  private static final int TAB_HEIGHT = 32;
  private static final int TAB_COUNT = 6;
  
  private static final int FAVOR_WIDTH = 69;
  private static final int FAVOR_HEIGHT = 86;
  private static final int FAVOR_LEFT = 7;
  private static final int FAVOR_TOP = 21 + 9;
  
  private static final int BTN_LEFT = 7;
  private static final int BTN_TOP = 111;
  private static final int BTN_WIDTH = 70;
  private static final int BTN_HEIGHT = 16;

  private static final int ITEM_LEFT = 80;
  private static final int ITEM_TOP = FAVOR_TOP;
  private static final int ITEM_WIDTH = 18;
  private static final int ITEM_HEIGHT = 18;
  private static final int ITEM_COUNT_X = 5;
  private static final int ITEM_COUNT_Y = 7;
  
  private static final int ENTITY_LEFT = ITEM_LEFT;
  private static final int ENTITY_TOP = ITEM_TOP;
  private static final int ENTITY_COUNT_Y = 14;
  private static final int ENTITY_WIDTH = ITEM_WIDTH * ITEM_COUNT_X;
  
  private static final int SCROLL_LEFT = 174;
  private static final int SCROLL_TOP = FAVOR_TOP + 1;
  private static final int SCROLL_WIDTH = 14;
  private static final int SCROLL_HEIGHT = 124;
  
  private final List<IDeity> deityList = new ArrayList<>();
  private final List<List<Item>> itemList = new ArrayList<>();
  private final List<List<EntityType<?>>> entityList = new ArrayList<>();
  
  private final DeityScreen.TabButton[] tabButtons = new TabButton[TAB_COUNT];
  private final DeityScreen.ItemButton[] itemButtons = new ItemButton[ITEM_COUNT_X * ITEM_COUNT_Y];
  private final ITextComponent[] entityButtons = new ITextComponent[ENTITY_COUNT_Y];
  
  private ScrollButton<DeityScreen> scrollButton;
  
  private int selectedDeity;
  
  private DeityScreen.Mode mode;
  
  /** Number of pixels between left side of screen and left side of gui **/
  private int guiLeft;
  /** Number of pixels between top of screen and top of gui **/
  private int guiTop;
  /** True if the scrollbar is being dragged **/
  private boolean isScrolling;
  /** True if there are at least [ITEM_VISIBLE] number of items in the list **/
  private boolean scrollEnabled;
  
  private IFavor favor;

  public DeityScreen(final DeityContainer screenContainer, final PlayerInventory inv, final ITextComponent title) {
    super(screenContainer, inv, title);
    favor = screenContainer.getFavor();
    // initialize lists (deity, item, entity, etc.)
    if(deityList.isEmpty()) {
      // populate deity list (alphabetically)
      GreekFantasy.PROXY.DEITY.getValues().forEach(o -> deityList.add(o.orElse(Deity.EMPTY)));
      Collections.sort(deityList, (d1, d2) -> d1.getText().getString().compareTo(d2.getText().getString()));
      // add item modifier list for each deity
      deityList.forEach(d -> {
        final List<Item> dItemList = new ArrayList<>();
        d.getItemFavorModifiers().forEach((r, i) -> {
          dItemList.add(ForgeRegistries.ITEMS.getValue(r));
          Collections.sort(dItemList, (i1, i2) -> d.getItemFavorModifier(i2) - d.getItemFavorModifier(i1));
        });
        itemList.add(dItemList);
      });
      
      // add entity type modifier list for each deity
      deityList.forEach(d -> {
        final List<EntityType<?>> dkillList = new ArrayList<>();
        d.getKillFavorModifiers().forEach((r, i) -> {
          dkillList.add(ForgeRegistries.ENTITIES.getValue(r));
          Collections.sort(dkillList, (i1, i2) -> d.getKillFavorModifier(i2) - d.getKillFavorModifier(i1));
        });
        entityList.add(dkillList);
      });
    }
    
    // determine default scroll
    isScrolling = false;
    scrollEnabled = itemList.get(selectedDeity).size() > ITEM_COUNT_X * ITEM_COUNT_Y;
  }
  
  @Override
  public void init(Minecraft minecraft, int width, int height) {
    super.init(minecraft, width, height);
    this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
    this.guiTop = (this.height - (SCREEN_HEIGHT - TAB_HEIGHT)) / 2 - 10;
    this.playerInventoryTitleY = this.height;
    // add 'done' button
    addButton(new Button(guiLeft, guiTop + SCREEN_HEIGHT + 4, SCREEN_WIDTH, 20, new TranslationTextComponent("gui.done"), c -> this.minecraft.displayGuiScreen(null)));
    // add deity tabs
    for(int i = 0, l = Math.min(TAB_COUNT, deityList.size()); i < l; i++) {
      tabButtons[i] = addButton(new TabButton(this, i, deityList.get(i).getText(), guiLeft + (i * TAB_WIDTH), guiTop - TAB_HEIGHT + 4));
    }
    // add item buttons
    for(int i = 0, l = ITEM_COUNT_X * ITEM_COUNT_Y; i < l; i++) {
      itemButtons[i] = addButton(new ItemButton(this, guiLeft + ITEM_LEFT + (i % ITEM_COUNT_X) * ITEM_WIDTH, guiTop + ITEM_TOP + (i / ITEM_COUNT_X) * ITEM_HEIGHT));
    }
    // add scroll button
    scrollButton = addButton(new ScrollButton<>(this, guiLeft + SCROLL_LEFT, guiTop + SCROLL_TOP, SCROLL_WIDTH, SCROLL_HEIGHT, 
        0, SCREEN_HEIGHT + 2 * BTN_HEIGHT, SCREEN_TEXTURE, s -> s.scrollEnabled, b -> isScrolling = true, b -> {
          updateScroll(b.getScrollAmount());
          isScrolling = false;
        }));
    // add mode buttons
    addButton(new FancyButton(this, guiLeft + BTN_LEFT, guiTop + BTN_TOP, new TranslationTextComponent("entity.minecraft.item"), b -> updateMode(DeityScreen.Mode.ITEM)));
    addButton(new FancyButton(this, guiLeft + BTN_LEFT, guiTop + BTN_TOP + BTN_HEIGHT + 4, new TranslationTextComponent("gui.mirror.entity"), b -> updateMode(DeityScreen.Mode.ENTITY)));
    // set selected deity now that things are nonnull
    setSelectedDeity(2);
  }
  
  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    // draw background image
    this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
    this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    // draw title
    drawString(matrixStack, this.font, deityList.get(selectedDeity).getText(), this.guiLeft + FAVOR_LEFT, this.guiTop + 12, 0xFFFFFF);
    // update scroll bar
    if (this.isScrolling && this.scrollEnabled) {
      updateScroll(this.scrollButton.getScrollAmount());
    }
    // draw favor values
    final FavorLevel level = favor.getFavor(deityList.get(selectedDeity));
    this.font.func_243248_b(matrixStack, new TranslationTextComponent("favor.favor")
        .mergeStyle(TextFormatting.BLACK, TextFormatting.BOLD), 
        guiLeft + FAVOR_LEFT, guiTop + FAVOR_TOP, 0xFFFFFF);
    this.font.func_243248_b(matrixStack, new StringTextComponent(String.valueOf(level.getFavor()))
        .mergeStyle(TextFormatting.DARK_PURPLE), 
        guiLeft + FAVOR_LEFT, guiTop + ENTITY_TOP + font.FONT_HEIGHT * 1, 0xFFFFFF);
    this.font.func_243248_b(matrixStack, new TranslationTextComponent("favor.level")
        .mergeStyle(TextFormatting.BLACK, TextFormatting.BOLD), 
        guiLeft + FAVOR_LEFT, guiTop + ENTITY_TOP + font.FONT_HEIGHT * 3, 0xFFFFFF);
    this.font.func_243248_b(matrixStack, new StringTextComponent(String.valueOf(level.getLevel()))
        .mergeStyle(TextFormatting.DARK_PURPLE), 
        guiLeft + FAVOR_LEFT, guiTop + ENTITY_TOP + font.FONT_HEIGHT * 4, 0xFFFFFF);
    this.font.func_243248_b(matrixStack, new TranslationTextComponent("favor.next_level")
        .mergeStyle(TextFormatting.BLACK, TextFormatting.BOLD), 
        guiLeft + FAVOR_LEFT, guiTop + ENTITY_TOP + font.FONT_HEIGHT * 6, 0xFFFFFF);
    this.font.func_243248_b(matrixStack, new StringTextComponent(String.valueOf(level.getFavorToNextLevel()))
        .mergeStyle(TextFormatting.DARK_PURPLE), 
        guiLeft + FAVOR_LEFT, guiTop + ENTITY_TOP + font.FONT_HEIGHT * 7, 0xFFFFFF);
    // draw entity values
    if(mode == DeityScreen.Mode.ENTITY) {
      for(int i = 0, l = entityButtons.length; i < l; i++) {
        int x = ENTITY_WIDTH - font.getStringWidth(entityButtons[i].getString());
        this.font.func_243248_b(matrixStack, entityButtons[i], guiLeft + ENTITY_LEFT + x, guiTop + ENTITY_TOP + i * font.FONT_HEIGHT, 0xFFFFFF);
      }
    }
    // draw buttons
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    // draw hovering text LAST
    for(final Widget b : this.buttons) {
      if(b.visible && b.isHovered()) {
        b.renderToolTip(matrixStack, mouseX, mouseY);
      }
    }
  }
  
  private void setSelectedDeity(int index) {
    selectedDeity = index;
    updateMode(Mode.ITEM);
  }

  
  protected void updateMode(final DeityScreen.Mode modeIn) {
    mode = modeIn;
    final boolean itemVisible = (mode == Mode.ITEM);
    for(int i = 0, l = itemButtons.length; i < l; i++) {
      itemButtons[i].visible = itemVisible;
    }
    if(mode == Mode.ITEM) {
      scrollEnabled = itemList.get(selectedDeity).size() > ITEM_COUNT_X * ITEM_COUNT_Y;
    } else if(mode == Mode.ENTITY) {
      scrollEnabled = entityList.get(selectedDeity).size() > ENTITY_COUNT_Y;
    }
    updateScroll(0.0F);
  }

  protected void updateScroll(final float amount) {
    if(mode == Mode.ITEM) {
      // updatee item buttons using scroll value
      int startIndex = (int) Math.round(amount * (this.itemList.get(selectedDeity).size() / (itemButtons.length)));
      for(int i = 0, l = itemButtons.length, s = itemList.get(selectedDeity).size(); i < l; i++) {
        Item item = (i + startIndex) >= s ? Items.AIR : itemList.get(selectedDeity).get(i + startIndex);
        itemButtons[i].updateItem(item);
      }
    } else if(mode == Mode.ENTITY) {
      // update entity buttons
      int startIndex = (int) Math.round(amount * (this.entityList.get(selectedDeity).size() / (entityButtons.length)));
      for(int i = 0, l = entityButtons.length, s = entityList.get(selectedDeity).size(); i < l; i++) {
        if(i + startIndex < s) {
          // entity type is within list, update the text component
          EntityType<?> entity = entityList.get(selectedDeity).get(i + startIndex);
          final int modifier = deityList.get(selectedDeity).getKillFavorModifier(entity);
          final TextFormatting color = modifier < 0 ? TextFormatting.DARK_RED : TextFormatting.DARK_GREEN;
          String spaces = Math.abs(modifier) >= 100 ? " " : (Math.abs(modifier) >= 10 ? "  " : "   ");
          entityButtons[i] = new TranslationTextComponent(entity.getTranslationKey()).mergeStyle(TextFormatting.BLACK)
              .append(new StringTextComponent(":" + spaces).mergeStyle(TextFormatting.BLACK))
              .append(new StringTextComponent((modifier < 0 ? "" : "+") + modifier).mergeStyle(color));
        } else {
          // entity type is not within list, clear the text component
          entityButtons[i] = StringTextComponent.EMPTY;
        }
      }
    }
  }
  
  protected class ItemButton extends Button {
    
    private static final int ITEM_X = 0;
    private static final int ITEM_Y = 207;
    
    private ItemStack item = ItemStack.EMPTY;
    private int itemValue;
    
    public ItemButton(final DeityScreen gui, final int x, final int y) {
      super(x, y, ITEM_WIDTH, ITEM_HEIGHT, StringTextComponent.EMPTY, b -> {}, (b, m, bx, by) -> gui.renderTooltip(m, b.getMessage(), bx, by));
      this.visible = false;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        final int xOffset = ITEM_X + (itemValue < 0 ? ITEM_WIDTH : 0);
        final int yOffset = ITEM_Y;
        // draw button background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DeityScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        // draw item
        DeityScreen.this.itemRenderer.renderItemIntoGUI(item, this.x + 1, this.y + 1);
      }
    }
    
    public void updateItem(final Item i) {
      if(i == Items.AIR) {
        this.visible = false;
      } else {
        this.visible = true;
        item = new ItemStack(i);
        itemValue = DeityScreen.this.deityList.get(selectedDeity).getItemFavorModifier(i);
        final String title = ((itemValue > 0) ? ": +" : ": ") + itemValue;
        this.setMessage(new TranslationTextComponent(item.getTranslationKey()).append(new StringTextComponent(title)));
      }
    }
  }
  
  protected class TabButton extends Button {
    
    private int id;
    private ItemStack item = ItemStack.EMPTY;

    public TabButton(final DeityScreen gui, final int index, final ITextComponent title, final int x, final int y) {
      super(x, y, TAB_WIDTH, TAB_HEIGHT, title, b -> gui.setSelectedDeity(index), (b, m, bx, by) -> gui.renderTooltip(m, b.getMessage(), bx, by));
      updateDeity(index);
    }
    
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        int selected = isSelected() ? 0 : 2;
        final int xOffset = (id % TAB_COUNT) * TAB_WIDTH;
        final int yOffset = isSelected() ? this.height : 2;
        // draw button background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DeityScreen.this.getMinecraft().getTextureManager().bindTexture(TABS_TEXTURE);
        this.blit(matrixStack, this.x, this.y - selected, xOffset, yOffset - selected, this.width, this.height - selected);
        // draw item
        DeityScreen.this.itemRenderer.renderItemIntoGUI(item, this.x + (this.width - ITEM_WIDTH) / 2, this.y + (this.height - ITEM_HEIGHT) / 2);
      }
    }
    
    public void updateDeity(final int deityId) {
      id = deityId;
      final ResourceLocation rl = DeityScreen.this.deityList.get(deityId).getName();
      final ResourceLocation altar = new ResourceLocation(rl.getNamespace(), "altar_" + rl.getPath());
      item = new ItemStack(ForgeRegistries.ITEMS.containsKey(altar) ? ForgeRegistries.ITEMS.getValue(altar) : GFRegistry.PANFLUTE);
    }
    
    public boolean isSelected() {
      return DeityScreen.this.selectedDeity == id;
    }
  }
  
  protected class FancyButton extends Button {
   
    public FancyButton(final DeityScreen screenIn, final int x, final int y, final ITextComponent title, final IPressable pressedAction) {
      super(x, y, BTN_WIDTH, BTN_HEIGHT, title, pressedAction);
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if(this.visible) {
        final boolean selected = isSelected();
        final int xOffset = 0;
        final int yOffset = SCREEN_HEIGHT + (selected ? this.height : 0);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DeityScreen.this.getMinecraft().getTextureManager().bindTexture(SCREEN_TEXTURE);
        this.blit(matrixStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
        drawCenteredString(matrixStack, DeityScreen.this.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor() | MathHelper.ceil(this.alpha * 255.0F) << 24);
      }
    }
    
    protected boolean isSelected() {
      return this.isHovered();
    }
  }
  
  protected static enum Mode {
    ITEM, ENTITY;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    // TODO Auto-generated method stub
    
  }
}
