package net.apeng.filtpick.gui.screen;


import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.config.FPConfigManager;
import net.apeng.filtpick.gui.widget.LegacyTexturedButton;
import net.apeng.filtpick.util.IntBoolConvertor;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class FiltPickScreen extends AbstractContainerScreen<FiltPickMenu> {

    public static final int WHITELIST_MODE_BUTTON_ID = 0;
    public static final int DESTRUCTION_MODE_BUTTON_ID = 1;
    public static final int CLEAR_BUTTON_ID = 2;
    private static final Style EXPLANATION_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).applyFormats(ChatFormatting.ITALIC);
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ResourceLocation FILT_MODE_BUTTON_TEXTURE = ResourceLocation.tryBuild(FiltPick.ID, "gui/filtmode_button.png");
    private static final ResourceLocation DESTRUCTION_BUTTON_TEXTURE = ResourceLocation.tryBuild(FiltPick.ID, "gui/destruction_button.png");
    private static final ResourceLocation CLEAR_BUTTON_TEXTURE = ResourceLocation.tryBuild(FiltPick.ID, "gui/clearlist_button.png");
    private static final ResourceLocation RETURN_BUTTON_TEXTURE = ResourceLocation.tryBuild(FiltPick.ID, "gui/return_button.png");

    private FPToggleButton filtModeButton, destructionButton;
    private LegacyTexturedButton clearButton, returnButton;

    public FiltPickScreen(FiltPickMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitlePosition();
        addButtons();
    }

    private void addButtons() {
        addFiltModeButton();
        addDestructionButton();
        addClearButton();
        addReturnButton();
    }

    private void addFiltModeButton() {
        filtModeButton = new FPToggleButton(
                this.leftPos + 10 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.FILT_MODE_BUTTON).xOffset(),
                this.topPos + 4 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.FILT_MODE_BUTTON).yOffset(),
                12,
                11,
                FILT_MODE_BUTTON_TEXTURE,
                WHITELIST_MODE_BUTTON_ID
        );
        filtModeButton.setTooltips(Component.translatable("whitelist_mode").append("\n").withStyle(ChatFormatting.DARK_GREEN).append(Component.translatable("whitelist_mode_explanation").withStyle(EXPLANATION_STYLE)), Component.translatable("blacklist_mode").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("blacklist_mode_explanation").withStyle(EXPLANATION_STYLE)));
        addRenderableWidget(filtModeButton);
    }

    private void addDestructionButton() {
        destructionButton = new FPToggleButton(
                this.leftPos + 10 + 2 + 12 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.DESTRUCTION_MODE_BUTTON).xOffset(),
                this.topPos + 4 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.DESTRUCTION_MODE_BUTTON).yOffset(),
                12,
                11,
                DESTRUCTION_BUTTON_TEXTURE,
                DESTRUCTION_MODE_BUTTON_ID
        );
        destructionButton.setTooltips(Component.translatable("destruction_mode_on").withStyle(ChatFormatting.DARK_RED).append("\n").append(Component.translatable("destruction_mode_on_explanation").withStyle(EXPLANATION_STYLE)), Component.translatable("destruction_mode_off").withStyle(ChatFormatting.DARK_GRAY));
        addRenderableWidget(destructionButton);
    }

    private void addClearButton() {
        clearButton = new LegacyTexturedButton(
                this.leftPos + 154 - 14 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.CLEAR_BUTTON).xOffset(),
                this.topPos + 4 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.CLEAR_BUTTON).yOffset(),
                12,
                11,
                0,
                0,
                12,
                CLEAR_BUTTON_TEXTURE,
                button -> sendButtonClickC2SPacket(CLEAR_BUTTON_ID)
        );
        setTooltip2ClearButton();
        addRenderableWidget(clearButton);
    }

    private void setTooltip2ClearButton() {
        clearButton.setTooltip(Tooltip.create(Component.translatable("reset_explanation").withStyle(EXPLANATION_STYLE)));
        clearButton.setTooltipDelay(500);
    }

    private void addReturnButton() {
        returnButton = new LegacyTexturedButton(
                this.leftPos + 154 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RETURN_BUTTON).xOffset(),
                this.topPos + 4 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RETURN_BUTTON).yOffset(),
                12,
                11,
                0,
                0,
                12,
                RETURN_BUTTON_TEXTURE,
                12,
                11 * 2 + 1,
                button -> minecraft.setScreen(new InventoryScreen(minecraft.player))
        );
        addRenderableWidget(returnButton);

    }

    private void setTitlePosition() {
        this.titleLabelX = 72;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTitle(context, font, Component.translatable("filtpick_screen_name"), 72, topPos + 4, width - 72, topPos + 14, 0x404040);
        this.renderTooltip(context, mouseX, mouseY);
    }

    protected void renderTitle(GuiGraphics context, Font textRenderer, Component text, int startX, int startY, int endX, int endY, int color) {
        int centerX = (startX + endX) / 2;
        int i = textRenderer.width(text);
        int j = (startY + endY - textRenderer.lineHeight) / 2 + 1;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, l);
            context.enableScissor(startX, startY, endX, endY);
            context.drawString(textRenderer, text, startX - (int) g, j, color, false);
            context.disableScissor();
        } else {
            int l = Mth.clamp(centerX, startX + i / 2, endX - i / 2);
            FormattedCharSequence orderedText = text.getVisualOrderText();
            context.drawString(textRenderer, orderedText, centerX - textRenderer.width(orderedText) / 2, j, color, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(BACKGROUND_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void sendButtonClickC2SPacket(int buttonId) {
        Minecraft.getInstance().getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, buttonId));
    }

    class FPToggleButton extends AbstractWidget {
        private final ContainerData propertyDelegate = menu.getPropertyDelegate();
        private final int buttonId;
        private final ResourceLocation texture;
        private Tooltip tureTooltip, falseTooltip;

        public FPToggleButton(int x, int y, int width, int height, ResourceLocation texture, int buttonId) {
            this(x, y, width, height, Component.empty(), texture, buttonId);
        }

        public FPToggleButton(int x, int y, int width, int height, Component message, ResourceLocation texture, int buttonId) {
            super(x, y, width, height, message);
            this.texture = texture;
            this.buttonId = buttonId;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            if (!visible) {
                return;
            }
            renderTexture(context);
            renderTooltip();
        }

        private void renderTooltip() {
            if(correspondPropertyTrue() && tureTooltip != null) {
                tureTooltip.refreshTooltipForNextRenderPass(isHovered(), isFocused(), getRectangle());
            }
            if (!correspondPropertyTrue() && falseTooltip != null) {
                falseTooltip.refreshTooltipForNextRenderPass(isHovered(), isFocused(), getRectangle());
            }
        }


        private void renderTexture(GuiGraphics context) {
            int u = 0, v = 0;
            v = setVerticalOffset(v);
            u = setHorizontalOffset(u);
            context.blit(texture, this.getX(), this.getY(), u, v, width, height, 2 * width + 1, 2 * height + 1);
        }

        private int setHorizontalOffset(int u) {
            if (!correspondPropertyTrue()) u += width + 1;
            return u;
        }

        private int setVerticalOffset(int v) {
            if (isHovered) v += height + 1;
            return v;
        }

        private boolean correspondPropertyTrue() {
            return IntBoolConvertor.toBool(propertyDelegate.get(buttonId));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
        }

        /**
         * Callback for when a mouse button scroll event
         * has been captured.
         *
         * @param mouseX           the X coordinate of the mouse
         * @param mouseY           the Y coordinate of the mouse
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount   the vertical scroll amount
         * @return {@code true} to indicate that the event handling is successful/valid
         */
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            if (this.visible && this.isHovered) {
                sendButtonClickC2SPacket(buttonId);
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            sendButtonClickC2SPacket(buttonId);
        }

        public void setTooltips(Component tureTooltip, Component falseTooltip) {
            this.tureTooltip = Tooltip.create(tureTooltip);
            this.falseTooltip = Tooltip.create(falseTooltip);
        }
    }

}
