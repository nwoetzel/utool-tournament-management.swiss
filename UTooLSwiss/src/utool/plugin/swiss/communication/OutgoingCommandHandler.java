package utool.plugin.swiss.communication;

import java.util.List;

import utool.plugin.Player;
import utool.plugin.activity.AbstractOutgoingCommandHandler;
import utool.plugin.activity.AbstractTournament;

/**
 * This class is a concrete outgoing commandhandler for swiss
 * @author waltzm
 * @version 1/25/2013
 */
public class OutgoingCommandHandler extends AbstractOutgoingCommandHandler{
	/**
	 * Constructor for the OutgoingCommandHandler
	 * @param tournamentLogic The tournament to associate this object with
	 */
	public OutgoingCommandHandler(AbstractTournament tournamentLogic)
	{
		super(tournamentLogic);
		this.tournament = tournamentLogic;
	}
	
	/**
	 * Handles the sending of players using a list
	 * @param id The id
	 * @param players The list of players
	 */
	public void handleSendPlayers(long id, List<Player> players){
		Player[] playerArray = players.toArray(new Player[players.size()]);
		
		super.handleSendPlayers(id, playerArray);
	}

}
