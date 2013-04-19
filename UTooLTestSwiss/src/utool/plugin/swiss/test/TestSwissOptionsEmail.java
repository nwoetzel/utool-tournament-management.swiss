package utool.plugin.swiss.test;

import java.util.ArrayList;
import utool.plugin.Player;
import utool.plugin.activity.AbstractPluginCommonActivity;
import utool.plugin.activity.TournamentContainer;
import utool.plugin.swiss.OptionsEmailTab;
import utool.plugin.swiss.R;
import utool.plugin.swiss.SwissTournament;
import utool.plugin.swiss.TournamentActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * This test class is meant to fully test the functionality of the Swiss Option Email Tab
 * @author Maria
 * @version 2/2/13
 */
@TargetApi(15)
public class TestSwissOptionsEmail extends ActivityUnitTestCase<OptionsEmailTab>{

	/**
	 * holds the tournament id
	 */
	private long tournamentId = 10;

	/**
	 * Required constructor for Activity Tests
	 * @since 10/11/2012
	 */
	public TestSwissOptionsEmail() {
		super(OptionsEmailTab.class);
	}

	/**
	 * The activity under test
	 */
	private OptionsEmailTab mActivity;

	@Override
	protected void setUp() throws Exception{
		super.setUp();

		//clear application data
		AndroidTestHelperMethods.clearApplicationData(getInstrumentation().getTargetContext());


		Player local = new Player("Profile");
		Player p1 = new Player("Bob");
		p1.setPermissionsLevel(Player.PARTICIPANT);
		Player p2 = new Player("Tim");
		//make a list of players
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(p2);
		players.add(p1);
		players.add(local);

		//start the activity
		TournamentContainer.putInstance(new SwissTournament(tournamentId, players, "t1", local.getUUID(), new TournamentActivity(),this.getInstrumentation().getTargetContext()));

	}


	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
		
		//clear application data
		AndroidTestHelperMethods.clearApplicationData(getInstrumentation().getTargetContext());
	}

	/**
	 * Tests that the various components are initialized properly
	 */
	public void testInitialization() 
	{		
		assertTrue(TournamentContainer.getInstance(tournamentId)!=null);
		assertTrue(((SwissTournament)TournamentContainer.getInstance(tournamentId)).getAutomaticEmailHandler()!=null);
		Intent i = new Intent(getInstrumentation().getTargetContext(), OptionsEmailTab.class);
		i.setClassName(AbstractPluginCommonActivity.UTOOL_TOURNAMENT_CONFIG_PACKAGE, AbstractPluginCommonActivity.UTOOL_TOURNAMENT_CONFIG_CLASS);
		i.putExtra(AbstractPluginCommonActivity.UTOOL_TOURNAMENT_ID_EXTRA_NAME, tournamentId);
		mActivity = startActivity(i, (Bundle)null, (Object)null);

		assertTrue(mActivity!=null);

		ListView l = (ListView)mActivity.findViewById(R.id.email_subscribers);
		assertNotNull(l);

		//no emails should be in list
		assertEquals(0,l.getAdapter().getCount());
		
	}
	
	/**
	 * Tests that an email is successfully added
	 */
	public void testEmailsInList() 
	{		
		Intent i = new Intent(getInstrumentation().getTargetContext(), OptionsEmailTab.class);
		i.setClassName(AbstractPluginCommonActivity.UTOOL_TOURNAMENT_CONFIG_PACKAGE, AbstractPluginCommonActivity.UTOOL_TOURNAMENT_CONFIG_CLASS);
		i.putExtra(AbstractPluginCommonActivity.UTOOL_TOURNAMENT_ID_EXTRA_NAME, tournamentId);
		mActivity = startActivity(i, (Bundle)null, (Object)null);
		
		EditText e = (EditText)mActivity.findViewById(R.id.email_address);
		e.setText("lajdlasd.com");
		
		ImageButton b = (ImageButton)mActivity.findViewById(R.id.email_plus);
		assertTrue(b.callOnClick());
		
		ListView l = (ListView)mActivity.findViewById(R.id.email_subscribers);
		assertNotNull(l);
		assertEquals(1,l.getAdapter().getCount());
		
	}

}