package net.apeng.filtpick.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.awt.event.KeyEvent;

public class ScrollBar extends AbstractWidget {

    private final int upBoundY;
    private final int scrollSpaceY; // NOT include bar itself!
    private double scrollRatio = 0;

    public ScrollBar(int pX, int pY, int scrollSlotHeight) {
        super(pX, pY, ScrollBarResource.WIDTH, ScrollBarResource.HEIGHT, Component.empty());
        this.upBoundY = pY;
        this.scrollSpaceY = scrollSlotHeight - ScrollBarResource.HEIGHT;
    }

    private void setScrollRatioAndUpdateRender(double newScrollRatio) {
        scrollRatio = Mth.clamp(newScrollRatio, 0, 1);
        this.setY(upBoundY + (int)(scrollRatio * scrollSpaceY));
    }

    /**
     * Render widget itself, excluding tooltip.
     * @param guiGraphics
     * @param pMouseX
     * @param pMouseY
     * @param pPartialTick
     */
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guiGraphics.blit(
                ScrollBarResource.LOCATION,
                getX(),
                getY(),
                ScrollBarResource.U + (this.isActive() ? 0 : ScrollBarResource.WIDTH),
                ScrollBarResource.V,
                ScrollBarResource.WIDTH,
                ScrollBarResource.HEIGHT
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    /**
     * Called when the mouse wheel is scrolled within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pDelta  the scrolling delta.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isActive()) {
            setY(Mth.clamp(getY() - (int)(2 * pDelta), upBoundY, upBoundY + scrollSpaceY));
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     *
     * @param pKeyCode   the key code of the pressed key.
     * @param pScanCode  the scan code of the pressed key.
     * @param pModifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (isActive()) {
            pKeyCode = KeyEvent.VK_UP;
            switch (pKeyCode) {
                case KeyEvent.VK_UP -> {
                    // TODO
                }
                case KeyEvent.VK_DOWN -> {
                    // TODO
                }
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public void onClick(double pMouseX, double pMouseY) {
    }

    public void onRelease(double pMouseX, double pMouseY) {
    }

    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        setY(Mth.clamp((int)pMouseY - ScrollBarResource.HEIGHT / 2, upBoundY, upBoundY + scrollSpaceY));
    }

    public static class ScrollBarResource {
        private static final ResourceLocation LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
        private static final int U = 232;
        private static final int V = 0;
        private static final int WIDTH = 12;
        private static final int HEIGHT = 15;
    }
}
