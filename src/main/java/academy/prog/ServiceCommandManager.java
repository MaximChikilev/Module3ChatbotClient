package academy.prog;

import java.util.HashMap;
import java.util.Map;

public class ServiceCommandManager {
    private Map<String, ServiceCommand> serviceCommands = new HashMap<>();

    public ServiceCommandManager() {
            serviceCommands.put("/s showUsersList", new ServiceCommand("/s showUsersList","showUsersList","show full users List. Without params"));
            serviceCommands.put("/s changeStatus", new ServiceCommand("/s changeStatus","changeUserStatus","Change your status. With params. Param 1: expected status(Offline/Online)"));
            serviceCommands.put("/s showServiceCommand", new ServiceCommand("/s showServiceCommand","showServiceCommand","Show full list service commands. Without params"));
            serviceCommands.put("/s sendContent", new ServiceCommand("/s sendContent","sendContent","Send file. With params. Param 1: recipient(UserLogin) if empty recipient = All , Param 2: File name (can't be Null), Param 3 : Some message"));
            serviceCommands.put("/s getContentFromMessage", new ServiceCommand("/s getContentFromMessage","downloadContentFromMessage","Download file from message. With params. Param 1: message ID"));
        }

    public void showServiceCommands() {
        System.out.println("Service commands are :");
        for (String serviceCommand: serviceCommands.keySet()){
            System.out.println(serviceCommands.get(serviceCommand).toString());
        }
    }
    public String getMethodName(String command){
        return serviceCommands.get(command).getMethodName();
    }
    public Boolean isServiceCommand(String command){
        Boolean result = false;
        if(serviceCommands.containsKey(command)) result = true;
        return result;
    }
    public String getCommandDescription(String command){
        return serviceCommands.get(command).getCommandDescription();
    }

}
