package net.apeng.filtpick.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * Special {@link SimpleContainer} that CAN save slot positions for item stacks
 */
public class SlotsContainer extends SimpleContainer {

    public SlotsContainer(int pSize) {
        super(pSize);
    }

    public SlotsContainer(ItemStack... pItems) {
        super(pItems);
    }

    public void fromTag(ListTag pContainerNbt) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for(int k = 0; k < pContainerNbt.size(); ++k) {
            CompoundTag compoundtag = pContainerNbt.getCompound(k);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < this.getContainerSize()) {
                this.setItem(j, ItemStack.of(compoundtag));
            }
        }

    }

    public ListTag createTag() {
        ListTag listtag = new ListTag();

        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }

        return listtag;
    }

}
