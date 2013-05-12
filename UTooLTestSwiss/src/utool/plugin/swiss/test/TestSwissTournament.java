package utool.plugin.swiss.test;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import utool.plugin.Player;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.mocks.MockSwissTournament;
import android.test.AndroidTestCase;

/**
 * This class is meant to verify proper functionality of the Swiss Tournament Class
 * @author Justin Kreier
 * @version 1/25/2013
 */
public class TestSwissTournament extends AndroidTestCase{

	/**
	 * Verifies the constructor builds properly
	 */
	public void testConstructor(){

		//sunny day
		UUID testUUID = UUID.randomUUID();
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", testUUID, getContext());

		assertEquals(-1, t.getTournamentId());
		assertNotNull(t.getPlayers());
		assertEquals("test name", t.getTournamentName());
		assertEquals(testUUID, t.getPID());

		assertNotNull(t.getRounds());
		assertNotNull(t.getObservable());
		assertNotNull(t.getAutomaticMessageHandler());
		assertNotNull(t.getSwissConfiguration());


		//null values
		try{
			t = new MockSwissTournament(-1, null, "test name", testUUID, getContext());
			fail("Players cannot be null");
		} catch(NullPointerException e) {
			//expected
		}

		try{
			t = new MockSwissTournament(-1, new ArrayList<Player>(), null, testUUID, getContext());
			fail("Tournament Name cannot be null");
		} catch(NullPointerException e) {
			//expected
		}

		try{
			t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", null, getContext());
			fail("PID cannot be null");
		} catch(NullPointerException e) {
			//expected
		}

		try{
			t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", testUUID, null);
			fail("Context cannot be null");
		} catch(NullPointerException e) {
			//expected
		}
	}

	/**
	 * Tests that the generate round function properly differentiates between the first
	 * round and other rounds
	 */
	public void testGenerateNextRound(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}
		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());

		//generate the first round (should be randomized)
		Round r = t.generateNextRound();
		
		assertEquals(8, r.getMatches().size());

		List<SwissPlayer> swissPlayers = t.getSwissPlayers();

		int errors = 0;
		for (int i = 0; i < 8; i++){
			Match m = r.getMatches().get(i);
			if (swissPlayers.get(i).equals(m.getPlayerOne())){
				errors++;
			}
		}

		for (int i = 0; i < 8; i++){
			Match m = r.getMatches().get(i);
			if (swissPlayers.get(i+1).equals(m.getPlayerTwo())){
				errors++;
			}
		}

		if (errors > 12){
			fail("There is a statistically significant chance that the list was not randomized, " +
					"verify that this failure is consistent");
		}

		//generate the second round (should not be randomized)
		r = t.generateNextRound();
		List<Match> matches = r.getMatches();
		assertEquals(8, matches.size());
		
		errors = 0;
		//check that it is mostly in order
		for (int i = 0; i < matches.size(); i++){
			Match m = matches.get(i);
			
			if(!players.get(i).equals(m.getPlayerOne())){
				errors++;
			}
			if(!players.get(i+8).equals(m.getPlayerTwo())){
				errors++;
			}
		}
		
		if (errors > 12){
			fail("There is a statistically significant chance that the list was not left in order");
		}
	}
	
	/**
	 * Verifies the getSwissPlayers method returns the correct list of players
	 */
	public void testGetSwissPlayers(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}
		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());
		
		List<SwissPlayer> swissPlayers = t.getSwissPlayers();
		for (int i = 0; i < swissPlayers.size(); i++){
			assertEquals(players.get(i), swissPlayers.get(i));
		}
	}
	
	/**
	 * Tests that the standings array returns a list of players sorted by their score
	 */
	public void testGetStandingsArray(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 16; i++){
			players.add(new Player("Player "+i));
		}
		MockSwissTournament t = new MockSwissTournament(-1, players, "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		
		//set up the matches so the standings array should be in reverse order
		List<SwissPlayer> swissPlayers = t.getSwissPlayers();
		for (int i = 0; i < swissPlayers.size(); i++){
			SwissPlayer p = swissPlayers.get(i);
			
			Match m = new Match(p, SwissPlayer.BYE, r);
			m.setScores(2*i, 0);
		}
		
		List<SwissPlayer> standingArray = t.getStandingsArray();
		assertEquals(16, standingArray.size());
		for (int i = 0; i < standingArray.size(); i++){
			SwissPlayer p = standingArray.get(i);
			SwissPlayer o = swissPlayers.get(15-i);
			
			assertEquals(o, p);
		}
	}
	
	
}
