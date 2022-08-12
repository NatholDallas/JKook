/*
 * Copyright 2022 JKook contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package snw.jkook.plugin;

import org.slf4j.Logger;
import snw.jkook.config.file.FileConfiguration;
import snw.jkook.config.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a basic Kook Plugin implementation. Use YAML as configuration. <p>
 * Plugin developers should extend this class to make their own Plugin program.
 */
public abstract class BasePlugin implements Plugin {
    private final Logger logger;
    private final File configFile;
    private FileConfiguration configuration;
    private final File dataFolder;
    private final File file;
    private final PluginDescription description;
    private volatile boolean enabled = false;

    // Plugin developers should NEVER use this constructor.
    // And they should NEVER define new constructor.
    // This constructor should be called by Plugin loaders (provided by API implementations).
    protected BasePlugin(
            final File configFile,
            final File dataFolder,
            final PluginDescription description,
            final File file,
            final Logger logger
    ) {
        if (!(getClass().getClassLoader() instanceof PluginLoader)) {
            throw new InvalidPluginException("This class should be loaded by using PluginLoader.");
        }
        this.configFile = Objects.requireNonNull(configFile);
        this.dataFolder = Objects.requireNonNull(dataFolder);
        this.description = Objects.requireNonNull(description);
        this.file = Objects.requireNonNull(file);
        this.logger = Objects.requireNonNull(logger);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public final void setEnabled(boolean enabled) {
        if (!this.enabled == enabled) {
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
            this.enabled = enabled; // make sure the plugin "really" enabled, no exception occurred
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public FileConfiguration getConfig() {
        return configuration;
    }

    // Override this if you don't want to use the YAML as the configuration.
    @Override
    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(configFile);

        final InputStream backend = getResource("config.yml");
        if (backend == null) {
            return;
        }

        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(backend, StandardCharsets.UTF_8)));
    }

    @Override
    public void saveDefaultConfig() {
        saveResource("config.yml", false, false);
    }

    @Override
    public void saveResource(final String path, final boolean replace, final boolean ignorePathStructure) throws IllegalArgumentException {
        try (final InputStream stream = getResource(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("The target resource does not embedded in the Plugin JAR file");
            }

            final String targetPath;
            if (ignorePathStructure) {
                final String[] pathSplit = path.split("/");
                targetPath = pathSplit[pathSplit.length - 1];
            } else {
                targetPath = path;
            }

            final File local = new File(dataFolder, targetPath);
            if (local.exists()) {
                if (!replace) {
                    getLogger().warn("Cannot save resource \"{}\" because it has already exists.", path);
                    return;
                }
                //noinspection ResultOfMethodCallIgnored
                local.delete();
            } else {
                //noinspection ResultOfMethodCallIgnored
                local.mkdirs();
            }

            try (final FileOutputStream out = new FileOutputStream(local)) {
                int index;
                byte[] bytes = new byte[1024];
                while ((index = stream.read(bytes)) != -1) {
                    out.write(bytes, 0, index);
                }
            }
        } catch (IOException e) {
            getLogger().warn("Cannot save resource because an error occurred.", e);
        }
    }

    @Override
    public InputStream getResource(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClass().getClassLoader().getResource(path);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public PluginDescription getDescription() {
        return description;
    }
}
