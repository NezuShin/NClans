package su.nezushin.clans;

import su.nezushin.anvil.orm.AnvilORMFactory;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.AnvilORMTable;
import org.bukkit.configuration.file.FileConfiguration;
import su.nezushin.clans.db.*;
import su.nezushin.clans.util.Config;

public class Database {


    private AnvilORMTable<NClan> clans;
    private AnvilORMTable<NClanPlayer> players;
    private AnvilORMTable<NClanInvitation> invitations;
    private AnvilORMTable<NClanCooldown> cooldowns;
    private AnvilORMTable<NClanTeleport> teleports;
    private AnvilORMTable<NClanMessage> messages;
    private AnvilORMTable<NClanAvailableServer> availableServers;

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
        return AnvilORMFactory.factory().buildMysqlTable(clazz, name, config.getString("mysql.host"), config.getInt("mysql.port"),
                config.getString("mysql.database"), config.getString("mysql.user"), config.getString("mysql.passowrd"), config.getBoolean("mysql.ssl", true));
    }
}
