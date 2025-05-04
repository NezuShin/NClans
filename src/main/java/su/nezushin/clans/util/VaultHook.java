package su.nezushin.clans.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nezushin.clans.NClans;

public class VaultHook {

    private Economy eco;

    public VaultHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {

            if (Config.useVault) {

                NClans.getInstance().getLogger().warning("use-vault is enabled, but vault is not found");
            }
            return;
        }
        eco = rsp.getProvider();
    }

    public double getBalance(Player player) {
        if (eco == null) {
            NClans.getInstance().getLogger().warning("use-vault is enabled, but vault is not found");
            return 0.0;
        }
        return eco.getBalance(player);
    }

    public void withdraw(Player player, double moneyToTake) {
        if (eco == null) {
            NClans.getInstance().getLogger().warning("use-vault is enabled, but vault is not found");
            return;
        }
        eco.withdrawPlayer(player, moneyToTake);
    }


}
