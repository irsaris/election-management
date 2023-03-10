package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;

import DataStructures.List.*;

/**
 * The Election class
 * 
 * Where it is decide who wins and is the new president base on the information
 * provided
 * 
 * @author: IRSARIS PEREZ
 * @version: 2.0
 * @since 2022-02-22
 */
public class Election {

	// Private Data Fields
	private int numOfCandidates = 0;
	private Candidate winner;
	private int round = 0;
	private int winnerVotes = 0;
	private int totalBallots = 0, invalidBallots = 0, blankBallots = 0, validBallots = 0;
	private ArrayList<Candidate> runningCandidates = new ArrayList<Candidate>();
	private ArrayList<Integer> eliminatedCandidates = new ArrayList<Integer>();
	private ArrayList<Integer> candidatesToEliminate = new ArrayList<Integer>();
	private ArrayList<Integer> onesAtElim = new ArrayList<Integer>();
	private ArrayList<ArrayList<Ballot>> ballotsByCandidate = new ArrayList<ArrayList<Ballot>>(5);
	private ArrayList<Candidate> candidateOutput = new ArrayList<Candidate>();

	/**
	 * Constructor for Election class.
	 * 
	 * @param ballotsFile    : File that contains the csv for all the ballots in one election               
	 * @param candidatesFile : File that contains the csv for all the candidates in one election
	 * @throws IOException   : One of the files (ballots, candidates) was not found
	 */
	public Election(File ballotsFile, File candidatesFile) throws IOException {
		try {
			scanCandidates(candidatesFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			scanBallots(ballotsFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (!(countVotes())) {
			if (eliminateCandidate(countVotesForRank(2))) {
				round++;
			}
		}
		FileWriter fw;
		try {
			fw = new FileWriter("result.txt");

			fw.write(String.format("Number of ballots: %d\n", totalBallots));
			fw.write(String.format("Number of blank ballots: %d\n", blankBallots));
			fw.write(String.format("Number of invalid ballots: %d\n", invalidBallots));
			for (int i = 0; i < round; i++) {
				if (getEliminatedCandidate(i) != null)
					fw.write(String.format("Round %d: %s was eliminated with %d #1's\n", (i + 1),
							getEliminatedCandidate(i), onesAtElim.get(i)));
			}
			fw.write(String.format("Winner: %s wins with %d #1's", winner.getCandidateName(), winnerVotes));
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the candidates from the csv and puts them into Candidate objects and
	 * lists.
	 * 
	 * @param f                      : csv file that contains all the candidates for one election
	 * @return runningCandidates     : List of all current running candidates in the election
	 * @throws FileNotFoundException : The file could not be read.
	 */
	public ArrayList<Candidate> scanCandidates(File f) throws FileNotFoundException {

		Scanner scanner = new Scanner(f); // Gets full file
		while (scanner.hasNextLine()) {
			// Gets individual line
			Scanner dataScanner = new Scanner(scanner.nextLine());
			// Gets candidate id and rank
			dataScanner.useDelimiter(",");
			String candidateName = dataScanner.next();
			int candidateId = Integer.parseInt(dataScanner.next());
			runningCandidates.add(new Candidate(candidateName, candidateId));
			numOfCandidates++;
		}
		scanner.close();
		return runningCandidates;
	}

	/**
	 * Scan and count the ballots for an election.
	 * 
	 * Discards invalid or empty ballots and adds them to the total count.
	 * 
	 * @param f                      : csv file containing all the ballots per candidate in the election
	 * @throws FileNotFoundException : The file could not be read
	 */
	public void scanBallots(File f) throws FileNotFoundException {

		String vote = "";
		Scanner scanner = new Scanner(f);

		int validRank = 1;
		boolean invalidOrBlank = false;

		// Initializes ballotsByCandidate and all its inner Lists
		for (int i = 0; i < runningCandidates.size(); i++) {
			ballotsByCandidate.add(new ArrayList<Ballot>());
		}

		// Reads csv with ballots line by line and creates/validates the ballots
		while (scanner.hasNextLine()) {
			Scanner dataScanner = new Scanner(scanner.nextLine());
			dataScanner.useDelimiter(",");

			int ballotNumber = Integer.parseInt(dataScanner.next());
			List<Integer> votes = new ArrayList<Integer>();
			// This will determine if the following token is a comma or not, in the case
			// that it is not, then the ballot is assumed to be a blank ballot.
			if (dataScanner.findInLine(",") == null) {
				totalBallots++;
				blankBallots++;
				invalidOrBlank = true;
			} else {
				while (dataScanner.hasNext()) {
					vote = dataScanner.next();
					// If the ballot is not blank then proceed.
					if (!(vote.charAt(1) == ':')) {
						totalBallots++;
						blankBallots++;
						invalidOrBlank = true;
						break;
					}

					invalidOrBlank = false;
					int rank = Integer.parseInt(vote.substring(2, 3));

					// If the rank is outside the range of candidates it is invalid
					if (!(rank == validRank && rank <= runningCandidates.size())) {
						totalBallots++;
						invalidBallots++;
						invalidOrBlank = true;
						break;
					}

					int candidateId = Integer.parseInt(vote.substring(0, 1));
					
					// Go through candidates and check if id is valid
					boolean isValidCandidate = false;
					for(Candidate c : runningCandidates) {
						if(c.getCandidateId() == candidateId){
							isValidCandidate = true;
							break;
						}
					}
					// If id is not valid, then it is an invalid ballot
					if(!isValidCandidate) {
						totalBallots++;
						invalidBallots++;
						invalidOrBlank = true;
						break;
					}
					
					validRank++;

					// If there is a repeated candidate it is invalid
					if (votes.contains(candidateId)) {
						totalBallots++;
						invalidBallots++;
						invalidOrBlank = true;
						break;
					}
					votes.add(rank - 1, candidateId);
				}
			}

			if (!(invalidOrBlank)) {
				Ballot ballot = new Ballot(ballotNumber, votes);

				// Gets the first candidate of the ballot and adds the ballot to that candidate
				ballotsByCandidate.get(ballot.getCandidateByRank(1) - 1).add(ballot);
				totalBallots++;
				validBallots++;
			}

			validRank = 1;
		}
		scanner.close();
	}

	/**
	 * Gets a candidate from the current voting round and tries to eliminate it from
	 * the election.
	 * 
	 * @param candidateId : Candidate to be eliminated
	 * @return flag determined if the candidate was eliminated
	 */
	public boolean eliminateCandidate(int candidateId) {
		if (ballotsByCandidate.size() >= candidateId) {
			List<Ballot> candidateBallots = ballotsByCandidate.get(candidateId - 1); // Gets candidate ballots
			while (!(candidateBallots.isEmpty())) {
				for (Ballot ballot : candidateBallots) {
					int removedCandidateId = ballot.getCandidateByRank(1); // Gets first candidate
					// Removes it from the ballot
					if (eliminatedCandidates.size() == 0) {
						/*
						 * If there are no eliminated candidates, just eliminate it and add the ballot
						 * without them to the next candidate in line.
						 */
						ballot.eliminate(removedCandidateId);
						if (!eliminatedCandidates.contains(removedCandidateId))
							eliminatedCandidates.add(removedCandidateId);
						ballotsByCandidate.get(ballot.getCandidateByRank(1) - 1).add(ballot);
					} else {
						/*
						 * Otherwise, go through all of the available eliminated candidates and
						 * eliminate them from the current ballot passing the ballot to the next one
						 */
						if (!eliminatedCandidates.contains(removedCandidateId))
							eliminatedCandidates.add(removedCandidateId);
						for (Integer ec : eliminatedCandidates) {
							if (!ballot.getVotes().isEmpty() && ballot.eliminate(ec)) { // Tries to eliminate first
																						// candidate, if not then
								if (!ballotsByCandidate.get(ballot.getCandidateByRank(1) - 1).contains(ballot))
									ballotsByCandidate.get(ballot.getCandidateByRank(1) - 1).add(ballot);
							}
						}
					}
				}
				candidateBallots.clear();
			}
			return true;
		}
		return false;

	}

	/**
	 * Gets the name of the eliminated candidate of the current round.
	 * 
	 * @param round : Round of the election
	 * @return string with the eliminated candidate's name
	 */
	private String getEliminatedCandidate(int round) {
		// For use with extra rounds to get the correct winners
		if (round >= eliminatedCandidates.size())
			return null;

		// Returns the candidate name for the eliminated candidate
		int eliminatedCandidate = eliminatedCandidates.get(round);

		for (Candidate c : runningCandidates) {
			if (c.getCandidateId() == eliminatedCandidate) {
				return c.getCandidateName();
			}
		}

		return null;
	}

	/**
	 * Counts the number of 1's in each of the candidates ballots.
	 * 
	 * If a winner is decided then it returns and ends the election, otherwise the
	 * elimination process occurs.
	 * 
	 * @return flag to determine if the election must proceed
	 */
	public boolean countVotes() {
		// Current candidates to eliminate starts empty
		candidatesToEliminate.clear();

		int smallestBallotSize = ballotsByCandidate.get(0).size();

		// Gets the smallest ballot size
		for (int i = 0; i < ballotsByCandidate.size(); i++) {
			if (ballotsByCandidate.get(i).size() > this.validBallots / 2) { // If they have more than 50% of votes
				int winnerId = ballotsByCandidate.get(i).get(0).getCandidateByRank(1); // Get their id
				for (Candidate c : runningCandidates) {
					if (winnerId == c.getCandidateId()) {
						winner = c;
						winnerVotes = ballotsByCandidate.get(i).size();
						return true;
					}
				}
			} else if (smallestBallotSize > ballotsByCandidate.get(i).size() && ballotsByCandidate.get(i).size() > 0) {
				smallestBallotSize = ballotsByCandidate.get(i).size();
			}

		}

		// Gets all current candidates with that ballot size
		for (int i = 0; i < ballotsByCandidate.size(); i++) {
			if (ballotsByCandidate.get(i).size() == smallestBallotSize) {
				if (!(candidatesToEliminate.contains(i))) {
					candidatesToEliminate.add(i + 1);
				}
			}
		}

		// If there is a tie and the numbers are 5-5 then return the lowest id candidate as the winner
		if (candidatesToEliminate.size() == 2) {
			if (ballotsByCandidate.get(candidatesToEliminate.get(0) - 1).size() == 5) {
				if (round >= runningCandidates.size()) {
					for (Candidate c : runningCandidates) {
						if (candidatesToEliminate.get(0) == c.getCandidateId()) {
							winner = c;
							winnerVotes = ballotsByCandidate.get(candidatesToEliminate.get(0) - 1).size();
							return true;
						}
					}
				}
			}
		}

		// Adds the amount of ones for the candidate that lost here
		onesAtElim.add(smallestBallotSize);
		return false;
	}

	/**
	 * Counts the rest of the votes for a particular rank in the case of a tie.
	 * 
	 * @param rank : rank to evaluate candidates to be eliminated on
	 * @return the id of the candidate to be eliminated
	 */
	public int countVotesForRank(int rank) {

		// If there is only one candidate, eliminate it
		if (candidatesToEliminate.size() == 1) {
			return candidatesToEliminate.get(0); // Gets the only candidate
		}

		// Otherwise, count the candidates with the most second votes
		ArrayList<Integer> votesToSecondaryRanks = new ArrayList<Integer>(candidatesToEliminate.size());
		for (int i = 0; i < candidatesToEliminate.size(); i++) {
			votesToSecondaryRanks.add(0);
		}
		for (Candidate c : runningCandidates) {
			ArrayList<Ballot> candidateBallots = ballotsByCandidate.get(c.getCandidateId() - 1); // Gets all ballot for
																									// this candidate
			for (Ballot b : candidateBallots) { // For all ballots
				if (candidatesToEliminate.contains(b.getCandidateByRank(rank))) {
					// Adds 1 to votes of this rank for this candidate
					votesToSecondaryRanks.set(candidatesToEliminate.firstIndexOf(b.getCandidateByRank(rank)),
							votesToSecondaryRanks.get(candidatesToEliminate.firstIndexOf(b.getCandidateByRank(rank)))
									+ 1);
				}
			}
		}

		// Find the least votes in the list
		int leastVotes = votesToSecondaryRanks.get(0);
		for (int i = 1; i < votesToSecondaryRanks.size(); i++) {
			if (leastVotes > votesToSecondaryRanks.get(i)) {
				leastVotes = votesToSecondaryRanks.get(i);
			}
		}

		// Remove all candidates which aren't the leastVotes (Only keep the ones with
		// the lowest ranks)
		for (int i = 0; i < votesToSecondaryRanks.size(); i++) {
			if (leastVotes != votesToSecondaryRanks.get(i)) {
				candidatesToEliminate.remove(i);
			}
		}

		// If there is still a tie, then go to the next rank available
		while (candidatesToEliminate.size() > 1) {
			if (rank <= runningCandidates.size()) {
				countVotesForRank(rank + 1);
			} else {
				int candidateToEliminate = candidatesToEliminate.get(0);
				for (Integer c : candidatesToEliminate) {
					if (candidateToEliminate < c) {
						return c;
					}
				}
			}
		}

		return candidatesToEliminate.get(0);
	}

	public static void main(String[] args) throws IOException {
		// Runs the election with specified ballots and candidates
		Election e = new Election(new File("inputFiles/ballots2.csv"), new File("inputFiles/candidates.csv"));
	}

}