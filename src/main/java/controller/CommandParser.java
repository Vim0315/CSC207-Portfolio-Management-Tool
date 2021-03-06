package controller;

import usecase.commands.Command;
import usecase.commands.CommandManager;
import usecase.commands.CommandProtocol;
import usecase.clientInterface.ClientInterface;
import usecase.managers.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * This is a Class for parsing command send from customers.
 * It recognizes wish command is being called and
 * calls the "execute()" method in the corresponding command class.
 * User can also check the status of our discord bot
 * <p>
 * Author Edward Li
 * Date: Nov 29 2021
 * Version: 1.0
 */
@SuppressWarnings("ALL")
public class CommandParser extends ListenerAdapter implements ClientInterface {
    private final String prefix = "!";
    private final CommandManager commandManager = CommandManager.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final VoteManager voteManager = VoteManager.getInstance();
    private final TransactionManager transactionManager = TransactionManager.getInstance();
    private final AssetManager assetManager = AssetManager.getInstance();
    private final DataAccessInterfaceControllerRelay api;
    private final GraphicInterfaceRelay graphicsUserInterface;

    public CommandParser(DataAccessInterfaceControllerRelay yahooapi, GraphicInterfaceRelay graphics){
        this.api = yahooapi;
        this.graphicsUserInterface = graphics;
    }


    /**
     * Callback method when the bot receives message from users
     *
     * @param event represents a message from discord
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // String message = event.getMessage().getContentRaw();
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (!event.getAuthor().isBot() && message[0].equals("!")) {
            String[] cmdArgs = new String[message.length - 1];
            System.arraycopy(message, 1, cmdArgs, 0, cmdArgs.length);
            String result = null;
            try {
                result = parseCommand(cmdArgs, event.getAuthor().getName(), event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getMessage().reply(result).queue();
        }
    }

    /**
     * parse the command received by the discord bot
     *
     * @param cmdArgs is a list of all arguments accomplishing that command
     * @param author  is the person who send the command
     */
    public String parseCommand(String[] cmdArgs, String author, GuildMessageReceivedEvent event) throws IOException {
        boolean res;
        Command cmd;
        if (cmdArgs.length == 0) {
            return ("Your command is empty.");
        }
        String cmdName = cmdArgs[0];
        String[] ArgWithoutCmd = new String[cmdArgs.length - 1];
        System.arraycopy(cmdArgs, 1, ArgWithoutCmd, 0, cmdArgs.length - 1);
        if (cmdName.equals("GOODJOB")) return ("Thanks:)");
        if (cmdName.equals("checkstatus")) return ("This bot is working");
        if (cmdName.equals("hello")) return ("Hello! " + author);
        if (cmdName.equals("getprice")) {
            return ("the price is " + api.update(ArgWithoutCmd[0]));
        }
        if (cmdName.equals("listallcommand"))
            return ("getprice\ncheckstatus\ncreateuser\nbuy\nviewtransaction\nviewvote\nGOODJOB" +
                    "\nviewassetvolume\nviewallasset\ngetGraph\nsavefile ");

        if (cmdName.equals("createuser")) {
            if (ArgWithoutCmd.length != 0) return ("Just type '! createuser' to create a user");
            String[] argForCreateUser = {author};
            CommandProtocol commandProtocol = new CommandProtocol(null, new CommandParser(this.api, this.graphicsUserInterface), this.api, argForCreateUser);
            cmd = commandManager.generate(commandManager.find(cmdName), commandProtocol);
            if (cmd == null) return ("No such command. Try again.");
            res = cmd.execute();
            if (!res) return cmd.help();
            return ("user " + author + " successfully created!\nUser '! listallcommand' to check all commands");
        }
        if (userManager.findUser(author) == null) {
            return ("You are not a user of this system. Use createuser to create a new user.");
        }
        if (cmdName.equals("viewtransaction") || cmdName.equals("vt")) {
            if (transactionManager.size() == 0) return ("There is no votes currently.");
            return (transactionManager.toString());
        }
        if (cmdName.equals("viewvote") || cmdName.equals("vv")) {
            System.out.println(voteManager.viewVote());
            return (voteManager.viewVote());
        }
        if (cmdName.equals("viewassetvolume") || cmdName.equals("asv")) {
            if (ArgWithoutCmd.length != 1) return ("You need to add symbol as argument.");
            assetManager.getTypeVolume(ArgWithoutCmd[0]);
            return (Double.toString(assetManager.getTypeVolume(ArgWithoutCmd[0])));
        }
        if (cmdName.equals("viewallasset") || cmdName.equals("vaa")) {
            return (assetManager.viewAssets(this.api));
        }
        if (cmdName.equals("getGraph")) {
            PerformanceHistoryManager.updateTotalDeposit(userManager.findUser(author).getUserPortfolio().getValue(this.api));
            PerformanceHistoryManager.recordHistory(this.api);
            this.graphicsUserInterface.generateGraphics(this.api);
            this.graphicsUserInterface.generateImage(this.api);

            File file = new File(".\\images\\image.png");

            if (Objects.nonNull(file)) {
                event.getChannel().sendMessage("Here is my image !").addFile(file).queue();
            } else {
                event.getChannel().sendMessage("Sorry, I can't found the image :c").queue();
            }
            return "Graph Presented";
        }

        if (cmdName.equals("savefile")) {
            AssetManager.getInstance().save();
            TransactionManager.getInstance().save();
            VoteManager.getInstance().save();
            UserManager.getInstance().save();
            return ("Saved");
        }

        CommandProtocol commandProtocol = new CommandProtocol(userManager.findUser(author), new CommandParser(this.api, this.graphicsUserInterface), this.api, ArgWithoutCmd);
        cmd = commandManager.generate(commandManager.find(cmdName), commandProtocol);
        if (cmd == null) return ("No such command. Try again.");
        res = cmd.execute();
        if (!res) return cmd.help();

        return ("command successfully executed!");
    }

    @Override
    public void input(String s) {

    }

    @Override
    public void output(String s) {

    }
}
