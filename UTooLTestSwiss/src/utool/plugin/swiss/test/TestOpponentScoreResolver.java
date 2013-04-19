package utool.plugin.swiss.test;

import java.util.ArrayList;
import java.util.UUID;

import utool.plugin.Player;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.mocks.MockOpponentScoreResolver;
import utool.plugin.swiss.mocks.MockSwissTournament;
import android.test.AndroidTestCase;

/**
 * Verifies the proper functionality of the Opponent Score Tie Resolver
 * @author Justin Kreier
 * @version 2/3/2013
 */
public class TestOpponentScoreResolver extends AndroidTestCase{
	
	/**
	 * Verifies the calculate opponent score method
	 */
	public void testCalculateOpponentScore(){
		SwissPlayer p = new SwissPlayer(new Player("Test Player"));
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", UUID.randomUUID(), getContext());
		
		SwissPlayer o = new SwissPlayer(new Player("Opponent"));
		Round r = new Round(0, t);
		Match m = new Match(p, o, r);
		m.setScores(2, 0);
		
		//opponent scores are 0
		MockOpponentScoreResolver resolver = new MockOpponentScoreResolver();
		assertEquals(0, resolver.calculateOpponentScore(p), 0.0001);
		
		r = new Round(1,t);
		m = new Match(o, SwissPlayer.BYE, r);
		m.setScores(2, 0);
		m = new Match(p, SwissPlayer.BYE, r);
		m.setScores(1, 0);
		
		//opponent scores are 2
		assertEquals(2, resolver.calculateOpponentScore(p), 0.0001);
		
		r = new Round(2,t);
		m = new Match(o, SwissPlayer.BYE, r);
		m.setScores(2,0);
		
		o = new SwissPlayer(new Player("Opponent 2"));
		m = new Match(p, o, r);
		m.setScores(0,1);
		
		//opponent scores are 5
		assertEquals(5, resolver.calculateOpponentScore(p), 0.0001);
		
	}
	
	/**
	 * Verifies the opponent score resolver method
	 */
	public void testOpponentScoreResolver(){
		SwissPlayer p = new SwissPlayer(new Player("Test Player"));
		MockSwissTournament t = new MockSwissTournament(-1, new ArrayList<Player>(), "test name", UUID.randomUUID(), getContext());
		
		SwissPlayer o = new SwissPlayer(new Player("Opponent"));
		Round r = new Round(0, t);
		Match m = new Match(p, o, r);
		m.setScores(2, 0);
		
		//p's opponent score is 0, o's opponent score is 2
		MockOpponentScoreResolver resolver = new MockOpponentScoreResolver();
		assertEquals(o, resolver.resolveTie(p, o));
		
		r = new Round(1, t);
		m = new Match(p, o, r);
		m.setScores(0, 2);
		
		//both opponent scores are 2
		
		assertNull(resolver.resolveTie(p, o));
		
		
		r = new Round(2, t);
		m = new Match(p, o, r);
		m.setScores(0, 2);
		
		//p's opponent score is 4, o's opponent score is 2
		
		assertEquals(p, resolver.resolveTie(p, o));
	}

}
