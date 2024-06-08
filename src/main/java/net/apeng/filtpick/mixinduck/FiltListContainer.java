package net.apeng.filtpick.mixinduck;

import net.apeng.filtpick.property.FiltListPropertyDelegate;
import net.minecraft.world.SimpleContainer;

/**
 * Indicate a class can provide filtlist objects including filtlist and list properties.
 */
public interface FiltListContainer {

    int CAPACITY = 27 * 4;
    int ROW_NUM = CAPACITY / 9;

    SimpleContainer getFiltList();

    FiltListPropertyDelegate getFiltListPropertyDelegate();

    /**
     * Reset filtlist and its properties
     */
    void resetFiltListAndProperties();

}
