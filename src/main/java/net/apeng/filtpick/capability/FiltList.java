package net.apeng.filtpick.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.items.ItemStackHandler;

@AutoRegisterCapability
public class FiltList extends ItemStackHandler {

    private boolean whitelistModeOn = false;
    private boolean destructionModeOn = false;

    public FiltList(int size) {
        super(size);
    }


    public boolean isWhitelistModeOn() {
        return whitelistModeOn;
    }

    public void setWhitelistModeOn(boolean whitelistModeOn) {
        this.whitelistModeOn = whitelistModeOn;
    }

    public boolean isDestructionModeOn() {
        return destructionModeOn;
    }

    public void setDestructionModeOn(boolean destructionModeOn) {
        this.destructionModeOn = destructionModeOn;
    }

    public void copyFrom(FiltList source) {
        this.whitelistModeOn = source.whitelistModeOn;
        this.destructionModeOn = source.destructionModeOn;
        for (int i = 0; i < source.getSlots(); i++) {
            this.setStackInSlot(i, source.getStackInSlot(i));
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();
        compoundTag.putBoolean("whitelistModeOn", whitelistModeOn);
        compoundTag.putBoolean("destructionModeOn", destructionModeOn);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        this.whitelistModeOn = nbt.getBoolean("WhitelistModeOn");
        this.destructionModeOn = nbt.getBoolean("DestructionModeOn");
    }

}
