package net.apeng.filtpick.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleButton extends AbstractButton {
    private final ResourceLocation texture;

    private Boolean selected = false;

    public ToggleButton(int x, int y, int width, int height, Component component, ResourceLocation textureLocation) {
        super(x, y, width, height, component);
        this.texture = textureLocation;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_260253_) {
        p_260253_.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                p_260253_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                p_260253_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }

    }

    public void renderButton(PoseStack pose, int p_93844_, int p_93845_, float p_93846_) {
        RenderSystem.setShaderTexture(0, texture);
        blit(pose, this.getX(), this.getY(), this.isFocused() ? 16.0F : 0.0F, this.selected ? 16.0F : 0.0F, this.width, this.height, 64, 64);


    }


}
