package su.nezushin.clans.messages;

import org.bukkit.Bukkit;
import su.nezushin.clans.NClans;
import su.nezushin.clans.messages.impl.BungeecordMessageForwarder;
import su.nezushin.clans.messages.impl.DatabaseMessageForwarder;
import su.nezushin.clans.messages.packets.*;
import su.nezushin.clans.messages.packets.impl.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Forwarders {

    Map<MessageType, Class<? extends MessagePacket>> map = new HashMap<>();

    Map<String, Long> receivedMessages = new HashMap<>();

    private final List<MessageForwarder> forwarders = new ArrayList<>();

    int cleanupReceivedTaskId;

    public Forwarders() {
        map.put(MessageType.refresh_cache, RefreshCacheMessagePacket.class);
        map.put(MessageType.broadcast_message, BroadcastMsgMessagePacket.class);
        map.put(MessageType.broadcast_message_clan, BroadcastClanMsgMessagePacket.class);
        map.put(MessageType.send_message, SendMsgMessagePacket.class);
        map.put(MessageType.delete_home, DeleteHomeMessagePacket.class);
        map.put(MessageType.delete_clan, DeleteClanMessagePacket.class);

        forwarders.add(new BungeecordMessageForwarder());
        forwarders.add(new DatabaseMessageForwarder());

        cleanupReceivedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(NClans.getInstance(), () -> {
            new HashSet<>(receivedMessages.entrySet()).forEach(i -> {
                if (i.getValue() < System.currentTimeMillis())
                    receivedMessages.remove(i.getKey());
            });
        }, 20, 20);
    }

    public void close() {
        forwarders.forEach(MessageForwarder::close);
    }

    public void send(MessagePacket packet) {
        String id = UUID.randomUUID().toString();
        for (var forwarder : forwarders) {
            if (forwarder.isAvailable()) {
                forwarder.send(id, packet);
            }
        }
    }


    public void decodeMessage(DataInputStream msgin) {
        try {
            var type = MessageType.valueOf(msgin.readUTF());
            var id = msgin.readUTF();

            if (receivedMessages.containsKey(id)) return;
            MessagePacket msg = map.get(type).getDeclaredConstructor().newInstance();
            msg.deserialize(msgin);

            receivedMessages.put(id, System.currentTimeMillis() + 5000);

            msg.exec();
        } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
