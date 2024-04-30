
package tolt.client.network.module;

public class Packet {

    public short id;
    public byte[] data;

    public Packet (short id, byte[] data) {

        this.id = id;
        this.data = data;
    }
}
