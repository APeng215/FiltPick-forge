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
            boolean matched = hasMatchedItem(pickedItem, filtList);

            //If is black-list mode
            if (!filtList.isWhitelistModeOn()) {
                event.setCanceled(false);
                if (matched) {
                    //If the destruction-mode is on
                    if (filtList.isDestructionModeOn()) itemEntity.discard();
                    event.setCanceled(true);
                }


            }
            //If is white-list mode
            else {
                event.setCanceled(true);
                if (matched) event.setCanceled(false);
                else if (filtList.isDestructionModeOn()) itemEntity.discard();

            }

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


    private static boolean hasMatchedItem(Item pickedItem, FiltList filtList) {
        boolean matched = false;
        //Check if there is matched item in the list
        for (int i = 0; i < filtList.getSlots(); i++) {
            ItemStack itemStack = filtList.getStackInSlot(i);
            if (itemStack.getItem().equals(pickedItem)) {
                matched = true;
            }
        }
        return matched;
    }
}
