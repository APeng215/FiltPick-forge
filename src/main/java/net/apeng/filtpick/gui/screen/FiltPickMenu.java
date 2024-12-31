package net.apeng.filtpick.gui.screen;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.mixinduck.FiltListContainer;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class FiltPickMenu extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FiltPick.ID);
    public static final RegistryObject<MenuType<FiltPickMenu>> TYPE = REGISTER.register("filt_menu", () -> new MenuType(FiltPickMenu::new, FeatureFlags.DEFAULT_FLAGS));
    private final ContainerData propertyDelegate;
    private final Inventory playerInventory;
    private final Container filtList;
    private int displayedRowOffset = 0;

    // For client side
    public FiltPickMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(FiltPick.CONTAINER_SIZE), new SimpleContainerData(2));
    }

    // For server side        
    public FiltPickMenu(int syncId, Inventory playerInventory, Container filtList, ContainerData propertyDelegate) {
        super(TYPE.get(), syncId);
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.filtList = filtList;
        addSlots(playerInventory, filtList);
        addDataSlots(propertyDelegate);
    }

    private static boolean inventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    private void addSlots(Inventory playerInventory, Container filtList) {
        int i = (FiltPick.FILTLIST_DISPLAYED_ROW_NUM - 4) * 18;
        addHotBarSlots(playerInventory, i);
        addInventorySlot(playerInventory, i);
        // FiltList must be added at last for #inventorySlotClicked working properly.
        addFiltList(filtList);
    }

    private void addInventorySlot(Inventory playerInventory, int i) {
        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
    }

    private void addHotBarSlots(Inventory playerInventory, int i) {
        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    /**
     * This is executed on the server as a response to clients sending a {@link ServerboundContainerButtonClickPacket}.
     *
     * @param serverPlayer
     * @param buttonId
     * @return return true to notify client screen handler to update state
     */
    @Override
    public boolean clickMenuButton(Player serverPlayer, int buttonId) {
        switch (buttonId) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID, FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID ->
                    ((FiltListContainer) serverPlayer).getFiltListPropertyDelegate().switchState(buttonId);
            case FiltPickScreen.CLEAR_BUTTON_ID -> ((FiltListContainer) serverPlayer).resetFiltListWithProperties();
        }
        return true;
    }

    public ContainerData getPropertyDelegate() {
        return propertyDelegate;
    }

    private void addFiltList(Container filtList) {
        for (int row = 0; row < FiltPick.FILTLIST_DISPLAYED_ROW_NUM; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                if (index >= filtList.getContainerSize()) {
                    FiltPick.LOGGER.warn(String.format("The size of displayed filtpick window (size: %d) is bigger than " +
                            "actual filtpick container size of the player (size: %d). " +
                            "Excess slots of the window won't be used.", FiltPick.FILTLIST_DISPLAYED_ROW_NUM * 9, filtList.getContainerSize()));
                    return;
                }
                this.addSlot(new Slot(filtList, index, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack2Move = playerInventory.getItem(index);
        if (stack2Move.isEmpty()) return ItemStack.EMPTY;
        if (isInventorySlotClicked(index)) {
            tryAddItem2FiltList(stack2Move);
        } else {
            deleteItemFromFiltList(index);
        }
        return ItemStack.EMPTY; // To cancel infinite invoking
    }

    private void deleteItemFromFiltList(int index) {
        setFiltStackEmpty(index - 36);
        markSlotDirty(index);
    }

    private static boolean isInventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    private void tryAddItem2FiltList(ItemStack stack2Move) {
        if (isFiltListAlreadyContainItem(stack2Move)) return;
        addItem2FiltList(stack2Move);
    }

    private boolean isFiltListAlreadyContainItem(ItemStack stack2Move) {
        return filtList.hasAnyOf(Set.of(stack2Move.getItem()));
    }

    private void addItem2FiltList(ItemStack stack2Move) {
        ItemStack singleItemStack2Add = stack2Move.getItem().getDefaultInstance();
        for (int i = 0; i < filtList.getContainerSize(); i++) {
            ItemStack targetStack = filtList.getItem(i);
            if (targetStack.isEmpty()) {
                filtList.setItem(i, singleItemStack2Add);
                markSlotDirty(i + 36);
                return;
            }
        }
    }

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * Logic comes from Create Mod.
     *
     * @param slotIndex
     * @param button
     * @param actionType the type of slot click, check the docs for each {@link ClickType} value for details
     * @param player
     */
    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (inventorySlotClicked(slotIndex)) {
            super.clicked(slotIndex, button, actionType, player);
        } else {
            onFiltSlotClicked(slotIndex, actionType);
        }
    }

    private void onFiltSlotClicked(int slotIndex, ClickType actionType) {
        int filtSlotIndex = slotIndex - 36;
        switch (actionType) {
            case THROW, QUICK_MOVE -> setFiltStackEmpty(filtSlotIndex);
            case PICKUP, QUICK_CRAFT -> setFiltStackCursorItem(filtSlotIndex);
        }
        markSlotDirty(slotIndex);
    }

    private void setFiltStackCursorItem(int filtSlotIndex) {
        filtList.setItem(filtSlotIndex, getCarried().getItem().getDefaultInstance());
    }

    private void setFiltStackEmpty(int filtSlotIndex) {
        filtList.setItem(filtSlotIndex, ItemStack.EMPTY);
    }

    private void markSlotDirty(int slotIndex) {
        getSlot(slotIndex).setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * @param cursorStack
     * @param pickedSlot
     * @return whether the slot should be extracted when double-click
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack cursorStack, Slot pickedSlot) {
        return pickedSlot.container == playerInventory;
    }

}
