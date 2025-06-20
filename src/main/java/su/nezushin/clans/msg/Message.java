package su.nezushin.clans.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import su.nezushin.clans.NClans;
import su.nezushin.clans.util.Util;

public enum Message {

    err_in_clan, err_not_in_clan, err_clan_already_exists, err_wrong_name, delete_r_u_shure, owner_r_u_shure,
    u_dont_have_permission, player_create_clan, member_not_found, unknown_role, player_is_member, player_is_curator,
    owner_moved, role_updated, player_delete_clan, player_not_found, player_already_in_clan, invite, invite_sent,
    invite_not_found, some_shit_happened, u_joined_clan, clan_info, clan_info_format_list_empty,
    clan_info_format_join_string, clan_info_curators_format, clan_info_members_format, u_cannot_leave_ur_clan,
    u_left_clan, u_kick_member, u_cannot_kick_urself, u_have_been_kicked, clan_not_found, player_join_clan,
    player_left_clan, home_not_found, home_set, home_deleted, teleporting, teleport_cancelled, teleport_scheduled, u_already_send_invite,
    friendlyfire_on, friendlyfire_off, about, player_kicked_from_clan, clan_group_members, clan_group_curators,
    clan_group_owner, clan_action_change_displayname, clan_action_invite_members, clan_action_kick_members,
    clan_action_promote_curators, clan_action_change_friendly_fire, clan_action_use_home, clan_action_set_home,
    change_displayname_usage, displayname_changed, permission_show_format_prefix, permission_show_format_suffix,
    permission_show_format, u_dont_have_enough_money, clan_tax_default, clan_tax_online_players,
    clan_tax_offline_players, clan_tax_additional_max_players, clan_tax_additional_min_players, tax_show_format_prefix,
    tax_show_format_suffix, tax_show_format, err_incorrect_number, clan_balance, clan_deposit_success,
    tax_delete_clan, tax_taken, wait_leave_cooldown, cannot_invite_cooldown;

    private List<String> list = Lists.newArrayList("");


    public static void load(FileConfiguration conf) {
        for (Message msg : values()) {
            Object obj = conf.get("messages." + msg.name().toLowerCase().replace("_", "-"));

            if (obj instanceof List) {
                msg.list = (List<String>) obj;

            } else
                msg.list = Lists.newArrayList((String) obj);

            {
                List<String> list = msg.list;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null)
                        list.set(i, applyColor(list.get(i)));
                    else {
                        // DiscordAuth.warning("String for " + msg.toString() + " is null");
                        NClans.getInstance().getLogger().warning("String for " + msg.toString() + " is null");
                    }
                }

                msg.list = list;

            }
        }
    }

    public void send(CommandSender sender) {
        new ChatMessageSender(this).send(sender);
    }

    public void kick(Player player) {
        new ChatMessageSender(this).kick(player);
    }

    public ChatMessageSender replace(String... strings) {
        return new ChatMessageSender(this).replace(strings);
    }

    public ChatMessageSender get() {
        return new ChatMessageSender(this);
    }

    public static class ChatMessageSender {

        List<String> msg;

        public ChatMessageSender(Message form) {
            this.msg = new ArrayList<String>(form.list);
        }

        public ChatMessageSender replace(String... strs) {
            String replace = null;
            for (String s : strs) {
                if (replace == null) {
                    replace = s;
                    continue;
                }
                for (int i = 0; i < msg.size(); i++) {
                    msg.set(i, msg.get(i).replace(replace, s));
                }

                replace = null;
            }
            return this;
        }

        public ChatMessageSender kick(Player p) {
            if (msg.isEmpty()) {
                p.kick(Component.text(""));
            } else {
                p.kick(MiniMessage.miniMessage().deserialize(toString()));
            }
            return this;
        }

        @Override
        public String toString() {

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < msg.size(); i++) {
                if (i != 0)
                    builder.append("\n");
                builder.append(msg.get(i));
            }

            return builder.toString();

        }

        public ChatMessageSender broadcast() {
            try {
                Util.broadcastMessage(String.join("\n", msg));
            } catch (Exception e) {
                // DiscordAuth.severe("While sending message occured exception", e);
            }
            return this;
        }

        public ChatMessageSender forward(String player) {
            try {
                Player p;
                if ((p = Bukkit.getPlayerExact(player)) != null) {
                    send(p);
                    return this;
                }

                Util.forwardMessage(player, String.join("\n", msg));
            } catch (Exception e) {
                // DiscordAuth.severe("While sending message occured exception", e);
            }
            return this;
        }


        public ChatMessageSender send(CommandSender p) {
            try {
                for (String string : msg) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize(string));
                }
            } catch (Exception e) {
                // DiscordAuth.severe("While sending message occured exception", e);
            }
            return this;
        }

    }

    public static String translateCodes(String s) {
        Pattern pattern = Pattern.compile("(§x(§[a-fA-f0-9]){6})");
        Matcher matcher = pattern.matcher("" + s);
        while (matcher.find()) {
            String color = s.substring(matcher.start(), matcher.end());
            s = s.replace(color, "<#" + color.replace("§", "").substring(1) + ">");
        }
        for (var i : ChatColor.values()) {
            for (var code : new char[]{ChatColor.COLOR_CHAR, '&'}) {
                String color = code + "" + i.getChar();
                s = s.replace(color, "<" + i.name().toLowerCase() + ">");
            }
        }
        return s;
    }

    public static String hexColor(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String stripColor(String s) {
        var STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(ChatColor.COLOR_CHAR) + "[0-9A-FK-ORX]");
        if (s == null) {
            return null;
        }
        return STRIP_COLOR_PATTERN.matcher(s).replaceAll("");
    }

    public static String applyColor(String message) {
        return translateCodes(message);
    }

}
