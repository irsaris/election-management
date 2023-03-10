package Main;

/**
 * Interface for the Ballots.
 */
public interface BaseBallot {
	
	// Returns the ballot number
	public int getBallotNum(); 
	
	// Returns the rank for that candidate
	public int getRankByCandidate(int candidateID); 
	
	// Returns the candidate with that rank
	public int getCandidateByRank(int rank); 
	
	// Eliminates a candidate
	public boolean eliminate(int candidateID); 
	
}
