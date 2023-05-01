package net.apeng.filtpick.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FiltScreen extends AbstractContainerScreen<FiltMenu> {

    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/container/shulker_box.png");

    public FiltScreen(FiltMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.titleLabelX = 10;
        this.inventoryLabelX = 10;
    }

    @Override
    protected void init() {
        super.init();

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
}
