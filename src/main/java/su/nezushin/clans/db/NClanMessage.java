package su.nezushin.clans.db;

import su.nezushin.anvil.orm.SqlFlag;
import su.nezushin.anvil.orm.SqlType;
import su.nezushin.anvil.orm.table.AnvilORMSerializable;
import su.nezushin.anvil.orm.table.SqlColumn;
import su.nezushin.clans.NClans;
import su.nezushin.clans.messages.packets.MessagePacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class NClanMessage implements AnvilORMSerializable {


    @SqlColumn(type = SqlType.VARCHAR, flags = SqlFlag.PRIMARY_KEY)
    private String id;

    @SqlColumn(type = SqlType.VARCHAR, name = "target_server")
    private String targetServer;

    @SqlColumn(type = SqlType.BLOB)
    private byte[] message;

    public NClanMessage() {
    }

    public NClanMessage(String targetServer, byte[] message) {
        this.id = UUID.randomUUID().toString();
        this.targetServer = targetServer;
        this.message = message;
    }

    public void exec() {
        NClans.getInstance().getForwarder().decodeMessage(new DataInputStream(new ByteArrayInputStream(this.message)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
