package org.toxi.wpp.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class Waypoint {
    private String name;
    private Location location;
    private Material displayMaterial;
    private boolean favorite;
    private final long creationTime;

    public Waypoint(String name, Location location, boolean favorite, Material displayMaterial) {
        this.name = name;
        this.location = location;
        this.favorite = favorite;
        this.displayMaterial = displayMaterial;
        this.creationTime = System.currentTimeMillis();
    }

    public Waypoint(String name, Location location) {
        this(name, location, false, Material.BOOK);
    }

    public Location location() {
        return location;
    }

    public Material displayMaterial() {
        return displayMaterial;
    }

    public String name() {
        return name;
    }

    public long creationTime() {
        return creationTime;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
