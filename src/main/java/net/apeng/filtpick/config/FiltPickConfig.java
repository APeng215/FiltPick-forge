package net.apeng.filtpick.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;

public class FiltPickConfig {

    public final EnumMap<ButtonName, ButtonOffset> buttonOffsets = new EnumMap<>(ButtonName.class);

    public FiltPickConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Button offset configuration").push("buttons");

        for (ButtonName key : ButtonName.values()) {
            buttonOffsets.put(key, defineButtonOffset(builder, key));
        }

        builder.pop();
    }

    private ButtonOffset defineButtonOffset(ForgeConfigSpec.Builder builder, ButtonName key) {
        builder.push(key.name());

        ForgeConfigSpec.ConfigValue<Integer> horizontalOffset = builder
                .define("horizontal offset", 0);

        ForgeConfigSpec.ConfigValue<Integer> verticalOffset = builder
                .define("vertical offset", 0);

        builder.pop();

        return new ButtonOffset(horizontalOffset, verticalOffset);
    }

    public enum ButtonName {
        RECIPE_BUTTON,
        ENTRY_BUTTON,
        FILT_MODE_BUTTON,
        DESTRUCTION_MODE_BUTTON,
        CLEAR_BUTTON,
        RETURN_BUTTON
    }

    public record ButtonOffset(ForgeConfigSpec.ConfigValue<Integer> horizontalOffset, ForgeConfigSpec.ConfigValue<Integer> verticalOffset) {
    }

}
