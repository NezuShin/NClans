package su.nezushin.clans.cache;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.nezushin.clans.NClans;
import su.nezushin.clans.cache.playerlist.DatabasePlayerList;
import su.nezushin.clans.cache.playerlist.PlayerList;
import su.nezushin.clans.cache.playerlist.ProxyPlayerList;
import su.nezushin.clans.db.NClanPlayer;
import su.nezushin.clans.util.Config;

import java.util.*;

public class PlayerCache {

    private Map<String, List<String>> clans = new HashMap<>();

    private Map<Player, CachedPlayer> cache = new HashMap<>();

    private final PlayerList playerList;

    public PlayerCache() {
        playerList = Config.useMysql ? new DatabasePlayerList() : new ProxyPlayerList();
    }

    public void close() {
        playerList.close();
    }

    public List<String> getClanMembers(String clan) {
        return clans.get(clan);
    }

    public String getClanName(Player p) {
        var cached = cache.get(p);

        if (cached == null) return null;
        return cached.getClanName();
    }

    public String getClanDisplayName(Player p) {
        var cached = cache.get(p);

        if (cached == null) return "";
        return cached.getClanDisplayname() + " ";
    }

    public boolean hasClan(Player p) {
        var cached = cache.get(p);

        return cached == null || cached.getClanName() != null;
    }

    public List<String> getClans() {
        return new ArrayList<>(clans.keySet());
    }

    public List<String> getOnlinePlayers() {
        return playerList.getPlayers();
    }

    public boolean canDamage(Player p1, Player p2) {
        var player1 = cache.get(p1);
        var player2 = cache.get(p2);

        if (player1 == null || player2 == null) return true;
        if (player1.getClanName() == null || player2.getClanName() == null) return true;
        if (!player1.getClanName().equalsIgnoreCase(player2.getClanName())) return true;
        return player1.isFriendlyFire();
    }

    public void removeCached(Player p) {
        cache.remove(p);
    }

    public void refreshCache() {
        var players = Bukkit.getOnlinePlayers();
        var cache = new HashMap<Player, CachedPlayer>();
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            for (var player : players) {
                var id = player.getUniqueId().toString();
                var cp = NClans.getInstance().getDatabase().getPlayers().query().where("id", id).completeAsOne();
                if (cp == null) continue;
                if (!cp.getPlayer().equalsIgnoreCase(player.getName())) {
                    if (NClans.getInstance().getDatabase().getPlayers().update().set("player", player.getName()).where("id", id).complete() > 0) {
                        NClans.getInstance().getDatabase().getCooldowns().update().set("player", player.getName()).where("id", id).complete();
                        NClans.getInstance().getLogger().info("Player changed name from " + cp.getPlayer() + " to " + player.getName() + "; UUID: " + id);
                    }
                    cp.setPlayer(player.getName());
                }
                var clan = cp.fetchClan();
                if (clan == null) continue;
                cache.put(player, new CachedPlayer(clan.getDisplayName(), clan.getName(), clan.isFriendlyFire(), clan.getHome()));
            }
            Map<String, List<String>> clans = new HashMap<>();//new ArrayList<>(NClans.getInstance().getDatabase().getClans().query().completeAsList().stream().map(NClan::getName).toList());

            NClans.getInstance().getDatabase().getClans().query().completeAsList().forEach(a -> {
                clans.put(a.getName(), a.fetchPlayers().stream().map(NClanPlayer::getPlayer).toList());
            });

            Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                this.cache = cache;
                this.clans = clans;
            });
        });
    }

}
