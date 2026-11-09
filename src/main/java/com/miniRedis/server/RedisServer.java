
package com.miniRedis.server;

import com.miniRedis.command.CommandHandler;
import com.miniRedis.command.CommandParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    private final int port;
    private final CommandParser parser;
    private final CommandHandler handler;

    public RedisServer(int port, CommandHandler handler) {
        this.port = port;
        this.handler = handler;
        this.parser = new CommandParser();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("MiniRedis running on port " + port);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected: "
                        + clientSocket.getInetAddress());

                handleClient(clientSocket);
            }

        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {

        try (
                Socket client = socket;
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                PrintWriter output = new PrintWriter(
                        client.getOutputStream(), true
                )
        ) {

            String command;

            while ((command = input.readLine()) != null) {

                if (command.equalsIgnoreCase("EXIT")) {
                    output.println("BYE");
                    break;
                }

                try {
                    String[] parts = parser.parse(command);

                    String response = handler.handle(parts);

                    output.println(response);

                } catch (IllegalArgumentException e) {
                    output.println("ERR " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }
}
