package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientChatManager {
    private String login;
    private final Gson gson = new GsonBuilder().create();
    private Map<String, String> serviceCommands = new HashMap<>();
    private List<String> usersList;

    public ClientChatManager(String login) {
        this.login = login;
        setAllServiceCommands();
        try {
            getUsersList();
        } catch (IOException e) {

        }
    }

    public void startRealtimeHistoryUpdate() {
        Thread realTimeHistoryUpdater = new Thread(new GetThread(login));
        realTimeHistoryUpdater.setDaemon(true);
        realTimeHistoryUpdater.start();
    }

    public void startChatting() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your message: ");
        while (true) {
            String text = scanner.nextLine();
            if (text.isEmpty()) break;
            if (isServiceCommand(text)) {
                startServiceCommand(text);
            } else {
                sendNewMessage(text);
            }
        }
        scanner.close();
    }

    public void getUsersList() throws IOException {
        java.net.URL url = new URL(Utils.getURL() + "/getUsers");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        InputStream is = http.getInputStream();
        try {
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            usersList = gson.fromJson(strBuf, List.class);
            if (usersList != null) {
                System.out.print("All users are : ");
                for (String user : usersList) {
                    System.out.print(user + ",");
                }
                System.out.println("");
            }
        } finally {
            is.close();
        }
    }

    private void setAllServiceCommands() {
        serviceCommands.put("chat -getUsersList", "getUsersList");
    }

    public boolean isServiceCommand(String command) {
        return serviceCommands.containsKey(command);
    }

    public void startServiceCommand(String command) {
        try {
            Method method = this.getClass().getMethod(serviceCommands.get(command));
            method.invoke(this, null);
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (IllegalAccessException e) {

        }
    }

    private void sendNewMessage(String text) throws IOException {
        Message message = generateNewMessageFromText(text);
        int res = message.send(Utils.getURL() + "/add");
        if (res != 200) { // 200 OK
            System.out.println("HTTP error occurred: " + res);
            return;
        }
    }

    private Message generateNewMessageFromText(String text) {
        String recipient = "All";
        String message = text;
        if (text.substring(0, 1).equals("@")) {
            int firstSpaceIndex = text.indexOf(" ");
            String testRecipient = text.substring(1, firstSpaceIndex);
            if (usersList.contains(testRecipient)) {
                recipient = testRecipient;
                message = text.substring(firstSpaceIndex+1);
            }
        }
        return new Message(login, message, recipient);
    }
}
