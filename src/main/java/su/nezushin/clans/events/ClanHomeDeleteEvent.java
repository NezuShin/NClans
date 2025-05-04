package su.nezushin.clans.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanHomeDeleteEvent extends Event {

    public static HandlerList handlerList = new HandlerList();

    public String clan;
    public Location location;
    public String server;

    public ClanHomeDeleteEvent(String clan, Location location, String server) {
        this.clan = clan;
        this.location = location;
        this.server = server;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
