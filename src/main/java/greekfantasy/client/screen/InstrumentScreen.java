package greekfantasy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.screen.radial.NoteRadialMenuItem;
import greekfantasy.client.screen.radial.RadialMenuHelper;
import greekfantasy.client.screen.radial.RadialMenuItem;
import greekfantasy.item.InstrumentItem;
import greekfantasy.util.Song;
import greekfantasy.util.SongManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class InstrumentScreen extends Screen {

    private static final ResourceLocation WIDGETS = new ResourceLocation(GreekFantasy.MODID, "textures/gui/song_widgets.png");

    private static final String[] NOTE_NAMES = {
            "G", "G#", "A", "A#", "B", "C", "C#", "D",
            "D#", "E", "F", "F#", "G", "G#", "A", "A#",
            "B", "C", "C#", "D", "D#", "E", "F", "F#"
    };
    private static final int VISIBLE_NOTE_COUNT = 12;
    private static final int GROUP_COUNT = 2;
    private static final int RADIAL_WIDTH = 80;
    private static final int RADIAL_HEIGHT = RADIAL_WIDTH;
    private static final int RADIAL_ITEM_WIDTH = 40;
    private static final int RADIAL_ITEM_HEIGHT = RADIAL_ITEM_WIDTH;

    private static final int VISIBLE_SONG_COUNT = 6;
    private static final int SONG_WIDTH = 170;
    private static final int SONG_HEIGHT = 24;
    private static final int SONG_RADIAL_MARGIN = 12;
    private static final int SCROLL_WIDTH = 12;
    private static final int SCROLL_HEIGHT = 15;

    private static final int BACKGROUND_COLOR = 0x3F000000;
    private static final int BACKGROUND_HOVER_COLOR = 0x3FFFFFFF;

    private final RadialMenuHelper helper;
    private final int itemSlot;
    private final ItemStack itemStack;
    private final InstrumentItem instrument;

    private NoteRadialMenuItem[] items = new NoteRadialMenuItem[VISIBLE_NOTE_COUNT * GROUP_COUNT];
    private final List<RadialMenuItem> visibleItems;
    private int group;
    /**
     * The x position of the center of the screen
     **/
    private float x;
    /**
     * The y position of the center of the screen
     **/
    private float y;

    private List<ResourceLocation> songs;
    private SongButton[] songButtons;
    private ScrollButton scrollButton;
    private ResourceLocation song;
    private int tickCount;
    private float scrollAmount;
    private boolean songsVisible;
    private boolean isDraggingScrollbar;

    private Component octaveControlComponent;
    private Component songVisibilityComponent;

    public InstrumentScreen(int itemSlot, ItemStack itemStack) {
        super(Component.empty());
        if (!(itemStack.getItem() instanceof InstrumentItem)) {
            throw new IllegalArgumentException("Instrument Screen received an item that is not InstrumentItem");
        }
        this.instrument = (InstrumentItem) itemStack.getItem();
        this.visibleItems = new ArrayList<>();
        this.helper = new RadialMenuHelper(this, visibleItems, RADIAL_WIDTH - RADIAL_ITEM_WIDTH, RADIAL_WIDTH,
                BACKGROUND_COLOR, BACKGROUND_HOVER_COLOR);
        this.itemSlot = itemSlot;
        this.itemStack = itemStack;
        // create radial menu items
        for (int i = 0, n = items.length; i < n; i++) {
            items[i] = new NoteRadialMenuItem(this, Component.literal(NOTE_NAMES[i]), i + 1);
        }
        // create song items
        this.songs = new ArrayList<>(GreekFantasy.SONG_MAP.keySet());
    }

    @Override
    protected void init() {
        super.init();
        this.x = width / 2.0F;
        this.y = height / 2.0F - 9.0F;
        octaveControlComponent = Component.translatable("screen." + GreekFantasy.MODID + ".controls.octave");
        songVisibilityComponent = Component.translatable("screen." + GreekFantasy.MODID + ".controls.songs");
        // add song buttons
        int songY = (int) (this.y) - (VISIBLE_SONG_COUNT * SONG_HEIGHT) / 2;
        this.songButtons = new SongButton[VISIBLE_SONG_COUNT];
        for (int i = 0; i < VISIBLE_SONG_COUNT; i++) {
            this.songButtons[i] = this.addRenderableWidget(new SongButton(this, 0, songY + SONG_HEIGHT * i));
        }
        // add scroll button
        this.scrollButton = this.addRenderableWidget(new ScrollButton(this, 0, songY));
        // initialize group and scroll amount
        setGroup(0);
        setSongScrollAmount(0.0F);
        setSongsVisible(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickCount++;
        if (this.song != null) {
            SongManager.playMusic(getMinecraft().player, instrument, song, tickCount, instrument.getVolume(), instrument.getVolume() * 0.5F);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // draw radial menu background
        helper.drawBackground(poseStack, x, y, 0);
        // draw other widgets
        super.render(poseStack, mouseX, mouseY, partialTick);
        // draw radial menu items
        helper.drawItems(poseStack, (int) x, (int) y, 0, RADIAL_ITEM_WIDTH, RADIAL_ITEM_HEIGHT, this.font, this.itemRenderer);
        // draw other text
        float octaveX = x - font.width(octaveControlComponent) / 2.0f;
        font.drawShadow(poseStack, octaveControlComponent, octaveX, y + RADIAL_HEIGHT + 4, 0xFFFFFF);
        float visibilityX = x - font.width(songVisibilityComponent) / 2.0f;
        font.drawShadow(poseStack, songVisibilityComponent, visibilityX, y + RADIAL_HEIGHT + 4 + font.lineHeight + 1, 0xFFFFFF);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        helper.processMouse(this.x, this.y, (int) mouseX, (int) mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (songsVisible && mouseX < this.x - RADIAL_WIDTH - SONG_RADIAL_MARGIN) {
            // attempt to scroll song menu
            float scrollAmount = Mth.clamp(this.scrollAmount - (float) amount * (1.0F / Math.max(1, this.songs.size() - VISIBLE_SONG_COUNT)), 0.0F, 1.0F);
            setSongScrollAmount(scrollAmount);
            scrollButton.setScrollAmount(scrollAmount);
        } else {
            // attempt to scroll radial menu
            setGroup((int) (this.group + amount));
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyEvent.VK_SPACE) {
            setSongsVisible(!songsVisible);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setGroup(final int group) {
        this.group = Mth.clamp(group, 0, GROUP_COUNT - 1);
        this.visibleItems.clear();
        for (int i = this.group * VISIBLE_NOTE_COUNT, n = (this.group + 1) * VISIBLE_NOTE_COUNT; i < n; i++) {
            this.visibleItems.add(items[i]);
        }
    }

    private void setSongScrollAmount(final float amount) {
        this.scrollAmount = Mth.clamp(amount, 0.0F, 1.0F);
        final int startIndex = Math.max(0, Math.round(scrollAmount * songs.size()) - VISIBLE_SONG_COUNT);
        for (int buttonId = 0; buttonId < VISIBLE_SONG_COUNT; buttonId++) {
            int index = buttonId + startIndex;
            this.songButtons[buttonId].setSong(this.songsVisible && index < songs.size() ? songs.get(index) : null);
        }
        scrollButton.setScrollAmount(scrollAmount);
        return;
    }

    private void setSongsVisible(final boolean songsVisible) {
        this.songsVisible = songsVisible;
        if (songsVisible) {
            this.x = this.width / 2.0F + SONG_WIDTH / 2.0F;
        } else {
            this.x = this.width / 2.0F;
        }
        setSongScrollAmount(this.scrollAmount);
        scrollButton.visible = songsVisible;
    }

    public void playNote(int note) {
        SongManager.playNoteAt(minecraft.player, instrument, note, instrument.getVolume());
    }

    private static class SongButton extends Button {

        private final InstrumentScreen screen;
        private ResourceLocation songId;
        private Component name;
        private Component credits;

        public SongButton(final InstrumentScreen screen, final int x, final int y) {
            super(x, y, SONG_WIDTH, SONG_HEIGHT, Component.empty(), b -> {
            });
            this.screen = screen;
            this.visible = false;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                // draw button background
                int color = isSelected() ? BACKGROUND_HOVER_COLOR : BACKGROUND_COLOR;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;
                int a = (color >> 24) & 0xFF;
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, InstrumentScreen.WIDGETS);
                RenderSystem.setShaderColor(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
                screen.blit(poseStack, this.x, this.y, 0, 0, this.width, this.height);
                RenderSystem.disableBlend();
                // draw the song name and credits
                screen.font.drawShadow(poseStack, name, this.x + 3, this.y + 4, 0);
                screen.font.drawShadow(poseStack, credits, this.x + 3, this.y + 5 + screen.font.lineHeight, 0);
            }
        }


        /**
         * @return whether this button should render as selected
         **/
        protected boolean isSelected() {
            return this.isHovered || (screen.song == this.songId);
        }

        @Override
        public void onPress() {
            super.onPress();
            this.screen.tickCount = 0;
            if (this.screen.song != null && this.screen.song == this.songId) {
                this.screen.song = null;
            } else {
                this.screen.song = this.songId;
            }
        }

        public void setSong(final ResourceLocation songId) {
            this.songId = songId;
            if (songId != null) {
                this.x = (int) (screen.x) - RADIAL_WIDTH - SONG_RADIAL_MARGIN - SONG_WIDTH;
                this.visible = true;
                Song song = GreekFantasy.SONG_MAP.getOrDefault(songId, Song.EMPTY);
                this.name = song.getName().copy().withStyle(ChatFormatting.WHITE);
                this.credits = song.getCredits().copy().withStyle(ChatFormatting.GRAY);
            } else {
                this.visible = false;
            }
        }
    }

    private static class ScrollButton extends Button {

        private InstrumentScreen screen;
        private int scrollY;

        public ScrollButton(final InstrumentScreen screen, int x, int y) {
            super(x, y, SCROLL_WIDTH, VISIBLE_SONG_COUNT * SONG_HEIGHT, Component.empty(), b -> {});
            this.screen = screen;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                // draw button background
                int color = isHoveredOrFocused() || screen.isDraggingScrollbar ? BACKGROUND_HOVER_COLOR : BACKGROUND_COLOR;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;
                int a = (color >> 24) & 0xFF;
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, InstrumentScreen.WIDGETS);
                RenderSystem.setShaderColor(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
                screen.blit(poseStack, this.x, this.scrollY, 0, 26, SCROLL_WIDTH, SCROLL_HEIGHT);
                RenderSystem.disableBlend();
            }
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            screen.isDraggingScrollbar = true;
            float scrollAmount = Mth.clamp((float) (mouseY - this.y) / (float) this.height, 0.0F, 1.0F);
            screen.setSongScrollAmount(scrollAmount);
            this.setScrollAmount(scrollAmount);
        }

        public void setScrollAmount(final float scrollAmount) {
            this.x = (int) screen.x - RADIAL_WIDTH - SONG_RADIAL_MARGIN - SONG_WIDTH - this.width - 2;
            this.scrollY = this.y + (int) (scrollAmount * (float)(this.height - SCROLL_HEIGHT));
        }
    }
}
