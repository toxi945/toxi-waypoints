package org.toxi.wpp.storage;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name("x");
        out.value(value.getX());
        out.name("y");
        out.value(value.getY());
        out.name("z");
        out.value(value.getZ());
        out.name("yaw");
        out.value(value.getYaw());
        out.name("pitch");
        out.value(value.getPitch());
        out.name("worldName");
        out.value(value.getWorld().getName());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        in.beginObject();

        double x = 0, y = 0, z = 0;
        float yaw = 0, pitch = 0;
        String worldName = null, fieldName = null;

        while (in.hasNext()) {
            JsonToken token = in.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = in.nextName();
            }
            assert fieldName != null;

            switch (fieldName) {
                case "x" -> x = in.nextDouble();
                case "y" -> y = in.nextDouble();
                case "z" -> z = in.nextDouble();
                case "yaw" -> yaw = (float) in.nextDouble();
                case "pitch" -> pitch = (float) in.nextDouble();
                case "worldName" -> worldName = in.nextString();
            }
            token = in.peek();
        }
        in.endObject();
        assert worldName != null;
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
