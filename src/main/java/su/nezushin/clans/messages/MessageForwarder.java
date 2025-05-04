package su.nezushin.clans.messages;

import su.nezushin.clans.messages.packets.MessagePacket;

public interface MessageForwarder {

    public void send(String id, MessagePacket packet);

    public void close();

    public boolean isAvailable();

}
