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
import org.jetbrains.annotations.NotNull;

public class FiltPickMenu extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FiltPick.ID);
    public static final RegistryObject<MenuType<FiltPickMenu>> TYPE = REGISTER.register("filt_menu", () -> new MenuType(FiltPickMenu::new, FeatureFlags.DEFAULT_FLAGS));
    private static final int FILTPICK_ROW_NUM = 3;
    private final ContainerData propertyDelegate;
    private final Inventory playerInventory;
    private final Container filtList;
    private int displayedRowStartIndex = 0;

    // For client side
    public FiltPickMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(FiltListContainer.CAPACITY), new SimpleContainerData(2));
    }

    // For server side        
    public FiltPickMenu(int syncId, Inventory playerInventory, Container filtList, ContainerData propertyDelegate) {
        super(TYPE.get(), syncId);
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.filtList = filtList;
        checkSize(filtList, propertyDelegate);
        addSlots(playerInventory, filtList);
        addDataSlots(propertyDelegate);
    }

    private void clearAllSlots() {
        this.slots.clear();
        this.remoteSlots.clear();
        this.lastSlots.clear();
    }

    private static void checkSize(Container filtList, ContainerData propertyDelegate) {
        checkContainerSize(filtList, FiltListContainer.CAPACITY);
        checkContainerDataCount(propertyDelegate, 2);
    }

    private static boolean inventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }

    private void updateSlots() {
        clearAllSlots();
        addSlots(playerInventory, filtList);
    }

    /**
     * Set displayed row start index and update the slots render.
     * Should be used for both side to maintain slot consistency.
     * @param displayedRowStartIndex the row index fot the first row of displayed filtlist
     * @exception IndexOutOfBoundsException if displayedRowStartIndex out of bound
     */
    public void setDisplayedRowStartIndexAndUpdate(int displayedRowStartIndex) {
        if (!validateDisplayedRowStartIndex(displayedRowStartIndex)) {
            throw new IndexOutOfBoundsException(String.format("displayedRowStartIndex %d out of menu bound", displayedRowStartIndex));
        }
        this.displayedRowStartIndex = displayedRowStartIndex;
        updateSlots();
    }

    private static boolean validateDisplayedRowStartIndex(int displayedRowStartIndex) {
        return displayedRowStartIndex < FiltListContainer.ROW_NUM - FILTPICK_ROW_NUM + 1;
    }

    private void addSlots(Inventory playerInventory, Container filtList) {
        addHotbarSlots(playerInventory);
        addInventorySlots(playerInventory);
        // FiltList must be added at last
        addFiltListSlots(filtList);
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
            case FiltPickScreen.CLEAR_BUTTON_ID -> ((FiltListContainer) serverPlayer).resetFiltListAndProperties();
        }
        return true;
    }

    public ContainerData getPropertyDelegate() {
        return propertyDelegate;
    }

    private void addHotbarSlots(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    private void addInventorySlots(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addFiltListSlots(Container filtList) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(filtList, i * 9 + j + displayedRowStartIndex * 9, 8 + j * 18, 18 + i * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (inventorySlotClicked(index)) {
            ItemStack stackToInsert = playerInventory.getItem(index);
            for (int i = 0; i < filtList.getContainerSize(); i++) {
                ItemStack stack = filtList.getItem(i);
                if (!allowRepeats() && canItemStacksStack(stack, stackToInsert))
                    break;
                if (stack.isEmpty()) {
                    ItemStack copy = stackToInsert.copy();
                    copy.setCount(1);
                    filtList.setItem(i, copy);
                    markSlotDirty(i + 36);
                    break;
                }
            }
        } else {
            setFiltStackEmpty(index - 36);
            markSlotDirty(index);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * Logic comes from Create Mod.
     *
     * @param slotIndex
     * @param button
     * @param actionType the type of slot click, check the docs for each {@link SlotActionType} value for details
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
        int filtListIndex = filtSlotIndex + displayedRowStartIndex * 9;
        switch (actionType) {
            case THROW, QUICK_MOVE -> setFiltStackEmpty(filtListIndex);
            case PICKUP, QUICK_CRAFT -> setFiltStackCursorItem(filtListIndex);
        }
    }

    private void setFiltStackCursorItem(int filtListIndex) {
        filtList.setItem(filtListIndex, getCarried().getItem().getDefaultInstance());
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

    protected boolean allowRepeats() {
        return false;
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

    public void clearFiltList() {
        filtList.clearContent();
    }

}
