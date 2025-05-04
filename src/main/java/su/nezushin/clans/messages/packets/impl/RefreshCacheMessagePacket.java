package su.nezushin.clans.messages.packets.impl;

import su.nezushin.clans.NClans;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RefreshCacheMessagePacket extends MessagePacket {

    @Override
    public void serialize(DataOutputStream out) {

    }

    @Override
    public void deserialize(DataInputStream in) {

    }

    @Override
    public MessageType getType() {
        return MessageType.refresh_cache;
    }

    @Override
    public void exec() {
        NClans.getInstance().getCache().refreshCache();
    }
}
