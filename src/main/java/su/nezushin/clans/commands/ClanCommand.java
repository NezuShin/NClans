package su.nezushin.clans.commands;

import java.util.*;
import java.util.regex.Pattern;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import su.nezushin.anvil.orm.table.query.OrderBy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nezushin.clans.NClans;
import su.nezushin.clans.db.NClan;
import su.nezushin.clans.db.NClanCooldown;
import su.nezushin.clans.db.NClanInvitation;
import su.nezushin.clans.db.NClanPlayer;
import su.nezushin.clans.enums.ClanAction;
import su.nezushin.clans.enums.ClanGroup;
import su.nezushin.clans.enums.ClanTax;
import su.nezushin.clans.events.ClanHomeDeleteEvent;
import su.nezushin.clans.messages.packets.impl.DeleteHomeMessagePacket;
import su.nezushin.clans.msg.Message;
import su.nezushin.clans.util.Config;
import su.nezushin.clans.util.Util;
import su.nezushin.clans.util.YamlStringSerializer;

public class ClanCommand implements CommandExecutor, TabCompleter {

    Set<String> errInClan = Sets.newHashSet("create", "accept");
    Set<String> errNotInClan = Sets.newHashSet("leave", "delete", "invite", "displayname", "role", "owner",
            "home", "friendlyfire", "deposit", "taxes", "money", "balance", "bal", "permissions", "delhome");
    Pattern pattern = Pattern.compile("[^a-zA-Z&^\\w]");

    public ClanCommand(PluginCommand cmd) {
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("nclans.admin")) {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("reload")) {
                    NClans.getInstance().unload();
                    NClans.getInstance().load();
                    sender.sendMessage("Reloaded!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("refresh")) {
                    NClans.getInstance().getCache().refreshCache();
                    sender.sendMessage("Cache refreshed!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("online-players")) {
                    sender.sendMessage(String.join(", ", NClans.getInstance().getCache().getOnlinePlayers()));
                    return true;
                }

            }
            return true;
        }

        if (!(sender instanceof Player p)) return true;

        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {

            NClanPlayer player = NClans.getInstance().getDatabase().getPlayers().query()
                    .where("id", p.getUniqueId().toString()).completeAsOne();
            NClan clan = player != null ? player.fetchClan() : null;
            boolean inClan = clan != null;
            boolean hasCooldown = Config.clanLeaveCooldown > 0 && !inClan && NClans.getInstance().getDatabase().getCooldowns().query()
                    .where("id", p.getUniqueId().toString())
                    .where("expire", ">", System.currentTimeMillis())
                    .completeAsCount() > 0;

            if (args.length == 0) {
                Message.about.send(sender);
                return;
            }

            if (inClan) {
                if (errInClan.contains(args[0])) {
                    Message.err_in_clan.send(sender);
                    return;
                }
            } else {
                if (errNotInClan.contains(args[0])) {
                    Message.err_not_in_clan.send(sender);
                    return;
                }
            }

            if (args[0].equalsIgnoreCase("create") && args.length == 2) {
                String name = args[1];

                if (pattern.matcher(name).find()) {
                    Message.err_wrong_name.send(sender);
                    return;
                }

                if (NClans.getInstance().getDatabase().getClans().query().where("name", name).completeAsCount() > 0) {
                    Message.err_clan_already_exists.send(sender);
                    return;
                }

                if (hasCooldown) {
                    Message.wait_leave_cooldown.send(p);
                    return;
                }

                if (Config.useVault) {
                    var price = Config.clanCreatePrice;


                    if (NClans.getInstance().getEconomy().getBalance(p) < price) {
                        Message.u_dont_have_enough_money.send(p);
                        return;
                    }
                    NClans.getInstance().getEconomy().withdraw(p, price);
                }

                clan = new NClan(p, name);

                if (player == null) player = new NClanPlayer(p.getUniqueId().toString(), p.getName(), name, false);
                player.setClan(name);

                player.save();
                Util.refreshLastJoin(p);

                clan.save();
                Util.broadcastRefresh();

                Message.player_create_clan.get().replace("{clan}", name, "{player}", sender.getName()).broadcast();
                Util.broadcastRefresh();
                return;
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (!clan.getOwnerId().equals(p.getUniqueId().toString())) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                if (args.length == 1) {
                    Message.delete_r_u_shure.send(sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("confirm")) {
                    clan.fetchPlayers().forEach(NClanPlayer::delete);
                    clan.delete();
                    Message.player_delete_clan.replace("{player}", sender.getName(), "{clan}", clan.getName()).broadcast();
                    Util.broadcastRefresh();
                }
                return;
            } else if (args[0].equalsIgnoreCase("list")) {

            } else if (args[0].equalsIgnoreCase("invite") && args.length == 2) {
                if (!player.hasPermission(ClanAction.INVITE_MEMBERS)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                String target = args[1];

                if (NClans.getInstance().getCache().getOnlinePlayers().stream().noneMatch(i -> i.equalsIgnoreCase(target))) {
                    Message.player_not_found.replace("{player}", target).send(sender);
                    return;
                }

                if (NClans.getInstance().getDatabase().getCooldowns().query()
                        .where("player", target)
                        .where("expire", "<", System.currentTimeMillis())
                        .completeAsCount() > 0) {
                    Message.cannot_invite_cooldown.send(p);
                    return;
                }

                if (NClans.getInstance().getDatabase().getPlayers().query().where("player", target).completeAsCount() > 0) {
                    Message.player_already_in_clan.replace("{player}", target).send(sender);
                    return;
                }

                if (NClans.getInstance().getDatabase().getInvitations().query().where("player", target).where("inviter", p.getName()).where("expire", ">", System.currentTimeMillis()).completeAsCount() > 0) {
                    Message.u_already_send_invite.send(sender);
                    return;
                }


                NClans.getInstance().getDatabase().getInvitations().update().replace(new NClanInvitation(target, p.getName(), clan.getName()));


                //Message.invite.replace("{clan}", clan.getName()).send(t);
                Message.invite_sent.send(p);


                Message.invite.replace("{clan}", clan.getName(), "{inviter}", p.getName()).forward(target);//.send(t);
                return;
            } else if (args[0].equalsIgnoreCase("accept") && args.length == 2) {
                String target = args[1];

                var invitation = NClans.getInstance().getDatabase().getInvitations().query().where("player", p.getName()).where("clan", target).where("expire", ">", System.currentTimeMillis()).orderBy(OrderBy.DESC, "timestamp").completeAsOne();

                if (invitation == null) {
                    Message.invite_not_found.send(sender);
                    return;
                }

                player = new NClanPlayer(p.getUniqueId().toString(), p.getName(), target, false);

                clan = player.fetchClan();

                NClans.getInstance().getDatabase().getInvitations().delete().where("id", invitation.getId()).compete();

                NClans.getInstance().getDatabase().getPlayers().update().replace(player);
                Message.u_joined_clan.replace("{clan}", clan.getDisplayName()).send(p);
                clan.broadcast(Message.player_join_clan.replace("{player}", sender.getName()).toString());
                Util.broadcastRefresh();
            } else if (args[0].equalsIgnoreCase("displayname") && args.length == 2) {
                if (!player.hasPermission(ClanAction.CHANGE_DISPLAYNAME)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                if (!p.hasPermission("nclans.displayname.change")) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }

                String name = MiniMessage.miniMessage().stripTags(Message.hexColor(args[1]));


                if (!ChatColor.stripColor(name).equalsIgnoreCase(clan.getName())) {
                    Message.change_displayname_usage.send(p);
                    return;
                }


                clan.setDisplayName(name);
                clan.save();
                Message.displayname_changed.replace("{clan}", Message.translateCodes(name)).send(p);
                Util.broadcastRefresh();
            } else if (args[0].equalsIgnoreCase("role") && args.length >= 2) {
                String name = correctName(args[1], clan);
                var curators = clan.getFetchedPlayers().stream().filter(NClanPlayer::isCurator).toList();

                if (clan.getFetchedPlayers().stream().noneMatch(i -> i.getPlayer().equalsIgnoreCase(name))) {
                    Message.member_not_found.replace("{member}", name).send(p);
                    return;
                }

                if (args.length == 2) {
                    if (curators.stream().filter(i -> i.getPlayer().equalsIgnoreCase(name)).count() > 0) {
                        Message.player_is_curator.replace("{player}", name).send(sender);
                    } else {
                        Message.player_is_member.replace("{player}", name).send(sender);
                    }
                    return;
                }

                if (!player.hasPermission(ClanAction.PROMOTE_CURATORS)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }

                String role = args[2];

                if (!role.equalsIgnoreCase("member") && !role.equalsIgnoreCase("curator")) {
                    Message.unknown_role.send(sender);
                    return;
                }
                var curator = curators.stream().filter(i -> i.getPlayer().equalsIgnoreCase(name)).findFirst().orElse(null);
                if (role.equalsIgnoreCase("curator")) {
                    curator.setCurator(true);
                    curator.save();

                } else {
                    curator.setCurator(false);
                    curator.save();
                }
                Message.role_updated.replace("{player}", name).send(sender);

                return;
            } else if (args[0].equalsIgnoreCase("owner") && args.length >= 2) {
                if (!clan.getOwnerId().equals(p.getUniqueId().toString())) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                String name = correctName(args[1], clan);
                var members = clan.getFetchedPlayers();


                var member = members.stream().filter(i -> i.getPlayer().equalsIgnoreCase(name)).findFirst().orElse(null);

                if (member == null) {
                    Message.member_not_found.replace("{member}", name).send(p);
                    return;
                }

                if (args.length == 2) {
                    Message.owner_r_u_shure.replace("{owner}", args[1]).send(sender);
                    return;
                }

                if (args[2].equalsIgnoreCase("confirm")) {
                    clan.setOwnerName(name);
                    clan.setOwnerId(member.getId());
                    clan.save();
                    Message.owner_moved.send(sender);
                    Util.broadcastRefresh();
                    return;
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (clan.getOwnerId().equalsIgnoreCase(p.getUniqueId().toString())) {
                    Message.u_cannot_leave_ur_clan.send(p);
                    return;
                }
                player.delete();
                Message.u_left_clan.send(p);
                clan.broadcast(Message.player_left_clan.replace("{player}", sender.getName()).toString());
                NClans.getInstance().getDatabase().getCooldowns().update()
                        .replace(new NClanCooldown(p.getUniqueId().toString(), p.getName(),
                                System.currentTimeMillis() + Config.clanLeaveCooldown));
                Util.broadcastRefresh();
                return;
            } else if (args[0].equalsIgnoreCase("kick") && args.length == 2) {
                if (!player.hasPermission(ClanAction.KICK_MEMBERS)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }

                String target = correctName(args[1], clan);
                if (p.getName().equalsIgnoreCase(target)) {
                    Message.u_cannot_kick_urself.send(p);
                    return;
                }
                var members = clan.getFetchedPlayers();
                var pMember = members.stream().filter(i -> i.getPlayer().equalsIgnoreCase(target)).findFirst().orElse(null);
                if (pMember == null) {
                    Message.member_not_found.replace("{member}", target).send(p);
                    return;
                }
                pMember.delete();
                Message.u_kick_member.replace("{player}", target).send(sender);
                Player t;
                if ((t = Bukkit.getPlayerExact(target)) != null && t.isOnline()) {
                    Message.u_have_been_kicked.send(t);
                }
                clan.broadcast(Message.player_kicked_from_clan.replace("{player}", target).toString());
                Util.broadcastRefresh();
                return;
            } else if (args[0].equalsIgnoreCase("info")) {
                NClan target;
                if (args.length == 1) {
                    if (clan == null) {
                        Message.err_not_in_clan.send(sender);
                        return;
                    }
                    target = clan;
                } else {
                    target = NClans.getInstance().getDatabase().getClans().query().where("name", args[1]).completeAsOne();
                    if (target == null) {
                        Message.clan_not_found.send(p);
                        return;
                    }
                }
                var players = target.fetchPlayers();
                Message.clan_info.replace("{display-name}", Message.translateCodes(target.getDisplayName()),

                        "{name}", target.getName(), "{owner}", target.getOwnerName(), "{curators}",
                        stringListToString(players.stream().filter(NClanPlayer::isCurator).map(NClanPlayer::getPlayer).toList(),
                                Message.clan_info_curators_format), "{members}", stringListToString(players.stream().filter(i -> !i.isCurator()).map(NClanPlayer::getPlayer).toList(), Message.clan_info_curators_format)).send(p);

                return;
            } else if (args[0].equalsIgnoreCase("deposit") && Config.useVault && args.length == 2) {
                double amount;
                try {
                    amount = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    Message.err_incorrect_number.send(p);
                    return;
                }
                if (amount > NClans.getInstance().getEconomy().getBalance(p)) {
                    Message.u_dont_have_enough_money.send(p);
                    return;
                }
                NClans.getInstance().getEconomy().withdraw(p, amount);
                clan.setBalance(clan.getBalance() + amount);
                clan.save();
                Message.clan_deposit_success.replace("{balance}", String.valueOf(clan.getBalance())).send(p);
                return;
            } else if ((args[0].equalsIgnoreCase("money")
                    || args[0].equalsIgnoreCase("balance")
                    || args[0].equalsIgnoreCase("bal")) && Config.useVault) {
                Message.clan_balance.replace("{balance}", String.valueOf(clan.getBalance()), "{days_remaining}",
                        String.valueOf((int) Math.floor(clan.getBalance() / ClanTax.calculate(clan)))).send(p);
                return;
            } else if (args[0].equalsIgnoreCase("taxes") && Config.useVault) {
                Message.tax_show_format_prefix.send(p);
                var total = 0.0;
                for (var i : ClanTax.values()) {
                    var val = i.calc(clan);
                    total += val;
                    if (val > 0)
                        Message.tax_show_format.replace("{tax_display}", i.getTitle().get().toString(), "{tax_price}", String.valueOf(val)).send(p);
                }

                Message.tax_show_format_suffix.replace("{tax_price_total}", String.valueOf(total),
                        "{days_remaining}", String.valueOf((int) Math.floor(clan.getBalance() / total))).send(p);
                return;
            } else if (args[0].equalsIgnoreCase("home")) {

                if (clan.getHome() == null) {
                    Message.home_not_found.send(sender);
                    return;
                }
                if (!player.hasPermission(ClanAction.USE_HOME)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                var teleporter = NClans.getInstance().getTeleporter();

                if (teleporter.isTeleporting(p)) {
                    Message.teleport_cancelled.send(sender);
                    teleporter.clearDelay(p);
                    return;
                }
                teleporter.teleport(p, clan.getHomeServer(), clan.getHome());
                Message.teleport_scheduled.send(sender);
                return;
            } else if (args[0].equalsIgnoreCase("sethome") && Config.allowSetHome) {
                if (!player.hasPermission(ClanAction.SET_HOME)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                clan.setHome(p.getLocation());
                clan.setHomeServer(Config.server);
                clan.save();
                Util.broadcastRefresh();
                clan.broadcast(Message.home_set.toString());
            } else if (args[0].equalsIgnoreCase("delhome")) {
                if (!player.hasPermission(ClanAction.SET_HOME)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }
                if (clan.getHome() == null) {
                    Message.home_not_found.send(sender);
                    return;
                }
                if (clan.getHomeServer().equalsIgnoreCase(Config.server)) {
                    String clanName = clan.getName();
                    Location clanHome = clan.getHome();
                    String clanHomeServer = clan.getHomeServer();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), () -> {
                        Bukkit.getPluginManager().callEvent(new ClanHomeDeleteEvent(clanName, clanHome, clanHomeServer));
                    });
                } else {
                    NClans.getInstance().getForwarder().send(
                            new DeleteHomeMessagePacket(clan.getName(),
                                    clan.getHomeServer(),
                                    YamlStringSerializer.serializeConfigurationSerializable(clan.getHome())));
                }
                clan.setHome(null);
                clan.setHomeServer(null);
                clan.save();
                Util.broadcastRefresh();
                clan.broadcast(Message.home_deleted.get().toString());
                return;
            } else if (args[0].equalsIgnoreCase("permissions")) {
                if (!clan.getOwnerId().equals(p.getUniqueId().toString())) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }

                if (args.length > 2) {
                    clan.getPermissions().put(ClanAction.valueOf(args[1].toUpperCase()), ClanGroup.valueOf(args[2].toUpperCase()));
                    clan.save();
                }

                Message.permission_show_format_prefix.send(sender);

                for (var i : ClanAction.values()) {
                    Message.permission_show_format.replace("{permission}", i.name().toLowerCase(),
                            "{permission_display}", i.getTitle().get().toString(), "{group_display}",
                            clan.getPermissions().get(i).getTitle().get().toString()).send(sender);
                }

                Message.permission_show_format_suffix.send(sender);


                return;
            } else if (args[0].equalsIgnoreCase("friendlyfire")) {
                if (!player.hasPermission(ClanAction.CHANGE_FRIENDLY_FIRE)) {
                    Message.u_dont_have_permission.send(sender);
                    return;
                }

                if (args.length > 1) {
                    boolean friendlyfire = args[1].equalsIgnoreCase("true");

                    clan.setFriendlyFire(friendlyfire);
                    clan.save();
                    Util.broadcastRefresh();
                }

                if (clan.isFriendlyFire()) {
                    Message.friendlyfire_on.send(sender);
                    return;
                }
                Message.friendlyfire_off.send(sender);
                return;
            }
        });

        return true;
    }

    public String correctName(String s, NClan c) {
        if (c.getFetchedPlayers() == null || c.getFetchedPlayers().isEmpty()) c.fetchPlayers();

        return c.getFetchedPlayers().stream().map(NClanPlayer::getPlayer).filter(player -> player.equalsIgnoreCase(s)).findFirst().orElse(s);
    }

    public String correctNameCurators(String s, NClan c) {
        if (c.getFetchedPlayers() == null || c.getFetchedPlayers().isEmpty()) c.fetchPlayers();

        return c.getFetchedPlayers().stream().filter(a -> a.getPlayer().equalsIgnoreCase(s) && a.isCurator()).map(NClanPlayer::getPlayer).findFirst().orElse(s);
    }

    public String stringListToString(List<String> list, Message format) {
        if (list.isEmpty()) return Message.clan_info_format_list_empty.get().toString();
        var s = "{next}";
        var js = Message.clan_info_format_join_string.get().toString();
        for (int i = 0; i < list.size(); i++) {
            s = s.replace("{join-string}", js).replace("{next}", format.replace("{player}", list.get(i)).toString());
        }

        return s.replace("{join-string}", "").replace("{next}", "");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        var isAdmin = sender.hasPermission("nclans.admin");
        if (args.length == 1) {
            var arr = Lists.newArrayList("create", "delete", "invite", "accept", "role", "leave", "kick",
                    "info", "home", "owner", "permissions", "friendlyfire", "displayname", "delhome");
            if (isAdmin)
                arr.add("admin");
            if (Config.useVault)
                arr.addAll(Lists.newArrayList("taxes", "deposit", "money", "balance", "bal"));


            return arr.stream().filter(a -> StringUtil.startsWithIgnoreCase(a, args[0])).toList();
        }


        if (!(sender instanceof Player p)) {
            return Lists.newArrayList("reload", "refresh");
        }


        var clan = NClans.getInstance().getCache().getClanName(p);
        var hasClan = clan != null;
        var members = clan == null ? new ArrayList<String>() : NClans.getInstance().getCache().getClanMembers(clan);
        if (args.length == 2) {
            if (isAdmin && args[0].equalsIgnoreCase("admin")) {
                return Lists.newArrayList("refresh", "reload", "online-players").stream().filter(a -> StringUtil.startsWithIgnoreCase(a, args[1])).toList();
            }
            if (hasClan) {
                if (args[0].equalsIgnoreCase("invite")) {
                    return NClans.getInstance().getCache().getOnlinePlayers().stream().filter(a -> StringUtil.startsWithIgnoreCase(a, args[1])).toList();
                } else if (args[0].equalsIgnoreCase("friendlyfire")) {
                    return Lists.newArrayList("true", "false");
                } else if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("role") || args[0].equalsIgnoreCase("owner")) {
                    return members.stream().filter(a -> StringUtil.startsWithIgnoreCase(a, args[1])).toList();
                } else if (args[0].equalsIgnoreCase("permissions")) {
                    return Arrays.stream(ClanAction.values()).map(i -> i.name().toLowerCase()).toList();
                }
            } else if (args[0].equalsIgnoreCase("accept")) {
                return NClans.getInstance().getCache().getClans().stream().filter(a -> StringUtil.startsWithIgnoreCase(a, args[1])).toList();
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("role")) {
                return Lists.newArrayList("member", "curator");
            } else if (args[0].equalsIgnoreCase("permissions")) {
                return Arrays.stream(ClanGroup.values()).map(i -> i.name().toLowerCase()).toList();
            }
        }
        return List.of();
    }
}
