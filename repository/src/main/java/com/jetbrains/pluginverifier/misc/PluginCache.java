package com.jetbrains.pluginverifier.misc;

import com.intellij.structure.domain.Plugin;
import com.intellij.structure.domain.PluginManager;
import com.intellij.structure.errors.IncorrectPluginException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Evdokimov
 */
public class PluginCache {
  private static final Map<File, SoftReference<Plugin>> myPluginsCache = new HashMap<File, SoftReference<Plugin>>();
  private static final PluginCache INSTANCE = new PluginCache();

  private PluginCache() {
  }

  public static PluginCache getInstance() {
    return INSTANCE;
  }

  /**
   * Returns a plugin from cache or creates it from the specified file
   *
   * @param pluginFile file of a plugin
   * @return null if plugin is not found in the cache
   * @throws IOException if IO error occurs during attempt to create a plugin
   * @throws IncorrectPluginException if the given plugin file is incorrect
   */
  @NotNull
  public synchronized Plugin createPlugin(File pluginFile) throws IOException, IncorrectPluginException {
    if (!pluginFile.exists()) {
      throw new IOException("Plugin file does not exist: " + pluginFile.getAbsoluteFile());
    }

    SoftReference<Plugin> softReference = myPluginsCache.get(pluginFile);

    Plugin res = softReference == null ? null : softReference.get();

    if (res == null) {
      res = PluginManager.getInstance().createPlugin(pluginFile);
      myPluginsCache.put(pluginFile, new SoftReference<Plugin>(res));
    }

    return res;
  }


}
