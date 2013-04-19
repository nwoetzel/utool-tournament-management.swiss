package utool.plugin.swiss.test;

import java.util.ArrayList;
import java.util.UUID;

import utool.plugin.Player;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.mocks.MockSwissTournament;
import utool.plugin.swiss.tieresolver.MatchResultTieResolver;
import android.test.AndroidTestCase;

/**
 * This class is responsible for verifying the functionality of the MatchResultTieResolver
 * @author Justin Kreier
 * @version 2/3/2013
 */
public class TestMatchResultTieResolver extends AndroidTestCase {
	
	/**
	 * Verifies the resolve tie functionality
	 */
	public void testResolveTie(){
		SwissPlayer p1 = new SwissPlayer(new Player("Test Player 1"));
		SwissPlayer p2 = new SwissPlayer(new Player("Test Player 2"));
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		
		//test when neither have played one another
		Match m1 = new Match(p1, SwissPlayer.BYE, r);
		Match m2 = new Match(p2, SwissPlayer.BYE, r);
		
		m1.setScores(2, 0);
		m2.setScores(2, 0);
		
		MatchResultTieResolver resolver = new MatchResultTieResolver();
		assertNull(resolver.resolveTie(p1, p2));
		
		
		//test when player one won one
		r = new Round(1, t);
		m1 = new Match(p1, p2, r);
		m1.setScores(2, 0);
		
		assertEquals(p1, resolver.resolveTie(p1, p2));
		
		//test when both players have beat one another
		r = new Round(2, t);
		m1 = new Match(p1, p2, r);
		m1.setScores(0, 2);
		
		assertNull(resolver.resolveTie(p1, p2));
		
		
		//test when player two has won two
		r = new Round(3, t);
		m1 = new Match(p1, p2, r);
		m1.setScores(0, 2);
		
		assertEquals(p2, resolver.resolveTie(p1, p2));
	}

}
