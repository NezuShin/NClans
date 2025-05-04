package su.nezushin.clans.db;

import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;

import java.util.UUID;

public class NClanInvitation implements AnvilORMSerializable {

    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String id;

    @SqlColumn(type = SqlType.VARCHAR)
    private String player;

    @SqlColumn(type = SqlType.VARCHAR)
    private String inviter;

    @SqlColumn(type = SqlType.VARCHAR)
    private String clan;

    @SqlColumn(type = SqlType.BIGINT)
    private long timestamp;

    @SqlColumn(type = SqlType.BIGINT)
    private long expire;

    public NClanInvitation() {
    }

    public NClanInvitation(String player, String inviter, String clan) {
        this.id = UUID.randomUUID().toString();
        this.player = player;
        this.inviter = inviter;
        this.clan = clan;
        this.expire = System.currentTimeMillis() + (10 * 60 * 1000);
        this.timestamp = System.currentTimeMillis();
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }
}
