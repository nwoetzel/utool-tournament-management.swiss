package utool.plugin.swiss.test;

import java.util.ArrayList;
import java.util.UUID;

import utool.plugin.Player;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.mocks.MockCumulativeResolver;
import utool.plugin.swiss.mocks.MockSwissTournament;
import android.test.AndroidTestCase;

/**
 * Tests the functionality of the cumulative tie resolver
 * @author Justin Kreier
 * @version 2/3/2013
 */
public class TestCumulativeTieResolver extends AndroidTestCase{
	
	/**
	 * Tests the cumulative score calculation
	 */
	public void testCalculateCumulativeScore(){
		SwissPlayer p = new SwissPlayer(new Player("Test Player"));
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		Match m = new Match(p, SwissPlayer.BYE, r);
		m.setScores(2, 0);
		
		//should be 2-0
		assertEquals(2, p.calculateScore(), 0.0001);
		
		MockCumulativeResolver resolver = new MockCumulativeResolver();
		
		double result = resolver.calculateCumulativeScore(p);
		assertEquals(2, result, 0.0001);
		
		r = new Round(1,t);
		m = new Match(p, SwissPlayer.BYE, r);
		m.setScores(0, 2);
		
		//he is 2-2 now
		assertEquals(2, p.calculateScore(), 0.0001);
		
		result = resolver.calculateCumulativeScore(p);
		
		assertEquals(4, result, 0.0001);
		
		r = new Round(2, t);
		m = new Match(p, SwissPlayer.BYE, r);
		m.setScores(1, 1);
		
		//he is 3-3 now
		assertEquals(3, p.calculateScore(), 0.0001);
		
		result = resolver.calculateCumulativeScore(p);
		
		assertEquals(7, result, 0.0001);
	}
	
	/**
	 * Tests that the proper player is returned from the resolve tie method
	 */
	public void testResolveTie(){
		SwissPlayer p1 = new SwissPlayer(new Player("Test Player 1"));
		SwissPlayer p2 = new SwissPlayer(new Player("Test Player 2"));
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", UUID.randomUUID(), getContext());
		Round r = new Round(0, t);
		
		MockCumulativeResolver resolver = new MockCumulativeResolver();
		
		//p1 with higher score
		Match m1 = new Match(p1, SwissPlayer.BYE, r);
		m1.setScores(2, 0);
		
		Match m2 = new Match(p2, SwissPlayer.BYE, r);
		m2.setScores(0, 2);
		
		assertEquals(p1, resolver.resolveTie(p1, p2));
		
		//p2 with higher score
		m1.setScores(0, 2);
		m2.setScores(2, 0);
		
		assertEquals(p2, resolver.resolveTie(p1, p2));
		
		//both with same score
		m1.setScores(2, 2);
		m2.setScores(2, 2);
		
		assertNull(resolver.resolveTie(p1, p2));
	}

}
