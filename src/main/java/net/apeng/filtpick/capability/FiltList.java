package net.apeng.filtpick.capability;

import net.apeng.filtpick.util.TypeTranslator;
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

    public int isWhitelistModeOnInt() {
        return TypeTranslator.boolToInt(whitelistModeOn);
    }

    public void setWhitelistModeOnInt(int whitelistModeOn) {
        this.whitelistModeOn = TypeTranslator.intToBool(whitelistModeOn);
    }

    public boolean isDestructionModeOn() {
        return destructionModeOn;
    }

    public void setDestructionModeOn(boolean destructionModeOn) {
        this.destructionModeOn = destructionModeOn;
    }

    public int isDestructionModeOnInt() {
        return TypeTranslator.boolToInt(destructionModeOn);
    }

    public void setDestructionModeOnInt(int destructionModeOn) {
        this.destructionModeOn = TypeTranslator.intToBool(destructionModeOn);
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
        this.whitelistModeOn = nbt.getBoolean("whitelistModeOn");
        this.destructionModeOn = nbt.getBoolean("destructionModeOn");
    }

}
