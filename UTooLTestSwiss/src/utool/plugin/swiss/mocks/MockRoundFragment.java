package utool.plugin.swiss.mocks;

import android.util.Log;
import utool.plugin.swiss.RoundFragment;

public class MockRoundFragment extends RoundFragment{
	
	public long getTid()
	{
		return tid;
	}
	
	public void setTID(long tid)
	{
		this.tid=tid;
	}
	
	public int getRound()
	{
		return round;
	}
	
	public void setRound(int round)
	{
		this.round=round;
	}
	
	public int getSelectedPlayerIndex()
	{
		return selectedPlayerIndex;
	}
	
	public void setSelectedPlayerIndex(int selectedPlayerIndex)
	{
		this.selectedPlayerIndex=selectedPlayerIndex;
	}
	
	public boolean getIsSelP1()
	{
		return isSelP1;
	}
	
	public void setIsSelP1(boolean isSelP1)
	{
		this.isSelP1=isSelP1;
	}

	/**
	 * Performs needed action if a second player is selected (the switch)
	 * @param pos the position of the second player
	 * @param p1 if the second player is p1
	 */
	public void onSecondPlayerClick(int pos, boolean p1)
	{
		super.onSecondPlayerClick(pos, p1);
	}
	
	public void doSwitch(int pos, boolean p1)
	{
		super.doSwitch(pos, p1);
	}
}
