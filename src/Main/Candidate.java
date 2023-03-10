package Main;

/**
 * The Candidate Running for President
 * 
 * @author IRSARIS PEREZ
 * @version 2.0
 * @since 2022-02-22
 */
public class Candidate {
	
	// Private Data fields
	private String candidateName;
	private int candidateID;

	/**
	 * Initializes a Candidate object with all properties specified.
	 * 
	 * @param candidateName : The candidate's name
	 * @param candidateId   : The candidate's identification data
	 */
	public Candidate(String candidateName, int candidateID) {
		this.candidateName = candidateName;
		this.candidateID = candidateID;
	}

	// Returns the candidate's name
	public String getCandidateName() { return candidateName; }
	
	// Returns the candidate's identification data
	public int getCandidateId() { return candidateID; }
	
}
