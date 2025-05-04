package su.nezushin.clans;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import su.nezushin.clans.db.NClanInvitation;
import su.nezushin.clans.db.NClanTeleport;
import su.nezushin.clans.msg.Message;
import su.nezushin.clans.util.Config;
import su.nezushin.clans.util.Util;

import java.util.HashMap;
import java.util.Map;

public class Teleporter {

    private final Map<Player, Integer> teleportDelay = new HashMap<>();

    public void clearDelay(Player p) {
        if (!teleportDelay.containsKey(p))
            return;
        Bukkit.getScheduler().cancelTask(teleportDelay.remove(p));
    }

    public void close() {
        teleportDelay.forEach((a, b) -> {
            Bukkit.getScheduler().cancelTask(b);
        });

    }

    public boolean isTeleporting(Player player) {
        return teleportDelay.containsKey(player);
    }

    public void teleport(Player player, String server, Location location) {
        int id;
        if (Config.server.equalsIgnoreCase(server)) {
            id = Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                Message.teleporting.send(player);
                player.teleportAsync(location).thenRun(() -> {
                    player.playSound(player.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);
                    teleportDelay.remove(player);
                });
            }, Config.teleportDelay);
        } else {
            id = Bukkit.getScheduler().runTaskLaterAsynchronously(NClans.getInstance(), () -> {
                Message.teleporting.send(player);
                NClans.getInstance().getDatabase().getTeleports().update().replace(new NClanTeleport(player.getName(), location));
                Util.teleportToServer(player, server);
                teleportDelay.remove(player);
            }, Config.teleportDelay).getTaskId();
        }
        teleportDelay.put(player, id);
    }

    public void postServerTeleport(Player player) {
        player.addScoreboardTag("NCLANS_PREVENT_DAMAGE");
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            NClanTeleport teleport = NClans.getInstance().getDatabase().getTeleports().query()
                    .where("player", player.getName())
                    .where("expire", ">", System.currentTimeMillis()).completeAsOne();
            NClans.getInstance().getDatabase().getTeleports().delete().where("player", player.getName()).compete();
            if (teleport != null) {
                //Can i do it async ?...
                player.teleportAsync(teleport.getLocation()).thenRun(() -> {
                    player.playSound(player.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);
                    player.removeScoreboardTag("NCLANS_PREVENT_DAMAGE");
                });
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                    player.removeScoreboardTag("NCLANS_PREVENT_DAMAGE");
                });
            }
        });
    }
}
