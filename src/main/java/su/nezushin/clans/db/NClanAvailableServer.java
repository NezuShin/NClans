package su.nezushin.clans.db;

import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import su.nezushin.clans.util.Config;
import su.nezushin.clans.util.YamlStringSerializer;

import java.util.List;
import java.util.UUID;

public class NClanAvailableServer implements AnvilORMSerializable {

    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String server;

    @SqlColumn(type = SqlType.VARCHAR, name = "online_players")
    private String onlinePlayersString;

    @SqlColumn(type = SqlType.BIGINT, name = "expire")
    private long expire;

    public NClanAvailableServer(String server, List<String> onlinePlayers) {
        this.server = server;
        this.expire = System.currentTimeMillis() + 4 * 1000;
        this.onlinePlayersString = YamlStringSerializer.serializeStringList(onlinePlayers);
    }

    public NClanAvailableServer() {
    }

    private List<String> onlinePlayers;

    @Override
    public void onDeserialize() {
        onlinePlayers = YamlStringSerializer.deserializStringList(onlinePlayersString);
    }

    public String getOnlinePlayersString() {
        return onlinePlayersString;
    }

    public void setOnlinePlayersString(String onlinePlayersString) {
        this.onlinePlayersString = onlinePlayersString;
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(List<String> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
