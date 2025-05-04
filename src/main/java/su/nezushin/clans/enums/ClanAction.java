package su.nezushin.clans.enums;

import su.nezushin.clans.msg.Message;

public enum ClanAction {
    CHANGE_DISPLAYNAME(Message.clan_action_change_displayname),
    INVITE_MEMBERS(Message.clan_action_invite_members),
    KICK_MEMBERS(Message.clan_action_kick_members),
    PROMOTE_CURATORS(Message.clan_action_promote_curators),
    CHANGE_FRIENDLY_FIRE(Message.clan_action_change_friendly_fire),
    USE_HOME(Message.clan_action_use_home),
    SET_HOME(Message.clan_action_set_home);

    private Message title;

    ClanAction(Message title) {
        this.title = title;
    }

    public Message getTitle() {
        return title;
    }
}
