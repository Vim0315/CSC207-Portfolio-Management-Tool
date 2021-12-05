package Controller;

import UseCase.Commands.Command;
import UseCase.Commands.CommandManager;
import UseCase.Commands.CommandProtocol;
import Interfaces.ClientInterface;
import Interfaces.YahooFinanceStockAPI;
import UseCase.Managers.AssetManager;
import UseCase.Managers.TransactionManager;
import UseCase.Managers.UserManager;
import UseCase.Managers.VoteManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * This is a Class for parsing command send from customers.
 * It recognizes wish command is being called and
 * calls the "execute()" method in the corresponding command class.
 * User can also check the status of our discord bot
 *
 * Author Edward Li
 * Date: Nov 29 2021
 * Version: 1.0
 */
public class CommandParser extends ListenerAdapter implements ClientInterface {
    private String prefix = "!";
    private CommandManager commandManager = CommandManager.getInstance();
    private UserManager userManager = UserManager.getInstance();
    private VoteManager voteManager = VoteManager.getInstance();
    private TransactionManager transactionManager = TransactionManager.getInstance();

    /**
     * Callback method when the bot receives message from users
     *  @param event represents a message from discord
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // String message = event.getMessage().getContentRaw();
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (! event.getAuthor().isBot() && message[0].equals("!")) {
            String[] cmdArgs = new String[message.length - 1];
            System.arraycopy(message, 1, cmdArgs, 0, cmdArgs.length);
            String result = parseCommand(cmdArgs, event.getAuthor().getName());
            event.getMessage().reply(result).queue();
        }
    }

    /**
     * parse the command received by the discord bot
     *  @param cmdArgs is a list of all arguments accomplishing that command
     *  @param author is the person who send the command
     */
    public String parseCommand(String[] cmdArgs, String author) {
        boolean res;
        Command cmd;
        if(cmdArgs.length == 0){
            return("Your command is empty.");
        }
        String cmdName = cmdArgs[0];
        String[] ArgWithoutCmd = new String[cmdArgs.length - 1];
        System.arraycopy(cmdArgs, 1, ArgWithoutCmd, 0, cmdArgs.length - 1);
        if(cmdName.equals("GOODJOB")) return("Thanks:)");
        if(cmdName.equals("checkstatus")) return("This bot is working");
        if(cmdName.equals("hello")) return("Hello! " + author);
        if(cmdName.equals("listallcommand")) return("createuser\nbuy\nviewalltransaction\n" +
                "viewvote\nviewallvote\nviewassetvolume\nGOODJOB\nviewmyasset");
        if(cmdName.equals("createuser")) {
            if(ArgWithoutCmd.length != 0) return("Just type '! createuser' to create a user");
            String[] argForCreateUser = {author};
            CommandProtocol commandProtocol = new CommandProtocol(null, new CommandParser(), new YahooFinanceStockAPI(), argForCreateUser);
            cmd = commandManager.generate(commandManager.find(cmdName), commandProtocol);
            if (cmd == null) return("No such command. Try again.");
            res = cmd.execute();
            if (! res) return cmd.help();
            return("user " + author + " successfully created!\nUser '! listallcommand' to check all commands");
        }
        if(userManager.findUser(author) == null){
            return("You are not a user of this system. Use createuser to create a new user.");
        }
        if(cmdName.equals("viewalltransaction")) {
            if(transactionManager.size() == 0) return("There is no votes currently.");
            String transList = transactionManager.toString();
            return(transList);
        }
        if(cmdName.equals("viewallvote")) {
            System.out.println(voteManager.viewVote());
            return(voteManager.viewVote());
        }
        if(cmdName.equals("viewassetvolume")) {
            if(ArgWithoutCmd.length != 1) return("You need to add symbol as argument.");
            AssetManager.getInstance().getTypeVolume(ArgWithoutCmd[0]);
            return(Double.toString(AssetManager.getInstance().getTypeVolume(ArgWithoutCmd[0])));
        }
        if(cmdName.equals("viewmyasset")) {
            if(ArgWithoutCmd.length != 1) return("You need to add symbol as argument.");
            AssetManager.getInstance().getTypeVolume(ArgWithoutCmd[0]);
            return(Double.toString(AssetManager.getInstance().getTypeVolume(ArgWithoutCmd[0])));
        }
        CommandProtocol commandProtocol = new CommandProtocol(userManager.findUser(author), new CommandParser(), new YahooFinanceStockAPI(), ArgWithoutCmd);
        cmd = commandManager.generate(commandManager.find(cmdName), commandProtocol);
        if (cmd == null) return("No such command. Try again.");
        res = cmd.execute();
        if (! res) return cmd.help();
        return("command successfully executed!");
    }

    @Override
    public void input(String s) {

    }

    @Override
    public void output(String s) {

    }
}