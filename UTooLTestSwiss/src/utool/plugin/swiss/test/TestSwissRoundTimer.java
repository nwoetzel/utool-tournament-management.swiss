package utool.plugin.swiss.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import utool.plugin.Player;
import utool.plugin.swiss.SwissConfiguration;

/**
 * Tests the roudn timer
 * @author waltzm
 * @version 3/17/2013
 */
public class TestSwissRoundTimer extends TestCase{

		/**
		 * Holds a reference to the config
		 */
		SwissConfiguration config;

		/**
		 * Holds first time users
		 */
		boolean firstTime = true;

		//This method is invoked before every test
		@Override
		protected void setUp() throws Exception
		{
			super.setUp();
			ArrayList<Player> p = new ArrayList<Player>();
			p.add(new Player("John"));
			p.add(new Player("John"));
			p.add(new Player("John"));
			p.add(new Player("John"));
			
			config= new SwissConfiguration(p, null);
			
			//test constructor and getters
			assertEquals(config.getStartTimerOnRoundChange(), false);
			assertEquals(config.getSecondsRemaining(), -1);
			assertEquals(config.getRoundTimerSeconds(), 50*60);
			
		}

		/**
		 * Tests that the start timer works as intended
		 */
		public void testStartTimer() 
		{
			//Testing setRoundTimerSeconds
			int time = 20*60+45;
			config.setRoundTimerSeconds(time);
			assertEquals(config.getRoundTimerSeconds(), time);
			
			//Testing getSecondsRemaining
			assertEquals(config.startTimer(), true);
			assertEquals(config.startTimer(), false);
			assertEquals(config.getSecondsRemaining(), 20*60+45);
		}
		
		/**
		 * Tests that the start timer with a delay works as intended
		 */
		public void testStartTimerDelay() 
		{
			//Testing setRoundTimerSeconds
			int time = 20*60+45;
			config.setRoundTimerSeconds(time);
			assertEquals(config.getRoundTimerSeconds(), time);
			
			//Testing getSecondsRemaining
			//assertEquals(config.startTimer(60*5), true);
			assertEquals(config.startTimer(60*20*1000), true);
			assertEquals(config.getSecondsRemaining(), 45);
		}
		
		/**
		 * Tests that setters and getters work
		 */
		public void testSettersGettersRoundTimer() 
		{
			//Testing setRoundTimerSeconds
			int time = 20*60+45;
			config.setRoundTimerSeconds(time);
			assertEquals(config.getRoundTimerSeconds(), time);
			
			//Testing getSecondsRemaining
			assertEquals(config.getSecondsRemaining(), -1);
			
			config.startTimer();
			assertEquals(config.getSecondsRemaining(), 20*60+45);
						
		}
}
