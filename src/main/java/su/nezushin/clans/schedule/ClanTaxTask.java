package su.nezushin.clans.schedule;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import su.nezushin.clans.NClans;

import java.util.Random;

public class ClanTaxTask extends BukkitRunnable {


    public ClanTaxTask() {
        runTaskTimerAsynchronously(NClans.getInstance(), new Random().nextInt(100) + (60 * 20), 5 * 60 * 20);
    }

    @Override
    public void run() {
        for (var clan : NClans.getInstance().getDatabase().getClans().query()
                .where("next_taxes_take", "<", System.currentTimeMillis())
                .completeAsList()) {
            clan.processTaxes();
        }
    }
}
