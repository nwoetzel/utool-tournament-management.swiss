package utool.plugin.swiss.test;

import java.util.ArrayList;

import utool.plugin.Player;
import utool.plugin.activity.AbstractPluginCommonActivity;
import utool.plugin.activity.TournamentContainer;
import utool.plugin.swiss.OptionsEmailTab;
import utool.plugin.swiss.OptionsTournamentTab;
import utool.plugin.swiss.R;
import utool.plugin.swiss.SwissTournament;
import utool.plugin.swiss.TournamentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

/**
 * Tests the tournament tab
 * @author waltzm
 * @version 2/2/2013
 */
public class TestSwissOptionsTournamentTab extends ActivityUnitTestCase<OptionsTournamentTab>{

	/**
	 * holds the tournament id
	 */
	private long tournamentId = 123;

	/**
	 * Required constructor for Activity Tests
	 * @since 10/11/2012
	 */
	public TestSwissOptionsTournamentTab() {
		super(OptionsTournamentTab.class);
	}

	/**
	 * The activity under test
	 */
	private OptionsTournamentTab mActivity;

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

		//ties
		ListView l = (ListView)mActivity.findViewById(R.id.tie_list_view);
		assertNotNull(l);
		assertEquals(4,l.getAdapter().getCount());
		
		//matching
		RadioButton r = (RadioButton)mActivity.findViewById(R.id.closest_score);
		assertEquals(true,r.isChecked());
		RadioButton r2 = (RadioButton)mActivity.findViewById(R.id.new_opponent);
		assertEquals(false,r2.isChecked());
		
		//scoring
		EditText w = (EditText)mActivity.findViewById(R.id.points_awarded_win);
		assertEquals("1.0",w.getText().toString());
		EditText t = (EditText)mActivity.findViewById(R.id.points_awarded_tie);
		assertEquals("0.5",t.getText().toString());
		EditText ll = (EditText)mActivity.findViewById(R.id.points_awarded_loss);
		assertEquals("0.0",ll.getText().toString());
		
		//rounds
		EditText nr = (EditText)mActivity.findViewById(R.id.number_of_rounds);
		assertEquals("2",nr.getText().toString());
	}
}