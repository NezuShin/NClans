package su.nezushin.clans.db;

import org.bukkit.Location;
import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import su.nezushin.clans.NClans;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;
import su.nezushin.clans.util.YamlStringSerializer;

public class NClanTeleport implements AnvilORMSerializable {

    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String player;
    @SqlColumn(type = SqlType.BIGINT)
    private long expire;
    @SqlColumn(type = SqlType.VARCHAR, name = "location")
    private String locationString;

    private Location location;

    public NClanTeleport() {

    }

    public NClanTeleport(String player, Location location) {
        this.location = location;
        this.player = player;
        this.expire = System.currentTimeMillis() + 20000;
    }

    @Override
    public void onDeserialize() {
        location = YamlStringSerializer.deserializeConfigurationSerializable(locationString, Location.class);
    }

    @Override
    public void onSerialize() {
        locationString = YamlStringSerializer.serializeConfigurationSerializable(location);
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
