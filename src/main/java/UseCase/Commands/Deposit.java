package UseCase.Commands;

import Entities.Assets.Currency;
import Entities.Assets.DataAccessInterface;
import Interfaces.ClientInterface;
import UseCase.Managers.AssetManager;
import Entities.Users.User;

/**
 * This Class is a Command that creates a deposit Transaction.
 * A deposit Transaction is one with no "sell" Asset and USD as the "buy" asset.
 * Deposit Transactions are immediately executed.
 */
public class Deposit extends Command{

    public Deposit(User initiator, ClientInterface client, DataAccessInterface api, String[] args) {
        super(initiator, client, api, args);
    }

    /**
     * Gets funds from Client and adds it to the system as an Asset.
     * this.args syntax: [double: volume]
     * @returns if successful
     */
    @Override
    public boolean execute() {
        if (this.args.length != 1) return false;

        double depositVolume;

        // Parse
        try {
            depositVolume = Double.parseDouble(this.args[0]);
        }
        catch (NumberFormatException e){
            return false;
        }

        // Get in-real-life funds
        if(!fundsTransfer(depositVolume)) return false;

        // Create Asset and Transaction
        Currency usd = new Currency(depositVolume, 1, "USD", "USD");
        initiator.getUserPortfolio().add(usd);
        AssetManager.getInstance().addAsset(usd);
        return true;
    }

    /**
     * Process for getting funds from the client.
     * @param volume is the amount of funds to request
     * @returns if successful
     */
    public boolean fundsTransfer(Double volume){
        //TODO: implement functionality to get funds from the client.
        return true;
    }

    @Override
    public String help() {
        return "Deposit: to add funds to the system.\n" +
                "Syntax: deposit [volume]\n" +
                "Usage: deposit 10000000\n\n";
    }

    @Override
    public String name() {
        return "deposit";
    }
}

