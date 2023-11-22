package academy.prog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter your login: ");
            String login = scanner.nextLine();
            ClientChatManager clientChatManager = new ClientChatManager(login);
            clientChatManager.startRealtimeHistoryUpdate();
            clientChatManager.startChatting();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
