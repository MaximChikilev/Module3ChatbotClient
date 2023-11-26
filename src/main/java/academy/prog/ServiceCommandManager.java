package academy.prog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServiceCommandManager {
    private Map<String, ServiceCommand> serviceCommands = new HashMap<>();

    public ServiceCommandManager() {
            serviceCommands.put("/s showUsersList", new ServiceCommand("/s showUsersList","showUsersList","show full users List. Without params"));
            serviceCommands.put("/s changeStatus", new ServiceCommand("/s changeStatus","changeUserStatus","Change your status. With params. /s changeStatus:(<param>). Param must be Offline or Online"));
            serviceCommands.put("/s showServiceCommand", new ServiceCommand("/s showServiceCommand","showServiceCommand","Show full list service commands. Without params"));
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
        if(serviceCommands.containsKey(getServiceCommand(command))) result = true;
        return result;
    }
    public String getServiceCommand(String fullCommand){
        return fullCommand.split(":")[0];
    }
    public   String[] extractCommandParams(String fullCommand){
        int openBracketIndex = fullCommand.indexOf("(");
        if(openBracketIndex!=-1){
            int closeBracketIndex = fullCommand.indexOf(")");
            return fullCommand.substring(openBracketIndex+1,closeBracketIndex).split(",");
        }
        return new String[0];
    }
}
