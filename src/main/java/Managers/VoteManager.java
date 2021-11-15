package Managers;

import Containers.PendingDecision;
import Containers.Transaction;
import Containers.Vote;
import Helpers.VotingPowerHelper;
import Users.User;

import java.io.Serializable;
import java.util.*;

// VoteManager is a management and storage class for all pending decisions and their related votes.
// A pending transaction is a transaction that is not carried out and undergoing a voting process.
// The pending transaction's identifier is the transaction's identifier. No user is involved in storage.
public class VoteManager implements Serializable {

    private static VoteManager instance;

    static {
        VoteManager.instance = new VoteManager();
    }

    public static VoteManager getInstance() {
        return VoteManager.instance;
    }

    // The UUID of transaction and its related pending decision (transaction itself + votes)
    private final HashMap<UUID, PendingDecision> storage;

    private VoteManager() {
        this.storage = new LinkedHashMap<UUID, PendingDecision>();
    }

    // Register a new transaction to be pending and enabling the voting process.
    public void createVote(Transaction transaction) {
        PendingDecision pendingDecision = new PendingDecision(transaction);
        this.storage.put(transaction.id, pendingDecision);
    }

    // Obtain a snapshop of all pending transaction ids.
    public UUID[] getAllTransactions() {
        return this.storage.keySet().toArray(new UUID[0]);
    }
    // Obtain the pending transaction associated with a UUID.
    // WARNING: This method EXPOSES the actual object reference
    // If the uuid is invalid this method will return null.

    public PendingDecision getPendingDecision(UUID id) {
        return this.storage.get(id);
    }

    // Obtain a snapshot of all votes currently associated with a pending decision.
    // If the transaction is not in the registry the method will return null.
    public Vote[] getVotes(UUID id) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return null;
        return pendingDecision.votes.toArray(new Vote[0]);
    }

    // Obtain the number of votes currently associated with a pending decision.
    // If the transaction is not in the registry the method will return -1.
    public int getVoteCount(UUID id) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return -1;
        return pendingDecision.votes.size();
    }

    // Performs an up vote on a pending transaction.
    // If the transaction is not registered it will return false,
    // otherwise it will return true.
    public boolean doUpVote(UUID id, User user) {
        return this.doVote(id, new Vote(user, true));
    }

    // Performs a down vote on a pending transaction.
    // If the transaction is not registered it will return false,
    // otherwise it will return true.
    public boolean doDownVote(UUID id, User user) {
        return this.doVote(id, new Vote(user, false));
    }

    // Internal method to append a vote to a pending decision.
    private boolean doVote(UUID id, Vote vote) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return false;
        pendingDecision.votes.add(vote);
        return true;
    }

    // Calculate the total voting power for a specific pending decision.
    // If the transaction is not registered it will return NaN.
    // This method uses the voting power obtained from TransactionManager.
    public double calcVotingPower(UUID id) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return Double.NaN;
        double votingPowerSum = 0;
        for(Vote vote : pendingDecision.votes) {
            double votingPower = TransactionManager.getInstance().getVotingPower(vote.initiator);
            if(Double.isNaN(votingPower)) continue;
            votingPowerSum = VotingPowerHelper.calcVote(votingPowerSum, votingPower, vote.isUpvote);
        }
        return votingPowerSum;
    }

    // Make a decision about this pending transaction.
    // This method does not perform the actual transaction. performTransaction method should be separately called.
    public int makeDecision(UUID id) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return 0;
        return VotingPowerHelper.decide(this.calcVotingPower(id), this.getVoteCount(id));
    }

    // Perform the pending transaction and remove the transaction from the internal registry.
    // If the transaction is not registered, or errors occured while performing, it will return false. The pending transaction will not be removed from internal registry.
    // If the transaction is successfully performed, the pending transaction will be removed from internal registry and the method will return true.
    // This method uses the perform transaction method from TransactionManager.
    public boolean performTransaction(UUID id) {
        PendingDecision pendingDecision = this.storage.get(id);
        if(pendingDecision == null) return false;
        if(!TransactionManager.getInstance().performTransaction(pendingDecision.transaction)) return false;
        this.storage.remove(pendingDecision.transaction.id);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Vote Manager Debug Report: \n");
        for(UUID key : this.storage.keySet()) {
            PendingDecision decision = this.storage.get(key);
            Transaction transaction = decision.transaction;
            sb.append(key.toString()).append(" (").append(transaction.initiator.getName()).append(") ");
            sb.append(this.calcVotingPower(transaction.id)).append('\n');
            for(Vote vote : this.getVotes(transaction.id)) {
                sb.append("    ");
                sb.append(vote.id.toString()).append(": ");
                sb.append(vote.initiator.getName()).append(' ');
                if(vote.isUpvote) sb.append("+1");
                else sb.append("-1");
                sb.append('\n');
            }
        }
        if(this.storage.keySet().size() == 0)
            sb.append("Nothing in history. \n");
        return sb.toString();
    }

}

// All tests passed
