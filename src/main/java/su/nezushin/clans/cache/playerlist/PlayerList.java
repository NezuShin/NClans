package su.nezushin.clans.cache.playerlist;

import java.util.List;

public interface PlayerList {

    public List<String> getPlayers();

    public void close();
}
