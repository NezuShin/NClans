package su.nezushin.clans.db;

import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import su.nezushin.clans.NClans;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;

public class NClanPlayer implements AnvilORMSerializable {

    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String id;
    @SqlColumn(type = SqlType.VARCHAR/*, flags = SqlFlag.UNIQUE*/)
    private String player;
    @SqlColumn(type = SqlType.VARCHAR, name = "clan")
    private String clan;
    @SqlColumn(type = SqlType.BOOLEAN, name = "is_curator")
    private boolean isCurator;
    @SqlColumn(type = SqlType.BIGINT, name = "last_join")
    private long lastJoin;


    private NClan fetchedClan;

    public NClanPlayer() {

    }

    public NClanPlayer(String id, String player, String clan, boolean isCurator) {
        this.id = id;
        this.player = player;
        this.clan = clan;
        this.isCurator = isCurator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public boolean isCurator() {
        return isCurator;
    }

    public void setCurator(boolean curator) {
        isCurator = curator;
    }

    public boolean hasClan() {
        return clan != null;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public NClan getFetchedClan() {
        return fetchedClan;
    }

    public void setFetchedClan(NClan fetchedClan) {
        this.fetchedClan = fetchedClan;
    }

    public NClan fetchClan() {
        if (!hasClan())
            return null;
        return fetchedClan = NClans.getInstance().getDatabase().getClans().query().where("name", clan).completeAsOne();
    }

    public boolean hasPermission(ClanAction action) {

        if (!hasClan()) return false;
        if (fetchedClan == null)
            fetchClan();


        if (fetchedClan == null) {
            NClans.getInstance().getLogger().warning("Checked permission on non existing clan: " + this.clan + "; player: " + player);
            return false;
        }

        boolean isOwner = fetchedClan.getOwner().equals(this.player);

        if (isOwner) return true;

        ClanGroup group = fetchedClan.getPermissions().get(action);

        if (group == ClanGroup.MEMBERS) return true;
        if (group == ClanGroup.CURATORS && isCurator) return true;
        return false;
    }

    public void save() {
        NClans.getInstance().getDatabase().getPlayers().update().replace(this);
    }

    public void delete() {
        NClans.getInstance().getDatabase().getPlayers().delete().where("player", player).compete();
    }
}
