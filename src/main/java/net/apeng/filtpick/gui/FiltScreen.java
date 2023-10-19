package net.apeng.filtpick.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.apeng.filtpick.networking.packet.ResetFiltListC2SPacket;
import net.apeng.filtpick.networking.packet.SynFiltModesC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiltScreen extends AbstractContainerScreen<FiltMenu> {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/container/shulker_box.png");
    public static final ResourceLocation FILT_MOD_BUTTON_TEXTURE_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_mode_button.png");
    public static final ResourceLocation DESTRUCTION_MOD_BUTTON_TEXTURE_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_destruction_on_button.png");
    public static final ResourceLocation FILTPICK_ENTRY_BUTTON_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_entry.png");
    public static final ResourceLocation FILTPICK_RETURN_BUTTON_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_return_button.png");
    public static final ResourceLocation FILTPICK_CLEAR_BUTTON_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_clearlist_button.png");

    private StateSwitchingButton filtModeButton, destructionModeButton;
    private ImageButton returnButton, resetButton;

    public FiltScreen(FiltMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.titleLabelX = 72;
        this.inventoryLabelX = 10;
    }

    @Override
    protected void init() {
        super.init();
        initFiltModeButton();
        initDestructionModeButton();
        updateButtonsStates();
        initReturnButton();
        initResetButton();
    }

    private void initResetButton() {
        resetButton = new ImageButton(
                this.leftPos + this.getXSize() - 12 - 7 - 13,
                this.topPos + 4,
                12,
                11,
                0,
                0,
                12,
                FILTPICK_CLEAR_BUTTON_LOCATION,
                (button) -> {
                    NetWorkHandler.sendToServer(new ResetFiltListC2SPacket());
                    filtModeButton.setStateTriggered(false);
                    destructionModeButton.setStateTriggered(false);
//                    filtModeButton.setTooltip(filtModeButton.isStateTriggered() ?
//                            Tooltip.create(Component.translatable("whitelist_mode").append("\n").withStyle(ChatFormatting.DARK_GREEN).append(Component.translatable("whitelist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
//                            Tooltip.create(Component.translatable("blacklist_mode").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("blacklist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY)))
//                    );
//                    destructionModeButton.setTooltip(destructionModeButton.isStateTriggered() ?
//                            Tooltip.create(Component.translatable("destruction_mode_on").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("destruction_mode_on_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
//                            Tooltip.create(Component.translatable("destruction_mode_off").withStyle(ChatFormatting.DARK_GRAY))
//                    );
                }
        );
        this.addRenderableWidget(resetButton);
    }

    private void updateButtonsStates() {
        filtModeButton.setStateTriggered(FiltPick.CLIENT_FILT_LIST.isWhitelistModeOn());
        destructionModeButton.setStateTriggered(FiltPick.CLIENT_FILT_LIST.isDestructionModeOn());
//        filtModeButton.setTooltip(filtModeButton.isStateTriggered() ?
//                Tooltip.create(Component.translatable("whitelist_mode").append("\n").withStyle(ChatFormatting.DARK_GREEN).append(Component.translatable("whitelist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
//                Tooltip.create(Component.translatable("blacklist_mode").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("blacklist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY)))
//        );
//        destructionModeButton.setTooltip(destructionModeButton.isStateTriggered() ?
//                Tooltip.create(Component.translatable("destruction_mode_on").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("destruction_mode_on_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
//                Tooltip.create(Component.translatable("destruction_mode_off").withStyle(ChatFormatting.DARK_GRAY))
//        );
    }

    private void initReturnButton() {
        returnButton = new ImageButton(
                this.leftPos + this.getXSize() - 12 - 7,
                this.topPos + 4,
                12,
                11,
                0,
                0,
                12,
                FILTPICK_RETURN_BUTTON_LOCATION,
                (button) -> {
                    this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
                }
        );
        this.addRenderableWidget(returnButton);
    }

    private void initDestructionModeButton() {
        destructionModeButton = new StateSwitchingButton(
                this.leftPos + 7 + 13,
                this.topPos + 4,
                12,
                12,
                FiltPick.CLIENT_FILT_LIST.isDestructionModeOn()
        );
        destructionModeButton.initTextureValues(0, 0, 16, 16, DESTRUCTION_MOD_BUTTON_TEXTURE_LOCATION);
        this.addRenderableWidget(destructionModeButton);
    }

    private void initFiltModeButton() {
        filtModeButton = new StateSwitchingButton(
                this.leftPos + 7,
                this.topPos + 4,
                12,
                12,
                FiltPick.CLIENT_FILT_LIST.isWhitelistModeOn()
        );
        filtModeButton.initTextureValues(0, 0, 16, 16, FILT_MOD_BUTTON_TEXTURE_LOCATION);
        // Add widgets and precomputed values
        this.addRenderableWidget(filtModeButton);
    }


    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        if (this.filtModeButton.isHoveredOrFocused()) {
            this.renderComponentTooltip(pose, getFiltModeButtonTooltip(), mouseX, mouseY);
        }
        if (this.destructionModeButton.isHoveredOrFocused()) {
            this.renderComponentTooltip(pose, getDestructionModeButtonTooltip(), mouseX, mouseY);
        }

        /*
         * This method is added by the container screen to render
         * a tooltip for whatever slot is hovered over.
         ***/
        this.renderTooltip(pose, mouseX, mouseY);
    }

    private List<Component> getFiltModeButtonTooltip() {
        return this.filtModeButton.isStateTriggered() ?
                new ArrayList<Component>(Arrays.asList(Component.translatable("whitelist_mode").withStyle(ChatFormatting.DARK_GREEN), Component.translatable("whitelist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
                new ArrayList<Component>(Arrays.asList(Component.translatable("blacklist_mode").withStyle(ChatFormatting.DARK_RED), Component.translatable("blacklist_mode_explanation").withStyle(ChatFormatting.DARK_GRAY)));
    }



    private List<Component> getDestructionModeButtonTooltip() {
        return this.destructionModeButton.isStateTriggered() ?
                new ArrayList<Component>(Arrays.asList(Component.translatable("destruction_mode_on").withStyle(ChatFormatting.DARK_RED), Component.translatable("destruction_mode_on_explanation").withStyle(ChatFormatting.DARK_GRAY))) :
                new ArrayList<Component>(Arrays.asList(Component.translatable("destruction_mode_off").withStyle(ChatFormatting.DARK_GRAY)));
    }
    @Override
    protected void renderBg(PoseStack pose, float p_97788_, int p_97789_, int p_97790_) {
        /*
         * Sets the texture location for the shader to use. While up to
         * 12 textures can be set, the shader used within 'blit' only
         * looks at the first texture index.
         */
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        /*
         * Renders the background texture to the screen. 'leftPos' and
         * 'topPos' should already represent the top left corner of where
         * the texture should be rendered as it was precomputed from the
         * 'imageWidth' and 'imageHeight'. The two zeros represent the
         * integer u/v coordinates inside the 256 x 256 PNG file.
         */
        this.blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        if (filtModeButton.mouseClicked(p_97748_, p_97749_, p_97750_)) {
            FiltPick.CLIENT_FILT_LIST.setWhitelistMode(!FiltPick.CLIENT_FILT_LIST.isWhitelistModeOn());
            NetWorkHandler.sendToServer(new SynFiltModesC2SPacket(FiltPick.CLIENT_FILT_LIST));
            updateButtonsStates();
        }
        if (destructionModeButton.mouseClicked(p_97748_, p_97749_, p_97750_)) {
            FiltPick.CLIENT_FILT_LIST.setDestructionMode(!FiltPick.CLIENT_FILT_LIST.isDestructionModeOn());
            NetWorkHandler.sendToServer(new SynFiltModesC2SPacket(FiltPick.CLIENT_FILT_LIST));
            updateButtonsStates();
        }
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

}
