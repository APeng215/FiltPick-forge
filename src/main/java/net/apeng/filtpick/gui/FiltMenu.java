package net.apeng.filtpick.gui;

import net.apeng.filtpick.FiltPick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FiltMenu extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FiltPick.MOD_ID);
    private final ItemStackHandler ghostInventory;
    private final Inventory playerInventory;
    public static final RegistryObject<MenuType<FiltMenu>> MENU_TYPE = REGISTER.register("filt_screen", () -> IForgeMenuType.create(FiltMenu::new));
    // Client menu constructor
    public FiltMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new ItemStackHandler(27));
    }

    // Server menu constructor
    public FiltMenu(int containerId, Inventory playerInventory, ItemStackHandler dataInventory) {
        super(MENU_TYPE.get(), containerId);
        this.ghostInventory = dataInventory;
        this.playerInventory = playerInventory;


        // Add slots for player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, 9 + i * 9 + j, 8 + j * 18, 84 + i * 18));
            }
        }
        // Add hot bar slots of player
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }

        // Add slots for data inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new SlotItemHandler(dataInventory, i * 9 + j, 8 + j * 18, 18 + i * 18));
            }
        }



    }

    protected boolean allowRepeats() {
        return false;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container == playerInventory;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }

    @Override
    public boolean canDragTo(Slot slotIn) {
        if (allowRepeats())
            return true;
        return slotIn.container == playerInventory;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId < 36) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        if (clickTypeIn == ClickType.THROW)
            return;

        ItemStack held = getCarried();
        int slot = slotId - 36;
        if (clickTypeIn == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stackInSlot = ghostInventory.getStackInSlot(slot)
                        .copy();
                stackInSlot.setCount(stackInSlot.getMaxStackSize());
                setCarried(stackInSlot);
                return;
            }
            return;
        }

        ItemStack insert;
        if (held.isEmpty()) {
            insert = ItemStack.EMPTY;
        } else {
            insert = held.copy();
            insert.setCount(1);
        }
        ghostInventory.setStackInSlot(slot, insert);
        getSlot(slotId).setChanged();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index < 36) {
            ItemStack stackToInsert = playerInventory.getItem(index);
            for (int i = 0; i < ghostInventory.getSlots(); i++) {
                ItemStack stack = ghostInventory.getStackInSlot(i);
                if (!allowRepeats() && ItemHandlerHelper.canItemStacksStack(stack, stackToInsert))
                    break;
                if (stack.isEmpty()) {
                    ItemStack copy = stackToInsert.copy();
                    copy.setCount(1);
                    ghostInventory.insertItem(i, copy, false);
                    getSlot(i + 36).setChanged();
                    break;
                }
            }
        } else {
            ghostInventory.extractItem(index - 36, 1, false);
            getSlot(index).setChanged();
        }
        return ItemStack.EMPTY;
    }




}
