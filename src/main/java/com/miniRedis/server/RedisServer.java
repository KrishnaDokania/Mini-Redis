
package com.miniRedis.server;

import com.miniRedis.command.CommandHandler;
import com.miniRedis.protocol.RESPParser;
import com.miniRedis.protocol.RESPResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {

    private final int port;
    private final CommandHandler handler;

    private final RESPParser parser;
    private final RESPResponse response;

    private final ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public RedisServer(int port, CommandHandler handler) {

        this.port = port;
        this.handler = handler;

        this.parser = new RESPParser();
        this.response = new RESPResponse();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("MiniRedis running on port " + port);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected: "
                        + clientSocket.getInetAddress());

                threadPool.execute(() -> handleClient(clientSocket));
            }

        } catch (Exception e) {

            System.out.println("Server error: " + e.getMessage());

        } finally {

            threadPool.shutdown();
        }
    }

    private void handleClient(Socket socket) {

        try (
                Socket client = socket;
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream()
        ) {

            while (true) {

                // Read a RESP command
                String[] parts = parser.readCommand(input);

                // Client disconnected
              if (parts == null) {
    System.out.println("Client disconnected normally.");
    break;
} 


                // Execute command
                String result = handler.handle(parts);

                // Convert result into RESP response
                sendResponse(output, parts[0], result);
            }

        } catch (IOException e) {

            System.out.println("Client disconnected: "
                    + e.getMessage());

        } catch (Exception e) {

            System.out.println("Client error: "
                    + e.getMessage());
        }
    }

    private void sendResponse(
            OutputStream output,
            String command,
            String result
    ) throws IOException {

        String cmd = command.toUpperCase();

        // Handle errors
        if (result.startsWith("ERR")) {

            response.writeError(output, result);
            return;
        }

        switch (cmd) {

           case "SET":
case "SETEX":
case "PING":
    response.writeSimpleString(output, result);
    break;

            case "GET":

                if ("(nil)".equals(result)) {
                    response.writeBulkString(output, null);
                } else {
                    response.writeBulkString(output, result);
                }

                break;

            case "DEL":
            case "EXISTS":
                response.writeInteger(output, Long.parseLong(result));
                break;
            case "EXPIRE":
case "TTL":
    response.writeInteger(output, Long.parseLong(result));
    break;
    

            default:
                response.writeSimpleString(output, result);
        }
    }
}
