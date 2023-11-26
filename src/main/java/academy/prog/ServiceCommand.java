package academy.prog;

public class ServiceCommand {
    private String commandName;
    private String methodName;
    private String commandDescription;

    public ServiceCommand(String commandName, String methodName, String commandDescription) {
        this.commandName = commandName;
        this.methodName = methodName;
        this.commandDescription = commandDescription;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String toString() {
        return "commandName='" + commandName + '\'' +
                ", commandDescription='" + commandDescription + '\'' +
                '}';
    }
}
