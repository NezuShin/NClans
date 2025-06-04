package su.nezushin.clans.cache.playerlist;

import org.bukkit.Bukkit;
import su.nezushin.clans.NClans;

import java.util.ArrayList;
import java.util.List;

public class DatabasePlayerList implements PlayerList {

    private int taskId;

    private List<String> players = new ArrayList<>();

    public DatabasePlayerList() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(NClans.getInstance(), this::queryPlayerList, 5 * 20, 5 * 20).getTaskId();
    }

    private void queryPlayerList() {
        List<String> players = new ArrayList<>();
        NClans.getInstance().getDatabase()
                .getAvailableServers()
                .query()
                .where("expire", ">", System.currentTimeMillis())
                .completeAsList().forEach(i -> {
                    players.addAll(i.getOnlinePlayers());
                });
        Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
            this.players = players;
        });
    }

    @Override
    public List<String> getPlayers() {
        return players;
    }

    @Override
    public void close() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
