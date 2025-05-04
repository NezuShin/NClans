package su.nezushin.clans.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nezushin.clans.NClans;
import su.nezushin.clans.msg.Message;

public class NClansPlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "nclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NezuShin";
    }

    @Override
    public @NotNull String getVersion() {
        return "";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        if (player instanceof Player p) {
            var clanName = NClans.getInstance().getCache().getClanDisplayName(p);
            if (params.equalsIgnoreCase("clan_displayname")) {
                if (clanName == null || clanName.isEmpty())
                    return "";
                return " " + clanName;
            } else if (params.equalsIgnoreCase("clan_displayname_minimessage")) {
                if (clanName == null || clanName.isEmpty())
                    return "";

                return " " + Message.translateCodes(clanName);
            }
        }

        return null;
    }
}
