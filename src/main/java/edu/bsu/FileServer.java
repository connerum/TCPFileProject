package edu.bsu;

import java.io.*;
import java.net.*;

public class FileServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FileServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("File server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostAddress());
                new ServerThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}

class ServerThread extends Thread {
    private Socket clientSocket;

    public ServerThread(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                DataInputStream dataInput = new DataInputStream(input);
                DataOutputStream dataOutput = new DataOutputStream(output);
        ) {
            byte command = dataInput.readByte();

            switch (command) {
                case 'L':
                    // List files
                    File folder = new File(".");
                    File[] files = folder.listFiles();

                    for (File file : files) {
                        if (file.isFile()) {
                            dataOutput.writeUTF(file.getName());
                        }
                    }
                    dataOutput.writeUTF("END");
                    dataOutput.writeUTF("S");
                    break;

                case 'D':
                    String fileName = dataInput.readUTF();
                    File file = new File(fileName);
                    if (file.delete()) {
                        dataOutput.writeUTF("S");
                    }
                    else {
                        dataOutput.writeUTF("F");
                    }

                default:
                    dataOutput.writeUTF("F");
                    break;
            }

            clientSocket.shutdownOutput();
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
