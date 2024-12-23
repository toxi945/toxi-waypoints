package org.toxi.wpp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import nl.odalitadevelopments.menus.OdalitaMenus;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.toxi.wpp.data.WaypointRegistry;
import org.toxi.wpp.storage.LocationTypeAdapter;

import java.util.Locale;
import java.util.ResourceBundle;

public class WaypointsPlugin extends JavaPlugin {
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .create();

    public static Gson getGson() {
        return GSON;
    }

    private OdalitaMenus odalitaMenus;
    private WaypointRegistry waypointRegistry;

    @Override
    public void onEnable() {
        loadConfig();
        loadLanguageBundles();

        // init inventory api
        odalitaMenus = OdalitaMenus.createInstance(this);
        waypointRegistry = new WaypointRegistry(this);

        getLogger().info("Waypoints plugin enabled");
    }

    @Override
    public void onDisable() {
        waypointRegistry.saveAll();
        getLogger().info("Waypoints plugin disabled");
    }

    public WaypointRegistry getWaypointRegistry() {
        return waypointRegistry;
    }

    private void loadLanguageBundles() {
        var registry = TranslationRegistry.create(Key.key(this.getName().toLowerCase() + ":lang"));
        var bundle_EN_US = ResourceBundle.getBundle("en_US", Locale.US, UTF8ResourceBundleControl.get());
        var bundle_DE = ResourceBundle.getBundle("de_DE", Locale.US, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.US, bundle_EN_US, true);
        registry.registerAll(Locale.GERMAN, bundle_DE, true);
        GlobalTranslator.translator().addSource(registry);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}