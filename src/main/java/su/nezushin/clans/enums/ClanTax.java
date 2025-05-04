package su.nezushin.clans.enums;

import su.nezushin.clans.db.NClan;
import su.nezushin.clans.msg.Message;
import su.nezushin.clans.util.Config;

public enum ClanTax {

    DEFAULT(Message.clan_tax_default, (clan) -> {
        return Config.clanDefaultTax;
    }), ONLINE_PLAYERS(Message.clan_tax_online_players, (clan) -> {
        return Config.onlinePlayerTax * clan.countOnlinePlayers();
    }), OFFLINE_PLAYERS(Message.clan_tax_offline_players, (clan) -> {
        return Config.offlinePlayerTax * clan.countOfflinePlayers();
    }), ADDITIONAL_MIN_PLAYERS(Message.clan_tax_additional_min_players, (clan) -> {
        return clan.countPlayers() <= Config.clanMinPlayersThreshold ? Config.minPlayersAdditionalTax : 0;
    }), ADDITIONAL_MAX_PLAYERS(Message.clan_tax_additional_max_players, (clan) -> {
        var players = clan.countPlayers();
        return players >= Config.clanMaxPlayersThreshold ? Config.maxPlayersAdditionalTax * (players - Config.clanMaxPlayersThreshold) : 0;
    });

    private Message title;
    private ClanTaxCalculator calculator;

    ClanTax(Message title, ClanTaxCalculator calculator) {
        this.title = title;
        this.calculator = calculator;
    }

    public Message getTitle() {
        return title;
    }

    public double calc(NClan clan) {
        return this.calculator.calc(clan);
    }

    public static double calculate(NClan clan) {
        double tax = 0;
        for (var i : ClanTax.values()) {
            tax += i.calc(clan);
        }
        return tax;
    }

    public static interface ClanTaxCalculator {
        public double calc(NClan clan);
    }
}
