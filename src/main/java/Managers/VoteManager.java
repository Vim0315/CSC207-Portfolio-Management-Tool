package Managers;

import Assets.Asset;
import Containers.Transaction;
import Containers.Vote;
import Users.User;

import java.util.*;

public class VoteManager {
    private final Map<Transaction, List<Vote>> voteMap;

    //create an object of VoteManager
    private static final VoteManager instance = new VoteManager();

    private VoteManager() {this.voteMap = new HashMap<>();}

    //Get the only object available
    public static VoteManager getInstance() {return instance;}

    /**
     * add vote to a transaction
     * @param trans transaction is which the vote is voting for
     * @param initiator the initiator of the vote
     * @param decision the vote is an upvote or a down vote
     */
    public void addVote(Transaction trans, User initiator, Boolean decision){
        Vote vote = new Vote(initiator, decision);
        if (!this.voteMap.containsKey(trans)) {
            this.voteMap.put(trans, new ArrayList<>());
        }
        this.voteMap.get(trans).add(vote);
    }

    /**
     * @param vote a vote of a transaction
     * @return the transaction which the vote is voting for
     */
    public Transaction voteFor(Vote vote){
        for (Transaction trans: this.voteMap.keySet()){
            if (this.voteMap.get(trans).contains(vote)){
                return trans;
            }
        }
        return null;
    }

    /**
     * returns a list of votes for a given transaction
     * @param trans the transaction is the queried transaction
     * @return the votes of the transaction
     */
    public List<Vote> getVotes(Transaction trans){
        return this.voteMap.get(trans);
    }

    /**
     * returns a list of voters for a given transaction
     * @param trans the transaction is the queried transaction
     * @return the voters of the transaction
     */
    public List<User> getVoters(Transaction trans) {
        List<User> voters = new ArrayList<>();
        for (Vote vote: this.voteMap.get(trans)){
            voters.add(vote.initiator);
        }
        return voters;
    }

    /**
     * Returns number of upVoters for a given transaction
     * @param trans the transaction is the queried transaction
     * @return the number of upVoters in double
     */
    public double upVoters(Transaction trans){
        double result = 0;
        for (Vote vote: this.voteMap.get(trans)){
            if (vote.isUpvote) {
                result += 1;
            }
        }
        return result;
    }

    /**
     * Returns number of downVoters for a given transaction
     * @param trans the transaction is the queried transaction
     * @return the number of downVoters in double
     */
    public double downVoters(Transaction trans){
        double result = 0;
        for (Vote vote: this.voteMap.get(trans)){
            if (!vote.isUpvote) {
                result += 1;
            }
        }
        return result;
    }

    /**
     * Returns information in string format on votes for a given transaction
     * @param trans the transaction is the queried transaction
     * @return information
     */
    public String viewVote(Transaction trans){
        StringBuilder sb = new StringBuilder();
        double up = this.upVoters(trans);
        double down = this.downVoters(trans);
        Asset a = trans.buy;
        sb.append("transaction id: ").append(trans.id).append(", buy: ").append(a.getSymbol())
                .append(", value: ").append(a.getValue()).append(", number of upVoters: ")
                .append(up).append(", number of downVoters: ").append(down);
        return sb.toString();
    }

    /**
     * Returns information in string format on votes for all transactions
     * @return information
     */
    public String viewVote(){
        StringBuilder sb = new StringBuilder();
        for (Transaction trans: this.voteMap.keySet()){
            double up = this.upVoters(trans);
            double down = this.downVoters(trans);
            Asset a = trans.buy;
            sb.append("transaction id: ").append(trans.id).append(", buy: ").append(a.getSymbol())
                    .append(", value: ").append(a.getValue()).append(", number of upVoters: ")
                    .append(up).append(", number of downVoters: ").append(down).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
