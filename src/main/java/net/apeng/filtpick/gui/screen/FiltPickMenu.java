package net.apeng.filtpick.gui.screen;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.mixinduck.FiltListContainer;
import net.apeng.filtpick.network.NetWorkHandler;
import net.apeng.filtpick.network.SynMenuFieldC2SPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.server.level.ServerPlayer;
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
    public static final int FILTLIST_DISPLAYED_ROW_NUM = 5;
    public final int scrollSpaceInRow;
    private final ContainerData propertyDelegate;
    private final Inventory playerInventory;
    private final Container filtList;
    private int displayedRowOffset = 0;

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
        this.scrollSpaceInRow = FiltListContainer.ROW_NUM - FILTLIST_DISPLAYED_ROW_NUM;
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
        checkContainerSize(filtList, FILTLIST_DISPLAYED_ROW_NUM * 9);
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

    private static int getActualRowNum() {
        return FiltListContainer.ROW_NUM;
    }

    /**
     * Set displayed row start index and update the slots render.
     * Should be used for both side to maintain slot consistency.
     * @param displayedRowOffset the row index fot the first row of displayed filtlist
     * @exception IndexOutOfBoundsException if displayedRowOffset out of bound
     */
    public void setDisplayedRowOffsetAndUpdate(int displayedRowOffset) {
        if (!validateDisplayedRowOffset(displayedRowOffset)) {
            throw new IndexOutOfBoundsException(String.format("displayedRowOffset %d out of menu bound", displayedRowOffset));
        }
        this.displayedRowOffset = displayedRowOffset;
        updateSlots();
        if (isClientSide()) {
            synRowOffsetWithServer();
        }
    }

    private boolean isClientSide() {
        return !(this.playerInventory.player instanceof ServerPlayer);
    }

    private void synRowOffsetWithServer() {
        if (isClientSide()) {
            NetWorkHandler.send2Server(new SynMenuFieldC2SPacket(displayedRowOffset));
        } else {
            throw new IllegalStateException("Try to syn row offset with server on server side!");
        }
    }

    /**
     * Safe version of {@link #increaseDisplayedRowOffsetAndUpdate()}. Do nothing if the index is already on bound.
     * @return {@code ture} if {@code displayedRowOffset} is modified.
     */
    public boolean safeIncreaseDisplayedRowOffsetAndUpdate() {
        if (validateDisplayedRowOffset(displayedRowOffset + 1)) {
            increaseDisplayedRowOffsetAndUpdate();
            return true;
        }
        return false;
    }

    /**
     * Safe version of {@link #decreaseDisplayedRowOffsetAndUpdate()}. Do nothing if the index is already on bound.
     * @return {@code ture} if {@code displayedRowOffset} is modified.
     */
    public boolean safeDecreaseDisplayedRowOffsetAndUpdate() {
        if (validateDisplayedRowOffset(displayedRowOffset - 1)) {
            decreaseDisplayedRowOffsetAndUpdate();
            return true;
        }
        return false;
    }

    /**
     * Increase displayedRowOffset by 1 and update the slot render. Remember to check the bound first or an exception may be thrown.
     * @see #setDisplayedRowOffsetAndUpdate(int)
     * @exception IllegalStateException {@code displayedRowOffset} is on the high bound so can not be increased
     */
    public void increaseDisplayedRowOffsetAndUpdate() {
        try {
            setDisplayedRowOffsetAndUpdate(displayedRowOffset + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException(
                    String.format("Failed to increase displayedRowOffset from %d to %d", displayedRowOffset, displayedRowOffset + 1), e);
        }
    }

    /**
     * Decrease displayedRowOffset by 1 and update the slot render. Remember to check the bound first or an exception may be thrown.
     * @see #setDisplayedRowOffsetAndUpdate(int)
     * @exception IllegalStateException {@code displayedRowOffset} is on the low bound so can not be decreased
     */
    public void decreaseDisplayedRowOffsetAndUpdate() {
        try {
            setDisplayedRowOffsetAndUpdate(displayedRowOffset - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException(
                    String.format(
                            "Failed to decrease displayedRowOffset from %d to %d",
                            displayedRowOffset, displayedRowOffset - 1),
                    e);
        }
    }

    private boolean validateDisplayedRowOffset(int displayedRowOffset) {
        return displayedRowOffset <= scrollSpaceInRow && displayedRowOffset >= 0;
    }

    public int getDisplayedRowOffset() {
        return displayedRowOffset;
    }

    /**
     * Emulate to {@link ChestMenu#ChestMenu(MenuType, int, Inventory, Container, int)}
     * @param playerInventory
     * @param filtList
     * @see ChestMenu#ChestMenu(MenuType, int, Inventory, Container, int)
     */
    private void addSlots(Inventory playerInventory, Container filtList) {
        int i = (FILTLIST_DISPLAYED_ROW_NUM - 4) * 18;
        addHotbarSlots(playerInventory, i);
        addInventorySlots(playerInventory, i);
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

    private void addHotbarSlots(Inventory playerInventory, int i) {
        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    private void addInventorySlots(Inventory playerInventory, int i) {
        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
    }

    private void addFiltListSlots(Container filtList) {
        for(int j = 0; j < FILTLIST_DISPLAYED_ROW_NUM; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(filtList, k + j * 9 + displayedRowOffset * 9, 8 + k * 18, 18 + j * 18));
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
     * @param actionType the type of slot click, check the docs for each SlotActionType value for details
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
        int filtListIndex = filtSlotIndex + displayedRowOffset * 9;
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

    /**
     * Clear filt list content excluding filt list properties.
     */
    public void clearFiltList() {
        filtList.clearContent();
    }

}
