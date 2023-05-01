package net.apeng.filtpick.gui;

import net.apeng.filtpick.FiltPick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class FiltMenu extends AbstractContainerMenu {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FiltPick.MOD_ID);
    private Inventory inventory;

    // Client menu constructor, supplies MenuType
    public FiltMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new ItemStackHandler(27), new SimpleContainerData(2));
    }

    // Server menu constructor
    public FiltMenu(int containerId, Inventory playerInventory, IItemHandler dataInventory, ContainerData dataMultiple) {
        super(MENU_TYPE.get(), containerId);

        // Check if the data inventory size is some fixed value
        checkContainerDataCount(dataMultiple, 2);
        // Then, add slots for data inventory
        this.addSlot(new SlotItemHandler(dataInventory, 0, 0, 0));

        // Add slots for player inventory
        this.addSlot(new Slot(playerInventory, 0, 0, 0));

        // Add data slots for handled integers
        this.addDataSlots(dataMultiple);

        // ...
    }

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        return ItemStack.EMPTY;
    }    public static final RegistryObject<MenuType<FiltMenu>> MENU_TYPE = REGISTER.register(FiltPick.MOD_ID, () -> IForgeMenuType.create(FiltMenu::new));

    @Override
    public boolean stillValid(Player player) {
        return true;
    }




}
