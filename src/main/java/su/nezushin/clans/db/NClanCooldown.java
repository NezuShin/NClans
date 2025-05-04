package su.nezushin.clans.db;

import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import su.nezushin.clans.NClans;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;

public class NClanCooldown implements AnvilORMSerializable {

    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String player;
    @SqlColumn(type = SqlType.BIGINT, name = "expire")
    private long expire;

    public NClanCooldown() {

    }

    public NClanCooldown(String player, long expire) {
        this.player = player;
        this.expire = expire;
    }


    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
