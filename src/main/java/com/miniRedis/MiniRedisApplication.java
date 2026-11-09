
package com.miniRedis;

import com.miniRedis.command.CommandParser;
import com.miniRedis.command.CommandHandler;
import com.miniRedis.storage.RedisStorage;

import java.util.Scanner;

public class MiniRedisApplication {

    public static void main(String[] args) {

        RedisStorage storage = new RedisStorage();
        CommandParser parser = new CommandParser();
        CommandHandler handler = new CommandHandler(storage);

        Scanner scanner = new Scanner(System.in);

        System.out.println("MiniRedis Server Started!");
        System.out.println("Type EXIT to quit.");

        while (true) {

            System.out.print("MiniRedis> ");

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("EXIT")) {
                System.out.println("Goodbye!");
                break;
            }

            try {
                String[] parts = parser.parse(input);

                String response = handler.handle(parts);

                System.out.println(response);

            } catch (IllegalArgumentException e) {
                System.out.println("ERR " + e.getMessage());
            }
        }

        scanner.close();
    }
}
