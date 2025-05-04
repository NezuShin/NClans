package su.nezushin.clans.messages.packets.impl;

import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;
import su.nezushin.clans.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BroadcastClanMsgMessagePacket extends MessagePacket {

    private String message;
    private String clan;

    public BroadcastClanMsgMessagePacket() {
    }

    public BroadcastClanMsgMessagePacket(String message, String clan) {
        this.message = message;
        this.clan = clan;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(clan);
        out.writeUTF(message);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        clan = in.readUTF();
        message = in.readUTF();
    }

    @Override
    public MessageType getType() {
        return MessageType.broadcast_message_clan;
    }

    @Override
    public void exec() {
        Util.broadcastClanMessageLocal(clan, message);
    }
}
