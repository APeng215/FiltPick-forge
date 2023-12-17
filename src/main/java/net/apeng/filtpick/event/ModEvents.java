package net.apeng.filtpick.event;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.capability.FiltList;
import net.apeng.filtpick.capability.FiltListProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FiltPick.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void itemsFilt(EntityItemPickupEvent event) {

        ItemEntity itemEntity = event.getItem();
        Item pickedItem = itemEntity.getItem().getItem();
        Player player = event.getEntity();

        player.getCapability(FiltListProvider.FILT_LIST).ifPresent(filtList -> {
            applyFiltPick(event, event.getItem(), filtList, hasMatchedItem(pickedItem, filtList));
        });

    }

    @SubscribeEvent
    public static void attachCapability2Player(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(FiltListProvider.FILT_LIST).isPresent()) {
                event.addCapability(new ResourceLocation(FiltPick.MOD_ID, "filtlist"), new FiltListProvider());
            }
        }
    }

    @SubscribeEvent
    public static void deathPersisting(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        oldPlayer.getCapability(FiltListProvider.FILT_LIST).ifPresent(oldList -> event.getEntity().getCapability(FiltListProvider.FILT_LIST).ifPresent(newList -> newList.copyFrom(oldList)));
        oldPlayer.invalidateCaps();
    }

    private static void applyFiltPick(EntityItemPickupEvent event, ItemEntity itemEntity, FiltList filtList, boolean matched) {
        if (isBlackListMode(filtList)) {
            applyBlackListMode(event, itemEntity, filtList, matched);
        } else {
            applyWhiteListMode(event, itemEntity, filtList, matched);
        }
    }

    private static void applyWhiteListMode(EntityItemPickupEvent event, ItemEntity itemEntity, FiltList filtList, boolean matched) {
        if (!matched) {
            dontPick(event);
            if (isDestructionMode(filtList)) {
                discardItem(itemEntity);
            }
        }
    }

    private static void applyBlackListMode(EntityItemPickupEvent event, ItemEntity itemEntity, FiltList filtList, boolean matched) {
        if (matched) {
            dontPick(event);
            if (isDestructionMode(filtList)) {
                discardItem(itemEntity);
            }
        }
    }

    private static void discardItem(ItemEntity itemEntity) {
        itemEntity.discard();
    }

    private static boolean isDestructionMode(FiltList filtList) {
        return filtList.isDestructionModeOn();
    }

    private static void dontPick(EntityItemPickupEvent event) {
        event.setCanceled(true);
    }

    private static boolean isBlackListMode(FiltList filtList) {
        return !filtList.isWhitelistModeOn();
    }

    private static boolean hasMatchedItem(Item pickedItem, FiltList filtList) {
        //Check if there is matched item in the list
        for (int i = 0; i < filtList.getSlots(); i++) {
            ItemStack itemStack = filtList.getStackInSlot(i);
            if (itemStack.getItem().equals(pickedItem)) {
                return true;
            }
        }
        return false;
    }
}
