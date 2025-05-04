package su.nezushin.clans.cache;

import org.bukkit.Location;

import java.util.List;

public class CachedPlayer {

    private String clanDisplayname;
    private String clanName;
    private boolean friendlyFire;
    private Location homeLocation;

    public CachedPlayer(String clanDisplayname, String clanName, boolean friendlyFire, Location homeLocation) {
        this.clanDisplayname = clanDisplayname;
        this.clanName = clanName;
        this.friendlyFire = friendlyFire;
        this.homeLocation = homeLocation;
    }

    public String getClanDisplayname() {
        return clanDisplayname;
    }

    public void setClanDisplayname(String clanDisplayname) {
        this.clanDisplayname = clanDisplayname;
    }


    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }


    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }
}
