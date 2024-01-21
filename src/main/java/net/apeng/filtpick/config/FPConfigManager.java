package net.apeng.filtpick.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton
 */
public final class FPConfigManager {

    private static FPConfigManager INSTANCE;
    private static final int EOF = -1;
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private FileChannel fileChannel;
    private WidgetOffsetConfig widgetOffsetConfig;

    private FPConfigManager(Path configDirPath) {
        initFileChannel(configDirPath);
        if (isFileEmpty()) {
            widgetOffsetConfig = new WidgetOffsetConfig();
            try2Write2ConfigFile(gson.toJson(widgetOffsetConfig.configMap));
            return;
        }
        // TODO
    }



    private boolean isFileEmpty() {
        return try2ReadFullConfigFile().isEmpty();
    }

    private void try2Write2ConfigFile(String content) {
        try {
            writeConfigFile(content);
        } catch (IOException e) {
            logger.error("Fail to write config to config file.");
            throw new RuntimeException(e);
        }

    }

    private void writeConfigFile(String content) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
        fileChannel.truncate(0); // Clear the original content
        fileChannel.write(buffer);
    }

    /**
     * Read config file content as String.
     * @return the content of the config file.
     */
    private String try2ReadFullConfigFile() {
        nullCheck4FileChannel();
        try {
            return readFullConfigFile();
        } catch (IOException e) {
            logger.error("Fail to readFullConfigFile config file.");
            throw new RuntimeException(e);
        }
    }

    private String readFullConfigFile() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        StringBuilder stringBuilder = new StringBuilder();
        resetChannelPosition();
        while (fileChannel.read(buffer) != EOF) {
            while (buffer.hasRemaining()) {
                stringBuilder.append(buffer.getChar());
            }
        }
        return stringBuilder.toString();
    }

    private void resetChannelPosition() throws IOException {
        fileChannel.position(0);
    }

    private void nullCheck4FileChannel() {
        if (fileChannel == null) {
            throw new NullPointerException("FileChannel is null.");
        }
    }

    private void initFileChannel(Path configDirPath) {
        try {
            fileChannel = FileChannel.open(configDirPath.resolve("filtpick.json"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error("Fail to resolve filtpick.json configuration file.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Get singleton for FPConfigManager.
     * @return the singleton.
     */
    public static FPConfigManager getInstance() {
        nullCheck4Instance();
        return INSTANCE;
    }

    /**
     * Get the singleton for FPConfigManager.
     * If the config file doesn't exist, it will be created automatically.
     * @param configDirPath the configuration directory path.
     * @return the singleton
     */
    public static FPConfigManager getInstance(Path configDirPath) {
        if (INSTANCE != null){
            return INSTANCE;
        }
        initInstance(configDirPath);
        return INSTANCE;
    }

    private static void initInstance(Path configDirPath) {
        INSTANCE = new FPConfigManager(configDirPath);
    }

    private static void nullCheck4Instance() {
        if (INSTANCE == null) {
            throw new NullPointerException("Config instance hasn't been initialized. Please init it via overload for FPConfigManager#getInstance first.");
        }
    }

    private static class WidgetOffsetConfig {

        private enum Key {
            ENTRY_BUTTON,
            FILT_MODE_BUTTON,
            DESTRUCTION_MODE_BUTTON,
            CLEAR_BUTTON,
            RETURN_BUTTON;
        }

        private final Map<Key, WidgetPosOffset> configMap = new HashMap<>();

        /**
         * Return new WidgetOffsetConfig with default values initialized.
         */
        private WidgetOffsetConfig() {
            setDefaultValues();
        }

        private void setDefaultValues() {
            for (Key key : Key.values()) {
                configMap.put(key, WidgetPosOffset.DEFAULT);
            }
        }

        private void setOffset(Key key, WidgetPosOffset offset) {
            configMap.put(key, offset);
        }
    }

}
