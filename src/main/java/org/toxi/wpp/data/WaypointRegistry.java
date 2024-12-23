package org.toxi.wpp.data;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.toxi.wpp.WaypointsPlugin;
import org.toxi.wpp.storage.PlayerStorageFile;
import org.toxi.wpp.util.SimpleResult;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class WaypointRegistry {
    private final WaypointsPlugin plugin;
    private final Map<UUID, PlayerStorageFile> simpleCache = new ConcurrentHashMap<>();

    public WaypointRegistry(WaypointsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openOverview(Player player) {

    }

    public void openPoint(@NotNull Player player, @NotNull Waypoint point) {

    }

    public void addPoint(@NotNull Player player, @NotNull Waypoint point, final Consumer<SimpleResult> callback) {
        if (isPointPresent(player, point.name())) {
            callback.accept(new SimpleResult(false, Component.translatable("point.already_present")
                    .arguments(Component.text(point.name()))));
            return;
        }

        getData(player).ifPresentOrElse(data -> {
            // add point to point list
            data.getWayPointList().add(point);
            // re-apply sorting
            data.getSortMechanism().prioritized(data.getWayPointList(), data.isPrioritizeFavorites());
            // callback
            callback.accept(new SimpleResult(true, Component.translatable("point.added")
                    .arguments(Component.text(point.name()))));
        }, () -> callback.accept(new SimpleResult(false, Component.translatable("point.not_added"))));

    }

    public void removePoint(@NotNull Player player, @NotNull Waypoint point, final Consumer<SimpleResult> callback) {
        if (!isPointPresent(player, point.name())) {
            callback.accept(new SimpleResult(false, Component.translatable("point.not_present")
                    .arguments(Component.text(point.name()))));
            return;
        }

        getPoints(player).ifPresentOrElse(pointList -> {
            pointList.remove(point);
            callback.accept(new SimpleResult(true, Component.translatable("point.deleted")
                    .arguments(Component.text(point.name()))));
        }, () -> callback.accept(new SimpleResult(false, Component.translatable("point.not_deleted"))));
    }

    public boolean isDataPresent(@NotNull Player player) {
        return getData(player).isPresent();
    }

    public boolean isPointPresent(@NotNull Player player, @NotNull String name) {
        return isDataPresent(player)
                && getPoints(player)
                .stream()
                .anyMatch(list -> list.stream().anyMatch(wp -> wp.name().equals(name)));
    }

    public Optional<Collection<Waypoint>> getPoints(@NotNull Player player) {
        return getData(player).map(PlayerStorageFile.DataFile::getWayPointList);
    }

    public void saveAll() {
        simpleCache.values().forEach(PlayerStorageFile::save);
    }

    public void save(@NotNull Player player) {
        Optional.of(simpleCache.get(player.getUniqueId())).ifPresent(PlayerStorageFile::save);
    }

    public Optional<PlayerStorageFile.DataFile> getData(@NotNull Player player) {
        if (simpleCache.containsKey(player.getUniqueId())) {
            return simpleCache.get(player.getUniqueId()).getData();
        } else {
            var file = new PlayerStorageFile(player.getUniqueId(),
                    new File(String.format("%s/users/%s.json", plugin.getDataFolder().getAbsolutePath(), player.getUniqueId())));
            simpleCache.put(player.getUniqueId(), file);
            return file.getData();
        }
    }

}
