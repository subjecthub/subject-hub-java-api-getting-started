package com.migratorydata.example;

import com.migratorydata.client.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {

        // create a MigratoryData client
        final MigratoryDataClient client = new MigratoryDataClient();

        // Define the log listener and verbosity
        client.setLogListener(new MigratoryDataLogListener() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ");

            @Override
            public void onLog(String log, MigratoryDataLogLevel level) {
                String isoDateTime = sdf.format(new Date(System.currentTimeMillis()));
                System.out.println(String.format("[%1$s] [%2$s] %3$s", isoDateTime, level, log));
            }
        }, MigratoryDataLogLevel.DEBUG);

        // attach the entitlement token
        client.setEntitlementToken(Config.token);

        // Define the listener to handle live message and status notifications
        // In your application it is recommended to define a regular class
        // instead of the anonymous class we define here for concision
        client.setListener(new MigratoryDataListener() {

            public void onStatus(String status, String info) {
                System.out.println("Got Status: " + status + " - " + info);
            }

            public void onMessage(MigratoryDataMessage message) {
                System.out.println("Got Message: " + message);
            }

        });

        client.setEncryption(Config.encryption);

        // set server to connect to the MigratoryData server
        client.setServers(Config.server);

        // subscribe
        client.subscribe(Arrays.asList(Config.subject));

        // connect to the MigratoryData server
        client.connect();

        // publish a message every 3 seconds
        int count = 1;
        while (count++ < 10000000) {
            String content = "data - " + count;
            String closure = "id" + count;
            client.publish(new MigratoryDataMessage(Config.subject, content.getBytes(), closure));

            Thread.sleep(3000);
        }

        // add a shutdown hook to catch CTRL-C and cleanly shutdown this client
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                client.disconnect();
            }
        });
    }
}