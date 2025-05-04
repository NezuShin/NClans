package su.nezushin.clans.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import su.nezushin.clans.NClans;
import su.nezushin.clans.msg.Message;

import java.io.File;

public class Config {

    public static FileConfiguration config;

    public static String server;

    public static double clanCreatePrice = 15000,
            clanDefaultTax = 500,
            onlinePlayerTax = 100,
            offlinePlayerTax = 1500,
            minPlayersAdditionalTax = 3000,
            maxPlayersAdditionalTax = 10000;

    public static int teleportDelay = 5 * 20, clanMaxPlayersThreshold = 15, clanMinPlayersThreshold = 1;

    public static long offlinePlayerThreshold = 2 * 24 * 60 * 60 * 1000, taxesTakePeriod = 24 * 60 * 60 * 1000, clanLeaveCooldown = 24 * 60 * 60 * 1000;

    public static boolean allowSetHome = true, useVault = true;

    public static void init() {
        var plugin = NClans.getInstance();
        if (!new File(plugin.getDataFolder() + File.separator + "config.yml").exists()) {
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveDefaultConfig();
        }


        config = plugin.getConfig();

        useVault = config.getBoolean("vault.use-vault", true);
        clanCreatePrice = config.getDouble("vault.create-clan-price", 0.0);
        clanDefaultTax = config.getDouble("vault.taxes.default", 0.0);
        onlinePlayerTax = config.getDouble("vault.taxes.online-player", 0.0);
        offlinePlayerTax = config.getDouble("vault.taxes.offline-player", 0.0);
        minPlayersAdditionalTax = config.getDouble("vault.taxes.min-players-additional", 0.0);
        maxPlayersAdditionalTax = config.getDouble("vault.taxes.max-players-additional", 0.0);

        taxesTakePeriod = config.getLong("taxes-take-period", 24 * 60 * 60 * 1000);

        offlinePlayerThreshold = config.getLong("limitations.offline-player-threshold");
        clanMaxPlayersThreshold = config.getInt("limitations.max-clan-members", 15);
        clanMinPlayersThreshold = config.getInt("limitations.min-clan-members", 1);
        clanLeaveCooldown = config.getLong("limitations.leave-cooldown", 24 * 60 * 60 * 1000);

        teleportDelay = config.getInt("home.teleport-delay");
        allowSetHome = config.getBoolean("home.allow-set");


        var messages = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        if (!messages.exists()) {
            plugin.saveResource("messages.yml", true);
        }
        Message.load(YamlConfiguration.loadConfiguration(messages));


        var serverFile = new File(plugin.getDataFolder() + File.separator + "server.yml");
        if (!serverFile.exists()) {
            plugin.saveResource("server.yml", true);
        }
        server = YamlConfiguration.loadConfiguration(serverFile).getString("server", "server");
    }
}
