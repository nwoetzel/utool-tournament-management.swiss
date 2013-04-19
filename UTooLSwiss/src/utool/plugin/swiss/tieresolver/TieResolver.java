package utool.plugin.swiss.tieresolver;

import utool.plugin.swiss.SwissPlayer;

/**
 * This interface defines a method responsible for resolving ties in a Swiss System
 * Tournament. There are multiple accepted methods of resolving a tie, so the Strategy
 * Pattern is being incorporated in order to meet the application needs.
 * @author Justin Kreier
 * @version 1/21/2013
 */
public interface TieResolver {

	/**
	 * Returns the player which would win as a result of the implemented. If after
	 * calculating the tie break, the players are still tied, the return value will be null.
	 * tie breaking algorithm
	 * @param p1 The first player being compared
	 * @param p2 The second player being compared
	 * @return The winner of the tie breaking, or null if the tie break is null
	 */
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2);
	
}
