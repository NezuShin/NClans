package su.nezushin.clans.cache;

import org.bukkit.Location;
import su.nezushin.clans.util.NClanLocation;

import java.util.List;

public class CachedPlayer {

    private String clanDisplayname;
    private String clanName;
    private boolean friendlyFire;
    private NClanLocation homeLocation;

    public CachedPlayer(String clanDisplayname, String clanName, boolean friendlyFire, NClanLocation homeLocation) {
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


    public NClanLocation getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(NClanLocation homeLocation) {
        this.homeLocation = homeLocation;
    }
}
