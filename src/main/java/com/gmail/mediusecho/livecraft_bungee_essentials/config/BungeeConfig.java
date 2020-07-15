/*
 * Copyright (c) 2020 Jacob (MediusEcho)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gmail.mediusecho.livecraft_bungee_essentials.config;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfig {

    protected File configurationFile;
    protected Configuration configuration;

    public BungeeConfig (@NotNull LivecraftBungeeEssentials plugin, String path, String name)
    {
        String directory = plugin.getDataFolder() + File.separator + path;
        configurationFile = new File(directory + File.separator + name);

        if (!configurationFile.exists())
        {
            configurationFile.getParentFile().mkdirs();
            if (plugin.getResourceAsStream(name) == null)
            {
                try {
                    configurationFile.createNewFile();
                } catch (IOException ignored) {}
            }

            else
            {
                try (InputStream in = plugin.getResourceAsStream(name)) {
                    Files.copy(in, configurationFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        reload();
    }

    /**
     * Attempts to reload this configuration file.
     * Will fail with an IOException if anything goes wrong.
     */
    public void reload ()
    {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to save this configuration file
     * Will fail with an IOException if anything goes wrong.
     */
    public void save ()
    {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the Bungee Configuration object
     *
     * @return
     *      Configuration
     */
    public Configuration getConfig () {
        return configuration;
    }
}
