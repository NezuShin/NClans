package su.nezushin.clans;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.nezushin.clans.util.Util;

public class NClanListener implements Listener {

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p))
            return;
        if (!(e.getEntity() instanceof Player p2))
            return;

        if (!NClans.getInstance().getCache().canDamage(p, p2))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player))
            return;
        if (!player.getScoreboardTags().contains("NCLANS_PREVENT_DAMAGE"))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!e.getPlayer().getScoreboardTags().contains("NCLANS_PREVENT_DAMAGE"))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        NClans.getInstance().getCache().refreshCache();
        NClans.getInstance().getTeleporter().postServerTeleport(e.getPlayer());
        Util.refreshLastJoin(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        NClans.getInstance().getCache().removeCached(e.getPlayer());
        NClans.getInstance().getTeleporter().clearDelay(e.getPlayer());
    }
}
