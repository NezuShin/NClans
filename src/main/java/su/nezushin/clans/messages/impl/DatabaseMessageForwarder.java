package su.nezushin.clans.messages.impl;

import org.bukkit.Bukkit;
import su.nezushin.clans.NClans;
import su.nezushin.clans.db.NClanAvailableServer;
import su.nezushin.clans.db.NClanMessage;
import su.nezushin.clans.messages.MessageForwarder;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseMessageForwarder implements Runnable, MessageForwarder {

    int id;

    public DatabaseMessageForwarder() {
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(NClans.getInstance(), this, 40, 40).getTaskId();
    }

    @Override
    public void run() {
        for (var i : NClans.getInstance().getDatabase().getMessages().query().where("target_server", "=", Config.server).completeAsList()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(NClans.getInstance(), i::exec);
            NClans.getInstance().getDatabase().getMessages().delete().where("id", i.getId()).compete();
        }
        NClans.getInstance().getDatabase().getAvailableServers().update().replace(new NClanAvailableServer());
    }

    @Override
    public void send(String id, MessagePacket packet) {
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(packet.getType().name());
                msgout.writeUTF(id);
                packet.serialize(msgout);
                byte[] arr = msgbytes.toByteArray();

                var messages = new ArrayList<NClanMessage>();
                for (var i : NClans.getInstance().getDatabase().getAvailableServers().query().where("server", "!=", Config.server).completeAsList()) {
                    messages.add(new NClanMessage(i.getServer(), arr));
                }
                NClans.getInstance().getDatabase().getMessages().update().replace(messages);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }

    @Override
    public void close() {
        Bukkit.getScheduler().cancelTask(id);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
