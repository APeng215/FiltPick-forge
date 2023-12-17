package net.apeng.filtpick.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FiltListProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<FiltList> FILT_LIST = CapabilityManager.get(new CapabilityToken<>() {
    });
    private FiltList filtList = null;
    private final LazyOptional<FiltList> optional = LazyOptional.of(this::getFiltList);

    private FiltList getFiltList() {
        if (this.filtList == null) {
            this.filtList = new FiltList(27);
        }
        return this.filtList;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == FILT_LIST) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return getFiltList().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getFiltList().deserializeNBT(nbt);
    }
}
