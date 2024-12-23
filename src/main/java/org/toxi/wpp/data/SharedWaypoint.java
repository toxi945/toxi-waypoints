package org.toxi.wpp.data;

import org.bukkit.entity.Player;

public record SharedWaypoint(Waypoint waypoint, Player player) {
}
