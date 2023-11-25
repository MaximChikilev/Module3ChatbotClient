package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientChatManager {
    private String login;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Map<String, String> serviceCommands = new HashMap<>();
    private Map<String, User> usersList;

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
                prepareServiceCommand(text);
            } else {
                sendNewMessage(text);
            }
        }
        scanner.close();
    }

    private void getUsersList() throws IOException {
        Type itemsMapType = new TypeToken<Map<String, User>>() {
        }.getType();
        java.net.URL url = new URL(Utils.getURL() + "/getUsers");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        InputStream is = http.getInputStream();
        try {
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            usersList = gson.fromJson(strBuf, itemsMapType);
        } finally {
            is.close();
        }
    }

    private void setAllServiceCommands() {
        serviceCommands.put("/service showUsersList", "showUsersList");
        serviceCommands.put("/service changeStatus", "changeUserStatus");
    }

    public boolean isServiceCommand(String command) {
        Boolean result = false;
        if(command.substring(0,8).equals("/service")) result = true;
        return result;
    }
    private String getServiceCommand(String fullCommand){
        return fullCommand.split(":")[0];
    }
    private  String[] extractCommandParams(String fullCommand){
        int openBracketIndex = fullCommand.indexOf("(");
        if(openBracketIndex!=-1){
            int closeBracketIndex = fullCommand.indexOf(")");
            return fullCommand.substring(openBracketIndex,closeBracketIndex).split(",");
        }
        return new String[0];
    }

    public void prepareServiceCommand(String command) {
        String serviceCommand = getServiceCommand(command);
        String[] commandParams = extractCommandParams(command);
        try {
            executeServiceCommand(serviceCommand,commandParams);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private void executeServiceCommand(String serviceCommand, String[] commandParams) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = serviceCommands.get(serviceCommand);
        Class clientChatManagerClass = ClientChatManager.class;
        Method[] methods = clientChatManagerClass.getDeclaredMethods();
        for(Method methodsElement:methods){
            if(methodsElement.getName().equals(methodName)){
                Class<?>[] parameterTypes = methodsElement.getParameterTypes();
                Method someMethod = clientChatManagerClass.getMethod(methodName, parameterTypes);
                someMethod.invoke(this, castCommandsParams(parameterTypes,commandParams));
            }
        }
    }
    private Object[] castCommandsParams(Class<?>[] parameterTypes,String[] commandParams){
        if(commandParams.length!=0){
            Object[] paramsArray = new Object[parameterTypes.length];
            for (int i=0;i<commandParams.length;i++){
                paramsArray[i] = parameterTypes[i].cast(commandParams[i]);
            }
            return paramsArray;
        }
        return null;
    }

    private void sendNewMessage(String text) throws IOException {
        Message message = generateNewMessageFromText(text);
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

    private Message generateNewMessageFromText(String text) {
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
        URL url = new URL(Utils.getURL() + "/changeStatus?login="+login+"&status="+status);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        //http.getResponseCode();
        getUsersList();
    }
}
