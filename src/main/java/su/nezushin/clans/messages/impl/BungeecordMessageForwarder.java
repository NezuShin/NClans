package su.nezushin.clans.messages.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import su.nezushin.clans.NClans;
import su.nezushin.clans.messages.MessageForwarder;
import su.nezushin.clans.messages.packets.MessagePacket;

import java.io.*;
import java.util.ArrayList;

public class BungeecordMessageForwarder implements PluginMessageListener, MessageForwarder {


    public BungeecordMessageForwarder() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(NClans.getInstance(), "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(NClans.getInstance(), "BungeeCord", this);
    }

    public void close() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(NClans.getInstance(), "BungeeCord", this);
    }

    @Override
    public boolean isAvailable() {
        return !Bukkit.getOnlinePlayers().isEmpty();
    }

    public void send(String id,  MessagePacket packet) {
        var players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().runTaskAsynchronously(NClans.getInstance(), () -> {
            if (players.isEmpty()) return;
            var player = players.getFirst();
            var out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("NClans");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(packet.getType().name());
                msgout.writeUTF(id);
                packet.serialize(msgout);
                byte[] arr = msgbytes.toByteArray();
                out.writeShort(arr.length);
                out.write(arr);
                msgbytes.close();
                msgout.close();
                player.sendPluginMessage(NClans.getInstance(), "BungeeCord", out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equalsIgnoreCase("NClans")) return;

        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        NClans.getInstance().getForwarder().decodeMessage(msgin);
    }
}
