package utool.plugin.swiss.tieresolver;

import java.util.List;

import utool.plugin.swiss.SwissPlayer;

/**
 * This tie resolver is capable of resolving ties in multiple different methods,
 * so that if the first method used cannot break the tie, then the next method
 * can
 * @author Justin Kreier
 * @version 1/26/2013
 */
public class MultipleTieResolver implements TieResolver{

	/**
	 * The ordered list of resolvers
	 */
	private List<TieResolver> tieResolvers;
	
	/**
	 * Constructor that takes an ordered list of resolvers
	 * @param resolvers The list of resolvers in the order they should resolve ties
	 */
	public MultipleTieResolver(List<TieResolver> resolvers){
		tieResolvers = resolvers;
	}
	
	@Override
	public SwissPlayer resolveTie(SwissPlayer p1, SwissPlayer p2) {
		SwissPlayer ret = null;
		for(TieResolver t : tieResolvers){
			ret = t.resolveTie(p1, p2);
			if (ret != null){
				break;
			}
		}
		return ret;
	}

}
