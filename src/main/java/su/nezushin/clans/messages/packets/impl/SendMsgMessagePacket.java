package su.nezushin.clans.messages.packets.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendMsgMessagePacket extends MessagePacket {

    private String message;
    private String player;

    public SendMsgMessagePacket() {
    }

    public SendMsgMessagePacket(String player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(message);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        player = in.readUTF();
        message = in.readUTF();
    }

    @Override
    public MessageType getType() {
        return MessageType.send_message;
    }


    @Override
    public void exec() {
        var p = Bukkit.getPlayerExact(player);
        if (p != null)
            for (var i : message.split("\n"))
                p.sendMessage(MiniMessage.miniMessage().deserialize(i));
    }
}
