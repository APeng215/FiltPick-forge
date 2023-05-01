package net.apeng.filtpick.util;


import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String CATEGORY = "key.category.filtpick";


    public static final String SET_ITEM_STR = "key.filtpick.set_item";
    public static final KeyMapping SET_ITEM_MAP = new KeyMapping(SET_ITEM_STR, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);


    public static final String OPEN_FILTLIST_STR = "key.filtpick.open_filtlist";
    public static final KeyMapping OPEN_FILTLIST_MAP = new KeyMapping(OPEN_FILTLIST_STR, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY);

}
