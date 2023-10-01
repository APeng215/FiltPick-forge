package net.apeng.filtpick.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.apeng.filtpick.networking.packet.SynFiltListC2SPacket;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FiltScreen extends AbstractContainerScreen<FiltMenu> {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/container/shulker_box.png");
    public static final ResourceLocation FILT_MOD_BUTTON_TEXTURE_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_mode_button.png");
    public static final ResourceLocation DESTRUCTION_MOD_BUTTON_TEXTURE_LOCATION = new ResourceLocation(FiltPick.MOD_ID, "textures/guis/filtpick_destruction_on_button.png");

    private StateSwitchingButton filtModeButton, destructionModeButton;

    public FiltScreen(FiltMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.titleLabelX = 72;
        this.inventoryLabelX = 10;
    }

    @Override
    protected void init() {
        super.init();

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


        destructionModeButton = new StateSwitchingButton(
                this.leftPos + 7 + 13,
                this.topPos + 4,
                12,
                12,
                FiltPick.CLIENT_FILT_LIST.isDestructionModeOn()
        );
        destructionModeButton.initTextureValues(0, 0, 16, 16, DESTRUCTION_MOD_BUTTON_TEXTURE_LOCATION);
        // Add widgets and precomputed values
        this.addRenderableWidget(destructionModeButton);
    }


    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        /*
         * This method is added by the container screen to render
         * a tooltip for whatever slot is hovered over.
         ***/
        this.renderTooltip(pose, mouseX, mouseY);
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
            filtModeButton.setStateTriggered(!filtModeButton.isStateTriggered());
            FiltPick.CLIENT_FILT_LIST.setWhitelistModeOn(!FiltPick.CLIENT_FILT_LIST.isWhitelistModeOn());
            NetWorkHandler.sendToServer(new SynFiltListC2SPacket(FiltPick.CLIENT_FILT_LIST));
        }
        if (destructionModeButton.mouseClicked(p_97748_, p_97749_, p_97750_)) {
            destructionModeButton.setStateTriggered(!destructionModeButton.isStateTriggered());
            FiltPick.CLIENT_FILT_LIST.setDestructionModeOn(!FiltPick.CLIENT_FILT_LIST.isDestructionModeOn());
            NetWorkHandler.sendToServer(new SynFiltListC2SPacket(FiltPick.CLIENT_FILT_LIST));
        }

        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }
}
