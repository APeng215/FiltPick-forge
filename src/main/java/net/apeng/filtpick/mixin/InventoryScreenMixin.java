package net.apeng.filtpick.mixin;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.config.FPConfigManager;
import net.apeng.filtpick.gui.util.LegacyTexturedButtonWidget;
import net.apeng.filtpick.network.NetworkHandler;
import net.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    @Shadow private @Final RecipeBookComponent recipeBookComponent;

    @Unique private static final ResourceLocation FILTPICK_ENTRY_TEXTURE = ResourceLocation.tryBuild(FiltPick.ID, "gui/entry_button.png");
    @Unique private ImageButton recipeBookButton;
    @Unique private ImageButton filtPickEntryButton;
    @Unique private static int filtPickEntryButtonPosX;
    @Unique private static int filtPickEntryButtonPosY;
    @Unique private static int recipeButtonPosX;
    @Unique private static int recipeButtonPosY;

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    private GuiEventListener configureRecipeBookButton(InventoryScreen instance, GuiEventListener element) {
        initRecipeBookButton();
        addRecipeBookButton();
        return element;
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void initAndAddFiltPickEntryButton(CallbackInfo ci) {
        initFiltPickEntryButton();
        addFiltPickEntryButton();
    }

    @Unique
    private void addRecipeBookButton() {
        this.addRenderableWidget(recipeBookButton);
    }

    @Unique
    private void addFiltPickEntryButton() {
        this.addRenderableWidget(filtPickEntryButton);
    }

    @Unique
    private void initRecipeBookButton() {
        calculateRecipeButtonPos();
        recipeBookButton = new ImageButton(recipeButtonPosX, recipeButtonPosY, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
            recipeBookComponent.toggleVisibility();
            this.leftPos = recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            calculateRecipeButtonPos();
            recipeBookButton.setPosition(recipeButtonPosX, recipeButtonPosY);
            calculateEntryButtonPos();
            filtPickEntryButton.setPosition(filtPickEntryButtonPosX, filtPickEntryButtonPosY);
        });
    }

    @Unique
    private void initFiltPickEntryButton() {
        calculateEntryButtonPos();
        filtPickEntryButton = new LegacyTexturedButtonWidget(filtPickEntryButtonPosX, filtPickEntryButtonPosY, 20, 18, 0, 0, 19, FILTPICK_ENTRY_TEXTURE, button -> NetworkHandler.send2Server(new OpenFiltPickScreenC2SPacket()));
        setTooltip2EntryButton();
    }

    /**
     * Should be invoked every time before the positions are accessed.
     */
    @Unique
    private void calculateEntryButtonPos() {
        filtPickEntryButtonPosX = this.leftPos + 104 + 23 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.ENTRY_BUTTON).xOffset();
        filtPickEntryButtonPosY = this.height / 2 - 22 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.ENTRY_BUTTON).yOffset();
    }

    /**
     * Should be invoked every time before the positions are accessed.
     */
    @Unique
    private void calculateRecipeButtonPos() {
        recipeButtonPosX = this.leftPos + 104 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RECIPE_BUTTON).xOffset();
        recipeButtonPosY = this.height / 2 - 22 + FiltPick.CONFIG_MANAGER.getWidgetPosOffset(FPConfigManager.WidgetOffsetConfig.Key.RECIPE_BUTTON).yOffset();
    }

    @Unique
    private void setTooltip2EntryButton() {
        filtPickEntryButton.setTooltip(Tooltip.create(Component.translatable("filtpick_screen_name").withStyle(ChatFormatting.YELLOW).append(": ").append(Component.translatable("entry_button_tooltip"))));
        filtPickEntryButton.setTooltipDelay(500);
    }

}
