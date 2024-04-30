
package tolt.client;

import tolt.client.network.Network;

public class Main {

    public static void main (String[] args) {


        try {

            System.out.print("Enter Server IPA: ");
            String address = System.console().readLine();
            System.out.print("Enter Server Port: ");
            int port = Integer.parseInt(System.console().readLine());
            int result = Network.connect(address, port);
            if (result != 0) System.exit(result);

            System.out.print("Register[0], Login[1]: ");
            int mode = Integer.parseInt(System.console().readLine());
            if (mode == 0) {

                System.out.print("Enter username: ");
                String username = System.console().readLine();
                System.out.print("Enter password hash: ");
                String passwordHash = System.console().readLine();
                System.out.print("Enter real name: ");
                String realName = System.console().readLine();
                System.out.print("Enter email address: ");
                String emailAddress = System.console().readLine();

                Network.authenticate(username, passwordHash, realName, emailAddress);

            } else {

                System.out.print("Enter username: ");
                String username = System.console().readLine();
                System.out.print("Enter password hash: ");
                String passwordHash = System.console().readLine();

                Network.authenticate(username, passwordHash);
            }

            Network.startIO();

        } catch (Exception e) {}

        // Network.authenticate("jonnyjon881", "FAKEHASH", "john the dripper", "john@smtp.john.com");
        // Network.authenticate("jonnyjon881", "FAKEHASH");

        try { System.in.read(); } catch (Exception e) {}

        Network.stop();
    }
}
