package net.apeng.filtpick.mixinduck;

import net.apeng.filtpick.util.FiltListPropertyDelegate;
import net.minecraft.world.SimpleContainer;

public interface FiltListContainer {

    SimpleContainer getFiltList();

    FiltListPropertyDelegate getFiltListPropertyDelegate();

    void resetFiltListWithProperties();

}
