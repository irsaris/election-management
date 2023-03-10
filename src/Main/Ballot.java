package Main;
import DataStructures.List.*;

/**
 * The Ballot
 * 
 * Stores all the votes casted by one person who votes in the Poor Harbor Election
 * 
 * @author: IRSARIS PEREZ
 * @version: 2.0
 * @since: 2022-02-22
 */
public class Ballot implements BaseBallot {

	//Private Data Fields
	private int ballotNum;
	private List<Integer> votes;
	
	/**
	 * Initializes a Ballot object with all properties specified.
	 * 
	 * @param ballotNum : The ballot number
	 * @param votes     : List of votes in the ballot per candidate
	 */
	public Ballot(int ballotNum, List<Integer> votes) {
		this.ballotNum = ballotNum;
		this.votes = votes;
	}
	
	// Returns the List of votes
	public List<Integer> getVotes() { return votes;	}
	
	// Returns the ballot number
	@Override
	public int getBallotNum() { return ballotNum; }

	/**
	 * Gets the rank for a specific candidate in the ballot.
	 * 
	 * @param candidateID : The id of the candidate to request from votes in ballot
	 */
	@Override
	public int getRankByCandidate(int candidateID) {
		for(int i = 0; i < getVotes().size(); i++) {
			if(candidateID == getVotes().get(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the candidate for a specified rank in the ballot.
	 * 
	 * @param rank : The rank of the candidate requested
	 */
	@Override
	public int getCandidateByRank(int rank) {
		return getVotes().get(rank-1);
	}

	/**
	 * If the candidate is the first rank in the ballot, eliminate it.
	 * 
	 * @param candidateID : Candidate to eliminate
	 * @return flag determined if the candidate was eliminated
	 */
	@Override
	public boolean eliminate(int candidateID) {
		if(candidateID == this.getVotes().get(0)) {
			this.getVotes().remove(0);
			return true;
		}
		else return false;
	}

}