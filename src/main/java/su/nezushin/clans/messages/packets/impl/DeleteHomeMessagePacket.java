package su.nezushin.clans.messages.packets.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import su.nezushin.clans.events.ClanHomeDeleteEvent;
import su.nezushin.clans.messages.packets.MessagePacket;
import su.nezushin.clans.messages.packets.MessageType;
import su.nezushin.clans.util.YamlStringSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeleteHomeMessagePacket extends MessagePacket {

    private String clan;
    private String server;
    private String location;

    public DeleteHomeMessagePacket() {
    }

    public DeleteHomeMessagePacket(String clan, String server, String location) {
        this.clan = clan;
        this.server = server;
        this.location = location;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(clan);
        out.writeUTF(server);
        out.writeUTF(location);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        clan = in.readUTF();
        server = in.readUTF();
        location = in.readUTF();
    }

    @Override
    public MessageType getType() {
        return MessageType.delete_home;
    }

    @Override
    public void exec() {
        Bukkit.getPluginManager().callEvent(new ClanHomeDeleteEvent(
                clan,
                YamlStringSerializer.deserializeConfigurationSerializable(this.location, Location.class),
                server
        ));
    }
}
