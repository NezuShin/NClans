package su.nezushin.clans.messages.packets.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import su.nezushin.clans.events.ClanDeleteEvent;
import su.nezushin.clans.events.ClanHomeDeleteEvent;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;
import su.nezushin.clans.util.YamlStringSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeleteClanMessagePacket extends MessagePacket {

    private String clan;

    public DeleteClanMessagePacket() {
        //TODO: NOT REALLY USED FOR NOW
    }

    public DeleteClanMessagePacket(String clan) {
        this.clan = clan;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(clan);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        clan = in.readUTF();
    }

    @Override
    public MessageType getType() {
        return MessageType.delete_clan;
    }

    @Override
    public void exec() {
        Bukkit.getPluginManager().callEvent(new ClanDeleteEvent(
                clan
        ));
    }
}
