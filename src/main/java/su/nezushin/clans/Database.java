package su.nezushin.clans;

import su.nezushin.anvil.orm.AnvilORMFactory;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.AnvilORMTable;
import org.bukkit.configuration.file.FileConfiguration;
import su.nezushin.clans.db.*;
import su.nezushin.clans.util.Config;

import java.io.File;

public class Database {


    private final AnvilORMTable<NClan> clans;
    private final AnvilORMTable<NClanPlayer> players;
    private final AnvilORMTable<NClanInvitation> invitations;
    private final AnvilORMTable<NClanCooldown> cooldowns;
    private final AnvilORMTable<NClanTeleport> teleports;
    private final AnvilORMTable<NClanMessage> messages;
    private final AnvilORMTable<NClanAvailableServer> availableServers;

    public Database() {
        clans = createTable(Config.config, NClan.class, "nclans_clans");
        players = createTable(Config.config, NClanPlayer.class, "nclans_players");
        invitations = createTable(Config.config, NClanInvitation.class, "nclans_invitations");
        teleports = createTable(Config.config, NClanTeleport.class, "nclans_teleports");
        messages = createTable(Config.config, NClanMessage.class, "nclans_messages");
        availableServers = createTable(Config.config, NClanAvailableServer.class, "nclans_available_servers");
        cooldowns = createTable(Config.config, NClanCooldown.class, "nclans_cooldowns");
    }

    public AnvilORMTable<NClanPlayer> getPlayers() {
        return players;
    }

    public AnvilORMTable<NClan> getClans() {
        return clans;
    }

    public AnvilORMTable<NClanInvitation> getInvitations() {
        return invitations;
    }

    public AnvilORMTable<NClanTeleport> getTeleports() {
        return teleports;
    }

    public AnvilORMTable<NClanMessage> getMessages() {
        return messages;
    }

    public AnvilORMTable<NClanAvailableServer> getAvailableServers() {
        return availableServers;
    }

    public AnvilORMTable<NClanCooldown> getCooldowns() {
        return cooldowns;
    }

    public <T extends AnvilORMSerializable> AnvilORMTable<T> createTable(FileConfiguration config, Class<T> clazz, String name) {
        if (Config.useMysql)
            return AnvilORMFactory.factory().buildMysqlTable(clazz, name, config.getString("database.mysql.host"),
                    config.getInt("database.mysql.port"), config.getString("database.mysql.database"),
                    config.getString("database.mysql.user"), config.getString("database.mysql.password"),
                    config.getBoolean("database.mysql.ssl", true));
        return AnvilORMFactory.factory().buildSqliteTable(clazz, name, new File(NClans.getInstance().getDataFolder() + File.separator + "database.db"));
    }
}
