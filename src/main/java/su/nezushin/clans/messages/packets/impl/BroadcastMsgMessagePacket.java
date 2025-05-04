package su.nezushin.clans.messages.packets.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BroadcastMsgMessagePacket extends MessagePacket {

    private String message;

    public BroadcastMsgMessagePacket() {
    }

    public BroadcastMsgMessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(message);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        message = in.readUTF();
    }

    @Override
    public MessageType getType() {
        return MessageType.broadcast_message;
    }


    @Override
    public void exec() {
        for (var p : Bukkit.getOnlinePlayers())
            for (var i : message.split("\n"))
                p.sendMessage(MiniMessage.miniMessage().deserialize(i));
    }
}
