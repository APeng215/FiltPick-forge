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

    // For client side
    public FiltPickMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(27), new SimpleContainerData(2));
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

    private static void checkSize(Container filtList, ContainerData propertyDelegate) {
        checkContainerSize(filtList, 27);
        checkContainerDataCount(propertyDelegate, 2);
    }

    private static boolean inventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    private void addSlots(Inventory playerInventory, Container filtList) {
        addHotbarSlots(playerInventory);
        addInventorySlots(playerInventory);
        // FiltList must be added at last
        addFiltList(filtList);
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

    private void addFiltList(Container filtList) {
        // Add slots for data inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(filtList, i * 9 + j, 8 + j * 18, 18 + i * 18));
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
