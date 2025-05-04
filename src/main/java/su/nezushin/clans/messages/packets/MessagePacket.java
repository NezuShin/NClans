package su.nezushin.clans.messages.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MessagePacket {

    public abstract void serialize(DataOutputStream out) throws IOException;

    public abstract void deserialize(DataInputStream in) throws IOException;

    public abstract MessageType getType();


    public abstract void exec();

}
