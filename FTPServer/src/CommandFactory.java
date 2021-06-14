public class CommandFactory {
    /**
     * 命令分流
     **/
    public static Command createCommand(String type) {

        type = type.toUpperCase();
        switch(type)
        {
            case "USER":return new UserCommand();

            case "PASS":return new PassCommand();

            case "QUIT":return new QuitCommand();

            case "MYPORT":return new PortCommand();

            case "PASV":return new PasvCommand();

            case "CWD":return new CwdCommand();

            case "PWD": return new PwdCommand();

            case "LIST":return new ListCommand();

            case "DELE": return new DeleCommand();

            case "STOR":return new StorCommand();

            case "RETR":return new RetrCommand();

            default :return null;
        }

    }
}