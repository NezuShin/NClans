package su.nezushin.clans.enums;

import su.nezushin.clans.msg.Message;

public enum ClanGroup {
    MEMBERS(Message.clan_group_members),
    CURATORS(Message.clan_group_curators),
    OWNER(Message.clan_group_owner);

    private Message title;

    ClanGroup(Message title) {
        this.title = title;
    }

    public Message getTitle() {
        return title;
    }
}
