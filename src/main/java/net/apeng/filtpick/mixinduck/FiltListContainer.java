package net.apeng.filtpick.mixinduck;

import net.apeng.filtpick.property.FiltListPropertyDelegate;
import net.minecraft.world.SimpleContainer;

/**
 * Indicate a class can provide filtlist objects including filtlist and list properties.
 */
public interface FiltListContainer {

    SimpleContainer getFiltList();

    FiltListPropertyDelegate getFiltListPropertyDelegate();

    /**
     * Reset filtlist and its properties
     */
    void resetFiltListWithProperties();

}
