package su.nezushin.clans;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import su.nezushin.clans.cache.PlayerCache;
import su.nezushin.clans.commands.ClanCommand;
import su.nezushin.clans.messages.Forwarders;
import su.nezushin.clans.schedule.ClanTaxTask;
import su.nezushin.clans.util.Config;
import su.nezushin.clans.util.NClansPlaceholder;
import su.nezushin.clans.util.VaultHook;

public final class NClans extends JavaPlugin {

    private static NClans instance;

    private Database database;
    private PlayerCache cache;
    private Forwarders forwarder;
    private Teleporter teleporter;
    private VaultHook economy;
    private ClanTaxTask taxes;
    private NClansPlaceholder placeholder;

    @Override
    public void onEnable() {
        instance = this;
        new ClanCommand(getCommand("clan"));
        load();
    }

    public void load() {
        Config.init();
        Bukkit.getPluginManager().registerEvents(new NClanListener(), instance);
        database = new Database();
        cache = new PlayerCache();
        forwarder = new Forwarders();
        teleporter = new Teleporter();
        economy = new VaultHook();
        taxes = new ClanTaxTask();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholder = new NClansPlaceholder();
            placeholder.register();
        }
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        forwarder.close();
        cache.close();
        teleporter.close();
        taxes.cancel();
        if (placeholder != null)
            placeholder.unregister();
    }

    @Override
    public void onDisable() {
    }

    public static NClans getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public PlayerCache getCache() {
        return cache;
    }

    public Teleporter getTeleporter() {
        return teleporter;
    }

    public Forwarders getForwarder() {
        return forwarder;
    }

    public VaultHook getEconomy() {
        return economy;
    }
}
