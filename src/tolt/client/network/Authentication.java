
package tolt.client.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Authentication {

    public static void register (
        String username,
        String passwordHash,
        String realName,
        String emailAddress,
        OutputStream outStream
    ) { try {

        outStream.write(new byte[]{107,105,114,97,0}, 0, 5);

        byte[] buffer = username.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

        buffer = passwordHash.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

        buffer = realName.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

        buffer = emailAddress.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

    } catch (Exception e) { e.printStackTrace(); } }

    public static void login (
        String username,
        String passwordHash,
        OutputStream outStream
    ) { try {

        outStream.write(new byte[]{107,105,114,97,1}, 0, 5);

        byte[] buffer = username.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

        buffer = passwordHash.getBytes(StandardCharsets.UTF_8);
        outStream.write(ByteBuffer.allocate(4).putInt(buffer.length).array(), 0, 4);
        outStream.write(buffer, 0, buffer.length);

    } catch (Exception e) { e.printStackTrace(); } }
}
