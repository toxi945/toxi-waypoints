package org.toxi.wpp.util;

import org.bukkit.Location;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final Format DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String toReadableTime(long time) {
        return DATE_FORMAT.format(new Date(time));
    }

    public static String blockLocationToString(Location location) {
        return String.format("%d / %d / %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String locationToString(Location location) {
        return String.format("%f / %f / %f", location.getX(), location.getY(), location.getZ());
    }
}
