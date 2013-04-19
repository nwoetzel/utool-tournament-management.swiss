package utool.plugin.swiss.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import utool.plugin.Player;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.mocks.MockSwissTournament;
import utool.plugin.swiss.roundgenerator.StandardRoundGenerator;
import android.test.AndroidTestCase;

/**
 * Test for the standard round generator
 * @author Justin Kreier
 * @version 2/2/2013
 */
public class TestStandardRoundGenerator extends AndroidTestCase {

	/**
	 * Tests the round generation for the standard round generator at round one
	 */
	public void testGenerateRoundOne(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}
		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());

		StandardRoundGenerator g = new StandardRoundGenerator();
		Round r = g.generateRound(t.getSwissPlayers(), t);

		List<Match> matches = r.getMatches();

		//check that it orders as expected with no scores set
		for (int i = 0; i < matches.size(); i++){
			Match m = matches.get(i);

			assertEquals(players.get(i), m.getPlayerOne());
			assertEquals(players.get(i+8), m.getPlayerTwo());
		}
	}

	/**
	 * Tests that players with the closest score are paired together
	 */
	public void testClosestScore(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}

		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		List<SwissPlayer> swissPlayers = t.getSwissPlayers();
		//give half a score of 4
		for (int i = 0; i < 8; i++){
			SwissPlayer p = swissPlayers.get(i);
			Match m = new Match(p, SwissPlayer.BYE, r);
			m.setScores(4, 0);
			r.addMatch(m);
		}
		//and half a score of 2
		for (int i = 8; i < 16; i++){
			SwissPlayer p = swissPlayers.get(i);
			Match m = new Match(p, SwissPlayer.BYE, r);
			m.setScores(2, 0);
			r.addMatch(m);
		}

		//add the round
		t.addRound(r);

		//calculate the scores for each player
		for (int i = 0; i < swissPlayers.size(); i++){
			swissPlayers.get(i).calculateScore();
		}

		StandardRoundGenerator g = new StandardRoundGenerator();

		r = g.generateRound(swissPlayers, t);

		List<Match> matches = r.getMatches();
		//make sure the 4's are playing one another
		for(int i = 0; i < 4; i++){
			Match m = matches.get(i);
			assertEquals(players.get(i), m.getPlayerOne());
			assertEquals(players.get(i+4), m.getPlayerTwo());
		}

		//make sure the 2's are playing one another
		for(int i = 4; i < 8; i++){
			Match m = matches.get(i);
			assertEquals(players.get(i+4), m.getPlayerOne());
			assertEquals(players.get(i+8), m.getPlayerTwo());
		}
	}

	/**
	 * Verifies that players will play against the least played opponent in their group
	 */
	public void testLeastPlayed(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}

		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		List<SwissPlayer> swissPlayers = t.getSwissPlayers();
		//give everyone the same score
		for (int i = 0; i < 8; i++){
			SwissPlayer p = swissPlayers.get(i);
			SwissPlayer o = swissPlayers.get(i+8);
			Match m = new Match(p, o, r);
			m.setScores(4, 4); //same score
			r.addMatch(m);
		}
		//add the round
		t.addRound(r);

		//calculate the scores for each player
		for (int i = 0; i < swissPlayers.size(); i++){
			swissPlayers.get(i).calculateScore();
		}

		StandardRoundGenerator g = new StandardRoundGenerator();

		r = g.generateRound(swissPlayers, t);
		List<Match> matches = r.getMatches();
		//verify that the same matchups do not occur
		//check that it orders as expected with no scores set
		for (int i = 0; i < matches.size(); i++){
			Match m = matches.get(i);

			if (players.get(i).equals(m.getPlayerOne()) && players.get(i+8).equals(m.getPlayerTwo())){
				fail("Duplicate match occurred: "+m.getPlayerOne().getName()+" vs "+m.getPlayerTwo().getName());
			}
		}
	}
	
	/**
	 * Makes sure that the least played algorithm works at large numbers of players
	 */
	public void testExhaustiveNumberLeastPlayed(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 256; i++){
			players.add(new Player("Player "+i));
		}

		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		List<SwissPlayer> swissPlayers = t.getSwissPlayers();
		//give everyone the same score
		for (int i = 0; i < 128; i++){
			SwissPlayer p = swissPlayers.get(i);
			SwissPlayer o = swissPlayers.get(i+128);
			Match m = new Match(p, o, r);
			m.setScores(4, 4); //same score
			r.addMatch(m);
		}
		//add the round
		t.addRound(r);

		//calculate the scores for each player
		for (int i = 0; i < swissPlayers.size(); i++){
			swissPlayers.get(i).calculateScore();
		}

		StandardRoundGenerator g = new StandardRoundGenerator();

		r = g.generateRound(swissPlayers, t);
		List<Match> matches = r.getMatches();
		//verify that the same matchups do not occur
		//check that it orders as expected with no scores set
		for (int i = 0; i < matches.size(); i++){
			Match m = matches.get(i);

			if (players.get(i).equals(m.getPlayerOne()) && players.get(i+128).equals(m.getPlayerTwo())){
				fail("Duplicate match occurred: "+m.getPlayerOne().getName()+" vs "+m.getPlayerTwo().getName());
			}
		}
	}
}
