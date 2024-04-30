
package tolt.client.network;

import java.util.Queue;
import java.util.LinkedList;

import tolt.client.network.module.Packet;

public class IOQueues {

    public static class Recv {

        private static Queue<Packet> recvQueue = new LinkedList<Packet>();

        public static void queue (Packet packet) { synchronized (recvQueue) {
            recvQueue.add(packet);
        } }
        public static int count () { synchronized (recvQueue) {
            return recvQueue.size();
        } }
        public static Packet pop () { synchronized (recvQueue) {
            return recvQueue.remove();
        } }
    }

    public static class Send {

        private static Queue<byte[]> sendQueue = new LinkedList<byte[]>();

        public static void queue (byte[] data) { synchronized (sendQueue) {
            sendQueue.add(data);
        } }
        public static int count () { synchronized (sendQueue) {
            return sendQueue.size();
        } }
        public static byte[] pop () { synchronized (sendQueue) {
            return sendQueue.remove();
        } }
    }
}
