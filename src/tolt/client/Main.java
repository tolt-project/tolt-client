
package tolt.client;

import tolt.client.network.Network;

public class Main {

    public static void main (String[] args) {

        int result = Network.connect();
        if (result != 0) System.exit(result);

        // Network.authenticate("jonnyjon881", "FAKEHASH", "john the dripper", "john@smtp.john.com");
        Network.authenticate("jonnyjon881", "FAKEHASH");

        Network.startIO();

        try { System.in.read(); } catch (Exception e) {}

        Network.stop();
    }
}
