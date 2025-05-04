package su.nezushin.clans.util;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.nezushin.clans.NClans;
import su.nezushin.clans.messages.packets.impl.BroadcastMsgMessagePacket;
import su.nezushin.clans.messages.packets.impl.RefreshCacheMessagePacket;
import su.nezushin.clans.messages.packets.impl.SendMsgMessagePacket;

public class Util {

    public static void forwardMessage(String player, String s) {
        NClans.getInstance().getForwarder().send(new SendMsgMessagePacket(player, s));
    }

    public static void broadcastMessage(String s) {
        NClans.getInstance().getForwarder().send(new BroadcastMsgMessagePacket(s));
        broadcastMessageLocal(s);
    }

    public static void broadcastRefresh() {
        NClans.getInstance().getCache().refreshCache();
        NClans.getInstance().getForwarder().send(new RefreshCacheMessagePacket());
    }

    public static void broadcastMessageLocal(String s) {
        for (var i : s.split("\n")) {
            var component = MiniMessage.miniMessage().deserialize(i);
            for (var p : Bukkit.getOnlinePlayers())
                p.sendMessage(component);
        }
    }

    public static void broadcastClanMessageLocal(String clan, String message) {
        for (var p : Bukkit.getOnlinePlayers()) {
            var pclan = NClans.getInstance().getCache().getClanName(p);
            if (pclan != null && pclan.equalsIgnoreCase(clan))
                for (var i : message.split("\n"))
                    p.sendMessage(MiniMessage.miniMessage().deserialize(i));
        }
    }

    public static void teleportToServer(Player player, String server) {
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            var out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(NClans.getInstance(), "BungeeCord", out.toByteArray());
        });
    }

    public static void refreshLastJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            NClans.getInstance().getDatabase().getPlayers().update()
                    .set("last_join", System.currentTimeMillis())
                    .where("player", player.getName())
                    .complete();
        });
    }
}
