package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;


public class ClientChatManager {
    private String login;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private ServiceCommandManager serviceCommandManager = new ServiceCommandManager();
    private Map<String, User> usersList;
    private String workFolder = "C:\\Users\\Maxim\\IdeaProjects\\Homeworks\\module3JavaPro\\ChatClient\\ChatClient\\src\\main\\resources\\";

    public ClientChatManager(String login) {
        this.login = login;
        try {
            changeUserStatus("Online");
            getUsersList();
        } catch (IOException e) {

        }
    }

    public void startRealtimeHistoryUpdate() {
        Thread realTimeHistoryUpdater = new Thread(new GetThread(login));
        realTimeHistoryUpdater.setDaemon(true);
        realTimeHistoryUpdater.start();
    }

    public void startChatting() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.equals("exit")) break;
                if (isServiceCommand(text)) {
                    executeServiceCommand(text);
                } else {
                    sendNewMessage(generateNewMessage(text));
                }
            }
            scanner.close();
        } finally {
            changeUserStatus("Offline");
        }

    }

    private void getUsersList() throws IOException {
        Type itemsMapType = new TypeToken<Map<String, User>>() {
        }.getType();
        java.net.URL url = new URL(Utils.getURL() + "/getUsers");
        String strBuf = Utils.getStringFromResponse(url);
        usersList = gson.fromJson(strBuf, itemsMapType);
    }

    public boolean isServiceCommand(String command) {
        return serviceCommandManager.isServiceCommand(command);
    }

    private void executeServiceCommand(String serviceCommand) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = serviceCommandManager.getMethodName(serviceCommand);
        System.out.println(serviceCommandManager.getCommandDescription(serviceCommand));
        Class clientChatManagerClass = ClientChatManager.class;
        Method[] methods = clientChatManagerClass.getDeclaredMethods();
        for (Method methodsElement : methods) {
            if (methodsElement.getName().equals(methodName)) {
                Class<?>[] parameterTypes = methodsElement.getParameterTypes();
                Method someMethod = clientChatManagerClass.getMethod(methodName, parameterTypes);
                someMethod.invoke(this, prepareCommandParams(methodsElement));
            }
        }
    }

    private Object[] prepareCommandParams(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 0) {
            Object[] paramsArray = new Object[parameters.length];
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < parameters.length; i++) {
                System.out.print("Enter param №" + (i + 1) + ": ");
                paramsArray[i] = parameters[i].getType().cast(scanner.nextLine());
            }
            return paramsArray;
        }
        return null;
    }

    private void sendNewMessage(Message message) throws IOException {
        int res = message.send(Utils.getURL() + "/add");
        if (res != 200) { // 200 OK
            System.out.println("HTTP error occurred: " + res);
            return;
        }
    }

    public void addNewUser() throws IOException {
        User user = new User(login, Status.Online);
        String json = user.toJSON();
        int res = Utils.send(Utils.getURL() + "/addNewUser", json);
        if (res != 200) { // 200 OK
            System.out.println("HTTP error occurred: " + res);
            return;
        }
        getUsersList();
    }

    public Message generateNewMessage(String text) {
        String recipient = "All";
        String message = text;
        if (text.substring(0, 1).equals("@")) {
            int firstSpaceIndex = text.indexOf(" ");
            String testRecipient = text.substring(1, firstSpaceIndex);
            if (usersList.keySet().contains(testRecipient)) {
                recipient = testRecipient;
                message = text.substring(firstSpaceIndex + 1);
            }
        }
        return new Message(login, message, recipient);
    }

    public Boolean isExistUser() {
        return usersList.containsKey(login);
    }

    public void showUsersList() {
        try {
            getUsersList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (usersList != null) {
            System.out.println("All users are : ");
            for (String user : usersList.keySet())
                System.out.println(usersList.get(user).getUserName() + "(" + usersList.get(user).getStatus() + ") last time was : " + usersList.get(user).getLastAppearanceDate());
        }
    }

    public void changeUserStatus(String status) throws IOException {
        URL url = new URL(Utils.getURL() + "/changeStatus?login=" + login + "&status=" + status);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.getResponseCode();
        getUsersList();
    }

    public void showServiceCommand() {
        serviceCommandManager.showServiceCommands();
    }

    public void sendContent(String recipient, String fileName, String text) {
        System.out.println("recipient:" + (((recipient.equals("")) ? "All" : recipient) + " File name: " + fileName + " Text: " + text));
        Message message = new Message(login, text, (usersList.keySet().contains(recipient)) ? recipient : "All");
        Content content = new Content(fileName, text);
        try {
            content.uploadContent(workFolder);
            message.setContent(content);
            sendNewMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadContentFromMessage(String messageId) {
        try {
            URL url = new URL(Utils.getURL() + "/getMessageById?messageId=" + messageId);
            String strBuf = Utils.getStringFromResponse(url);
            if (strBuf.equals("")) {
                System.out.println("There is no message with this ID");
                return;
            }
            Message message = gson.fromJson(strBuf, Message.class);
            if ((message.getFrom().equals(login)) || (message.getTo().equals(login)) || (message.getTo().equals("All"))) {
                if (message.getContent() == null) {
                    System.out.println("There is not content in " + messageId + " message");
                    return;
                }
                message.getContent().downloadContent(workFolder);
                System.out.println("File :" + message.getContent().getFileName() + " was downloaded to - " + workFolder);
            } else System.out.println("You do not have access to this content");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
