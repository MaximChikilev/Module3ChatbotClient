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
            System.out.println("Enter - /s showServiceCommand to see service command list");
            System.out.println("Enter @Username to send private message");
            System.out.println("Enter exit to stop chatting");

            if (!clientChatManager.isExistUser()) clientChatManager.addNewUser();
            clientChatManager.startRealtimeHistoryUpdate();
            clientChatManager.startChatting();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
