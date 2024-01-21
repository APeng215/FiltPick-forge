package net.apeng.filtpick.config;

public record WidgetPosOffset(int xOffset, int yOffset) {
    public static final WidgetPosOffset DEFAULT = new WidgetPosOffset(0, 0);
}
