package su.nezushin.clans.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import su.nezushin.clans.NClans;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;
import su.nezushin.clans.enums.ClanTax;
import su.nezushin.clans.messages.packets.impl.BroadcastClanMsgMessagePacket;
import su.nezushin.clans.msg.Message;
import su.nezushin.clans.util.Config;
import su.nezushin.clans.util.Util;
import su.nezushin.clans.util.YamlStringSerializer;

public class NClan implements AnvilORMSerializable {
    //Database things
    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String name;
    @SqlColumn(type = SqlType.VARCHAR, name = "display_name")
    private String displayName;
    @SqlColumn(type = SqlType.BOOLEAN, name = "friendly_fire")
    private boolean friendlyFire;
    @SqlColumn(type = SqlType.BOOLEAN, name = "join_alarm")
    private boolean joinAlarm;
    @SqlColumn(type = SqlType.VARCHAR)
    private String owner;

    @SqlColumn(type = SqlType.VARCHAR, name = "home_server")
    private String homeServer;

    @SqlColumn(type = SqlType.VARCHAR, name = "home")
    private String homeString;

    @SqlColumn(type = SqlType.VARCHAR, name = "permissions")
    private String permissionsString;


    @SqlColumn(type = SqlType.BIGINT, name = "created_at")
    private long createdAt;


    @SqlColumn(type = SqlType.BIGINT, name = "next_taxes_take")
    private long nextTaxesTake;

    @SqlColumn(type = SqlType.DOUBLE, name = "balance")
    private double balance;


    private Location home;
    private Map<ClanAction, ClanGroup> permissions = new HashMap<>();

    @Override
    public void onSerialize() {
        this.homeString = YamlStringSerializer.serializeConfigurationSerializable(home);
        this.permissionsString = YamlStringSerializer.serializePermissionMap(permissions);
    }

    @Override
    public void onDeserialize() {
        this.home = YamlStringSerializer.deserializeConfigurationSerializable(homeString, Location.class);
        this.permissions = YamlStringSerializer.deserializePermissionMap(permissionsString);
    }

    //Runtime things
    private List<NClanPlayer> fetchedPlayers;


    public List<NClanPlayer> fetchPlayers() {
        fetchedPlayers = NClans.getInstance().getDatabase().getPlayers().query().where("clan", this.name).completeAsList();
        return fetchedPlayers;
    }

    public int countOnlinePlayers() {
        return NClans.getInstance()
                .getDatabase()
                .getPlayers()
                .query()
                .where("clan", this.name)
                .where("last_join", ">", System.currentTimeMillis() - Config.offlinePlayerThreshold)
                .completeAsCount();
    }

    public int countOfflinePlayers() {
        return NClans.getInstance()
                .getDatabase()
                .getPlayers()
                .query()
                .where("clan", this.name)
                .where("last_join", "<=", System.currentTimeMillis() - Config.offlinePlayerThreshold)
                .completeAsCount();
    }

    public int countPlayers() {
        return NClans.getInstance()
                .getDatabase()
                .getPlayers()
                .query()
                .where("clan", this.name)
                .completeAsCount();
    }

    public NClan() {

    }

    public NClan(Player p, String name) {
        this.name = name;
        this.owner = p.getName();
        this.displayName = "&r&7" + name;
        this.createdAt = System.currentTimeMillis();
        this.nextTaxesTake = System.currentTimeMillis() + Config.taxesTakePeriod;

        this.friendlyFire = true;
        this.joinAlarm = false;
        this.home = null;

        for (ClanAction action : ClanAction.values()) {
            permissions.put(action, ClanGroup.OWNER);
        }
    }

    public void processTaxes() {
        var total = ClanTax.calculate(this);
        if (total > this.balance) {
            delete();
            Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                Message.tax_delete_clan.get().replace("{clan}", Message.translateCodes(this.displayName)).broadcast();
                Util.broadcastRefresh();
            });

            return;
        }
        this.balance -= total;
        if (total > 0)
            Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                broadcast(Message.tax_taken.get().replace("{tax_price_total}", String.valueOf(total)).toString());
            });
        save();
    }

    public void delete() {
        NClans.getInstance().getDatabase().getPlayers().delete().where("clan", name).compete();
        NClans.getInstance().getDatabase().getClans().delete().where("name", name).compete();
    }

    public void broadcast(String msg) {
        NClans.getInstance().getForwarder().send(new BroadcastClanMsgMessagePacket(msg, this.name));
        Util.broadcastClanMessageLocal(this.name, msg);
    }

    public void save() {
        NClans.getInstance().getDatabase().getClans().update().replace(this);
    }

    public List<NClanPlayer> getFetchedPlayers() {
        return fetchedPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public boolean isJoinAlarm() {
        return joinAlarm;
    }

    public void setJoinAlarm(boolean joinAlarm) {
        this.joinAlarm = joinAlarm;
    }

    public Map<ClanAction, ClanGroup> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<ClanAction, ClanGroup> permissions) {
        this.permissions = permissions;
    }

    public String getHomeServer() {
        return homeServer;
    }

    public void setHomeServer(String homeServer) {
        this.homeServer = homeServer;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getNextTaxesTake() {
        return nextTaxesTake;
    }

    public void setNextTaxesTake(long nextTaxesTake) {
        this.nextTaxesTake = nextTaxesTake;
    }
}
