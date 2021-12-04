package Managers;

import Assets.Asset;
import Assets.Currency;
import Assets.DataAccessInterface;
import Containers.Transaction;
import Users.User;

public class TransactionExecutor {

    /**
     * execute a Transaction
     *
     * @param transaction a transaction to be executed
     * @param api         from yahoo finance to update price
     */

    public static void execute(Transaction transaction, DataAccessInterface api) {
        Asset bought = transaction.buy;
        Asset sold = transaction.sell;
        AssetManager am = AssetManager.getInstance();
        VoteManager vm = VoteManager.getInstance();
        TransactionManager tm = TransactionManager.getInstance();

        // remove the transaction from TransactionManager to indicate transaction is complete
        if (sold instanceof Currency) {        //buy stock

            bought.updatePrice(api);
            bought.setInitialPrice(bought.getPrice());

            double new_volume = -1 * sold.getValue() / bought.getPrice();

            bought.setVolume(new_volume);

            am.addAsset(bought);
            am.addAsset(sold);

            transaction.initiator.getUserPortfolio().add(bought);
            for (User user : vm.getVoters(transaction)) {
                user.getUserPortfolio().add(transaction);
            }

        } else {                                      //sell stock
            // preform transaction by modify asset
            sold.updatePrice(api);                  // update into newest price
            double new_value = sold.getValue();     // get the newest price
            bought.setVolume(new_value);      // reset the price

            am.addAsset(bought);                    // Add the bought asset into the group
            am.delAsset(sold);                      // remove the asset from the group

            transaction.initiator.getUserPortfolio().sub(sold);
            // remove the transaction to indicate vote is completed

        }
        tm.remove(transaction.getId());

    }
}
