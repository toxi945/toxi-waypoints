package org.toxi.wpp.storage;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.toxi.wpp.WaypointsPlugin;
import org.toxi.wpp.data.Waypoint;
import org.toxi.wpp.util.SortMechanism;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerStorageFile {
    private final UUID playerId;
    private final File file;
    private final DataFile storage;

    public PlayerStorageFile(@NotNull UUID playerId, @NotNull File file) {
        this.playerId = Preconditions.checkNotNull(playerId);
        this.file = Preconditions.checkNotNull(file);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (isEmpty()) {
            this.storage = new DataFile();
        } else {
            this.storage = readFile();
        }
    }

    public Optional<DataFile> getData() {
        return Optional.of(storage);
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            WaypointsPlugin.getGson().toJson(storage, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void delete() {
        file.delete();
    }

    public boolean isEmpty() {
        return !file.exists() || file.length() == 0;
    }

    private @Nullable DataFile readFile() {
        try (Reader reader = new FileReader(file)) {
            return WaypointsPlugin.getGson().fromJson(reader, DataFile.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public static class DataFile {
        private final List<Waypoint> wayPointList = new ArrayList<>();
        private SortMechanism sortMechanism = SortMechanism.NAME_ASCENDING;

        private boolean prioritizeFavorites = false;
        private boolean enableOverviewFilters = false;

        public void setSortMechanism(SortMechanism sortMechanism) {
            this.sortMechanism = sortMechanism;
        }

        public SortMechanism getSortMechanism() {
            return sortMechanism;
        }

        public List<Waypoint> getWayPointList() {
            return wayPointList;
        }

        public void setEnableOverviewFilters(boolean enableOverviewFilters) {
            this.enableOverviewFilters = enableOverviewFilters;
        }

        public void setPrioritizeFavorites(boolean prioritizeFavorites) {
            this.prioritizeFavorites = prioritizeFavorites;
        }

        public boolean isEnableOverviewFilters() {
            return enableOverviewFilters;
        }

        public boolean isPrioritizeFavorites() {
            return prioritizeFavorites;
        }
    }

}
