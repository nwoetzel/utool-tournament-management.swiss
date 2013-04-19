package utool.plugin.swiss.test;

import java.util.ArrayList;

import utool.plugin.swiss.communication.AutomaticEmailHandler;
import junit.framework.TestCase;

/**
 * Tests the automatic email handler
 * @author waltzm
 * @version 1/20/2013
 */
public class TestAutomaticEmailHandler extends TestCase{



	/**
	 * holds tid
	 */
	private long tournamentId =2390;

	/**
	 * Holds a reference to the OOT
	 */
	AutomaticEmailHandler aeh;

	/**
	 * Holds first time users
	 */
	boolean firstTime = true;

	//This method is invoked before every test
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		aeh = new AutomaticEmailHandler(tournamentId);
		
		//test constructor
		assertEquals(aeh.getPossibleSubscribers().size(), 0);
		assertEquals(aeh.getSubscribers().size(), 0);
	}

	/**
	 * Tests that the set and get subscribers works
	 */
	public void testSetSubscribers() 
	{
		ArrayList<String> subs = new ArrayList<String>();
		subs.add("Ben");
		subs.add("Randy");

		aeh.setSubscribers(subs);
		assertEquals(aeh.getSubscribers(),subs);

		//make sure safe from mutation
		subs.add("Tom");
		assertNotSame(aeh.getSubscribers(),subs);

		//make sue set works after adding
		aeh.setSubscribers(subs);
		assertEquals(aeh.getSubscribers(),subs);

	}

	/**
	 * Tests that the set and get possible subscribers works
	 */
	public void testSetPossibleSubscribers() 
	{
		ArrayList<String> subs = new ArrayList<String>();
		subs.add("Ben");
		subs.add("Randy");

		aeh.setPossibleSubscribers(subs);
		assertEquals(aeh.getPossibleSubscribers(),subs);

		//make sure safe from mutation
		subs.add("Tom");
		assertNotSame(aeh.getPossibleSubscribers(),subs);

		//make sue set works after adding
		aeh.setPossibleSubscribers(subs);
		assertEquals(aeh.getPossibleSubscribers(),subs);

	}




}
