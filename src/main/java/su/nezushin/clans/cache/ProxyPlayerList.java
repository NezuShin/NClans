package su.nezushin.clans.cache;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import su.nezushin.clans.NClans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyPlayerList implements PluginMessageListener {

    private List<String> players = new ArrayList<>();

    public ProxyPlayerList() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(NClans.getInstance(), "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(NClans.getInstance(), "BungeeCord", this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(NClans.getInstance(), this::queryPlayerList, 5 * 20, 5 * 20);
    }

    public void close() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(NClans.getInstance(), "BungeeCord", this);
    }

    private void sendMessage(String... message) {
        var players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            if (players.isEmpty()) return;
            var player = players.getFirst();
            var out = ByteStreams.newDataOutput();
            for (var i : message) {
                out.writeUTF(i);
            }
            player.sendPluginMessage(NClans.getInstance(), "BungeeCord", out.toByteArray());
        });
    }

    public List<String> getPlayers() {
        return players;
    }

    public void queryPlayerList() {
        sendMessage("PlayerList", "ALL");
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord"))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (in.readUTF().equalsIgnoreCase("PlayerList") && in.readUTF().equalsIgnoreCase("ALL")) {
            this.players = Arrays.stream(in.readUTF().split(",")).map(String::trim).toList();
        }
    }
}
