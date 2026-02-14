package su.nezushin.clans.util;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

//Bukkit location will throw exception if world is not found. For-cross server setup it is possible situation. This class needed to prevent it.
public class NClanLocation implements ConfigurationSerializable {

    private double x, y, z;
    private float yaw, pitch;
    private String world;

    public NClanLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = loc.getWorld() == null ? null : loc.getWorld().getName();
    }

    public NClanLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.pitch = pitch;
        this.yaw = yaw;
        this.z = z;
        this.y = y;
        this.x = x;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        if (world != null)
            data.put("world", world);
        data.put("x", this.x);
        data.put("y", this.y);
        data.put("z", this.z);
        data.put("yaw", this.yaw);
        data.put("pitch", this.pitch);
        return data;
    }

    public static NClanLocation deserialize(Map<String, Object> args) {
        return new NClanLocation((String) args.get("world"),
                NumberConversions.toDouble(args.get("x")),
                NumberConversions.toDouble(args.get("y")),
                NumberConversions.toDouble(args.get("z")),
                NumberConversions.toFloat(args.get("yaw")),
                NumberConversions.toFloat(args.get("pitch")));
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
