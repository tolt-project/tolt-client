
package tolt.client.network;

import java.lang.Thread;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

import tolt.client.security.Loading.PemLoader;
import tolt.client.network.module.Packet;

public class Network {

    private static boolean running, shouldStop = false;
    public static boolean isOnline () { return running; }

    public static int connect (String address, int port) {

        X509Certificate certificate = PemLoader.loadX509Certificate("keys/server-cert.pem");

        if (certificate == null) {

            System.out.println("Failed to start! Server Certificate was not found!");
            return -1;
        }

        try {

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("default", certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());


            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            socket = (SSLSocket)socketFactory.createSocket(address, port);

            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();

            return 0;

        } catch (Exception e) {

            e.printStackTrace();
            return -2;
        }
    }

    public static void authenticate (
        String username,
        String passwordHash,
        String realName,
        String emailAddress
    ) {
        Authentication.register(
            username, passwordHash, realName, emailAddress, outStream
        );
    }
    public static void authenticate (String username, String passwordHash) {
        Authentication.login(username, passwordHash, outStream);
    }
    public static void authenticate (String token) {
        System.out.println("not implemented!");
    }

    public static void startIO () {

        running = true; shouldStop = false;

        new Thread () {
            public void run () { sendLoop(); }
        }.start();
        new Thread () {
            public void run () { recvLoop(); }
        }.start();
    }

    public static void stop () {

        try { socket.close(); } catch (Exception e) {}
        shouldStop = true;
        while (running) try { Thread.sleep(10); } catch (Exception e) {}
    }

    private static SSLSocket socket;
    private static InputStream inStream;
    private static OutputStream outStream;

    private static int idCache = -1, capacity;
    private static byte[] sendCache;
    private static final int packetChunkMax = 10;
    private static void sendLoop () {

        while (!shouldStop) try {

            if (IOQueues.Send.count() == 0)
                Thread.sleep(10);

            else {

                sendCache = IOQueues.Send.pop();
                capacity = packetChunkMax;

                    System.out.println("");
                    System.out.println("In length: " + sendCache.length);
                    System.out.println("Capacity: " + capacity);

                if (capacity > sendCache.length) {

                    outStream.write(sendCache, 0, sendCache.length);

                        String c = ""; for (int x = 0; x < sendCache.length; ++x) c += sendCache[x] + ",";
                        System.out.println(String.format("sent: [%sEOB]", c));

                } else {

                    if (sendCache.length % capacity != 0) {
                        while (sendCache.length % capacity < 6 && capacity > 6)
                            capacity--;


                        System.out.println("Adjusted capacity: " + capacity);
                    }

                    int count = -java.lang.Math.floorDiv(-sendCache.length, capacity);
                    for (int s = 0; s < count; ++s) {

                        int size = java.lang.Math.min(capacity, sendCache.length - (s * capacity));
                        byte[] section = new byte[size];
                        System.arraycopy(sendCache, s * capacity, section, 0, size);

                        outStream.write(section, 0, size);

                            String c = ""; for (int x = 0; x < size; ++x) c += section[x] + ",";
                            System.out.println(String.format("sent: [%sEOB]", c));
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        running = false;
    }
    private static void recvLoop () {

        byte[] cacheBuffer = new byte[1];
        ByteBuffer recvBuffer = ByteBuffer.allocate(2);
        short packetId = 0; int packetSize = 0;
        int recvBytes = 0, state = 0;

        try { while (!shouldStop) {

            recvBytes = inStream.read(cacheBuffer, 0, 1);
            if (recvBytes <= 0) break;

            recvBuffer.put(cacheBuffer[0]);

            switch (state) {

                case 0: {
                    if (recvBuffer.position() != 2) break;

                    recvBuffer.rewind();
                    packetId = recvBuffer.getShort();
                    recvBuffer = ByteBuffer.allocate(4);
                    state = 1;

                break; }

                case 1: {
                    if (recvBuffer.position() != 4) break;

                    //later add a check here to stop HUGE packets being send

                    recvBuffer.rewind();
                    packetSize = recvBuffer.getInt();

                    if (packetSize == 0) {

                        ///////////////////////////// TEMP
                            System.out.println(
                                "packetId: " + packetId +
                                ", packetSize: " + packetSize +
                                ", packetData: [null]"
                            );
                        ///////////////////////////// TEMP

                        IOQueues.Recv.queue(new Packet(packetId, new byte[0]));

                        recvBuffer = ByteBuffer.allocate(2);
                        state = 0;

                    } else {
                        recvBuffer = ByteBuffer.allocate(packetSize);
                        state = 2;
                    }

                break; }

                case 2: {
                    if (recvBuffer.position() != packetSize) break;

                    ///////////////////////////// TEMP
                        String cache = "";
                        for (byte b : recvBuffer.array()) cache += b +", ";
                        System.out.println(
                            "packetId: " + packetId +
                            ", packetSize: " + packetSize +
                            ", packetData: [" + cache + "EOB]"
                        );
                    ///////////////////////////// TEMP

                    IOQueues.Recv.queue(new Packet(packetId, new byte[0]));

                    recvBuffer = ByteBuffer.allocate(2);
                    state = 0;

                break; }
            }

        } } catch (Exception e) {

            e.printStackTrace(); return;
        }

        //if an exception occurs, or the connection closes and nothing is read
        //then control will flow here

        // TODO
        //  make it so a flag is triggered here that is proccessed by the readloop
        //  or something, and tehn it closes the connection properly
    }
}
