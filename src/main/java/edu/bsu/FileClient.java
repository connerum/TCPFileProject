package edu.bsu;

import java.io.*;
import java.net.*;

public class FileClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java FileClient <server IP address> <server port number>");
            System.exit(1);
        }

        String serverIP = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(serverIP, portNumber);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to file server at " + serverIP + ":" + portNumber);

            while (true) {
                System.out.print("Enter a command (L)ist, (Del)ete, (R)ename, (U)pload, or (D)ownload: ");
                String command = console.readLine();

                if (command.equals("L")) {
                    String fileName;
                    while (!(fileName = input.readUTF()).equals("END")) {
                        System.out.println(fileName);
                    }
                } else {
                    System.out.println("Invalid command.");
                }
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Error: Unknown host " + serverIP);
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}