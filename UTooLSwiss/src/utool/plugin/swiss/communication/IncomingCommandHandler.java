package utool.plugin.swiss.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import android.util.Log;

import utool.plugin.Player;
import utool.plugin.activity.AbstractIncomingCommandHandler;
import utool.plugin.activity.AbstractTournament;
import utool.plugin.swiss.Match;
import utool.plugin.swiss.MatchResult;
import utool.plugin.swiss.Round;
import utool.plugin.swiss.SwissConfiguration;
import utool.plugin.swiss.SwissPlayer;
import utool.plugin.swiss.SwissTournament;
import utool.plugin.swiss.TournamentActivity;

/**
 * Implementation of the AbstractIncomingCommandHandler for the Swiss System tournament.
 * @author Justin Kreier
 * @version 2/7/2013
 */
public class IncomingCommandHandler extends AbstractIncomingCommandHandler{

	/**
	 * True if the handler believes it is out of sync and waiting for an error 
	 */
	private boolean inErrorState;

	/**
	 * Code for if the clear is being sent as part of error code handling
	 */
	public static final long RESEND_CLEAR = 235;

	/**
	 * Required constructor
	 * @param t The tournament it is acting on
	 */
	public IncomingCommandHandler(AbstractTournament t) {
		super(t);
		inErrorState = false;
	}

	@Override
	public void handleReceiveMatchup(long id, long matchid, String team1name, String team2name, ArrayList<String> team1, ArrayList<String> team2, int round, String table){
		super.handleReceiveMatchup(id, matchid, team1name, team2name, team1, team2, round, table);

		if (t.getPermissionLevel() == Player.PARTICIPANT || t.getPermissionLevel() == Player.MODERATOR){
			if (!inErrorState){
				try{
					SwissTournament swiss = (SwissTournament)t;

					//get the round the match is meant for
					Round r = swiss.getRounds().get(round);

					//retrieve the local players from the tournament
					List<SwissPlayer> players = swiss.getSwissPlayers();
					UUID p1 = UUID.fromString(team1.get(0));
					UUID p2 = UUID.fromString(team2.get(0));


					SwissPlayer playerOne = null;
					SwissPlayer playerTwo = null;

					if (p1.equals(SwissPlayer.BYE.getUUID())){
						playerOne = SwissPlayer.BYE;
					}
					if (p2.equals(SwissPlayer.BYE.getUUID())){
						playerTwo = SwissPlayer.BYE;
					}

					for (SwissPlayer p : players){
						if (p.getUUID().equals(p1)){
							playerOne = p;
						}
						if (p.getUUID().equals(p2)){
							playerTwo = p;
						}
						if (playerOne != null && playerTwo != null){
							break;
						}
					}

					Match m = new Match(playerOne, playerTwo, r);
					r.addMatch(m);

				} catch (Exception e){
					Log.e("IncomingCommandHandler", "Exception in handleReceiveMatchup", e);
					sendError(TournamentActivity.RESEND_ERROR_CODE);
				}
			}
		}
	}

	@Override
	public void handleReceiveBeginNewRound(long id, int round){
		super.handleReceiveBeginNewRound(id, round);

		if (t.getPermissionLevel() == Player.PARTICIPANT || t.getPermissionLevel() == Player.MODERATOR){
			if (!inErrorState){
				try{
					SwissTournament swiss = (SwissTournament)t;
					List<Round> rounds = swiss.getRounds();

					if (round == rounds.size()){
						//means we're adding the correct round
						rounds.add(new Round(round,swiss));

						//restart round timer
						if(((SwissTournament) t).getSwissConfiguration().getStartTimerOnRoundChange())
						{
							Log.e("INC","Starting timer for new round");
							((SwissTournament) t).getSwissConfiguration().startTimer();
						}

					} else {
						//something got messed up, throw an exception and panic
						throw new RuntimeException("Tournament desynchronization in handleSendBeginNewRound");
					}
				} catch (Exception e){
					Log.e("IncomingCommandHandler", "Exception in handleReceiveBeginNewRound", e);
					sendError(TournamentActivity.RESEND_ERROR_CODE);
				}
			}
		}
	}

	@Override
	public void handleReceiveScore(long id, long matchid, String team1name, String team2name, String score1, String score2, int round){
		super.handleReceiveScore(id, matchid, team1name, team2name, score1, score2, round);

		if (t.getPermissionLevel() == Player.PARTICIPANT || t.getPermissionLevel() == Player.MODERATOR){
			if (!inErrorState){
				try{
					SwissTournament swiss = (SwissTournament)t;
					//get the round and match
					Round r = swiss.getRounds().get(round);
					Match m = r.getMatches().get((int)matchid);

					m.setScores(Double.parseDouble(score1), Double.parseDouble(score2));

				} catch (Exception e){
					Log.e("IncomingCommandHandler", "Exception in handleReceiveScore", e);
					sendError(TournamentActivity.RESEND_ERROR_CODE);
				}
			}
		}
	}

	@Override
	public void handleReceiveClear(long tid){
		super.handleReceiveClear(tid);
		if (tid == RESEND_CLEAR){
			inErrorState = false;
		}
		if (t.getPermissionLevel() == Player.PARTICIPANT || t.getPermissionLevel() == Player.MODERATOR){
			if (!inErrorState){
				SwissTournament swiss = (SwissTournament)t;
				swiss.clearTournament();
			}
		}
	}

	@Override
	public void handleReceiveError(long id, String playerid, String name, String message){
		super.handleReceiveError(id, playerid, name, message);
		if (id == TournamentActivity.RESEND_ERROR_CODE && t.getPermissionLevel() == Player.HOST){
			OutgoingCommandHandler out = new OutgoingCommandHandler(t);
			out.handleSendClear(RESEND_CLEAR);
			out.handleSendPlayers(-1, t.getPlayers());

			SwissTournament swiss = (SwissTournament)t;

			List<Round> rounds = swiss.getRounds(); 

			//for each round
			for (Round r : rounds){
				//send new round
				out.handleSendBeginNewRound(-1, r.getRoundNumber());

				List<Match> matches = r.getMatches();

				//for each match in round
				for (int i = 0; i < matches.size(); i++){
					Match m = matches.get(i);

					SwissPlayer playerOne = m.getPlayerOne();
					SwissPlayer playerTwo = m.getPlayerTwo();

					String[] team1 = new String[1];
					String[] team2 = new String[1];

					team1[0] = playerOne.getUUID().toString();
					team2[0] = playerTwo.getUUID().toString();

					//send new match
					out.handleSendMatchup(-1, i, null, null, team1, team2, r.getRoundNumber(), null);

					//send match score
					if (m.getMatchResult() != MatchResult.UNDECIDED){
						out.handleSendScore(0, i, playerOne.getUUID().toString(), playerTwo.getUUID().toString(), m.getPlayerOneScore(), m.getPlayerTwoScore(), r.getRoundNumber());
					}
				}
			}
		}
	}

	@Override
	public void handleReceivePlayers(String id, ArrayList<Player> players){
		super.handleReceivePlayers(id, players);
		if (t.getPermissionLevel() == Player.PARTICIPANT || t.getPermissionLevel() == Player.MODERATOR){
			if (!inErrorState){
				t.setPlayers(SwissTournament.playerListToSwissPlayers(players));
			}
		}
	}

	/**
	 * Responsible for sending an error with an error code to the receiver
	 * @param errorCode The error code to send
	 */
	private void sendError(int errorCode){
		inErrorState = true;
		OutgoingCommandHandler out = new OutgoingCommandHandler(t);
		out.handleSendError(errorCode, "", "", "");
	}

	/**
	 * Notifies the plugin of the time limit for each round, beginning at the next round message.
	 * If any of the times are negative, indicates no round timer.
	 * @param milliElapsedSoFar of the tournament
	 * @param time the time in hh:mm:ss
	 */
	public void handleReceiveRoundTimerAmount(long milliElapsedSoFar, String time)
	{
		super.handleReceiveRoundTimerAmount(milliElapsedSoFar, time);

		SwissConfiguration config = ((SwissTournament)t).getSwissConfiguration();
		StringTokenizer s = new StringTokenizer(time,":");
		if(s.countTokens()!=3)
		{
			//time is malformed
			Log.e("Round Timer Inc","Time received is malformed: "+time);
			return;
		}
		try
		{
			int hour = Integer.parseInt(s.nextToken());
			int min = Integer.parseInt(s.nextToken());
			int sec = Integer.parseInt(s.nextToken());

			config.setRoundTimerSeconds(hour*60*60+min*60+sec);

			config.startTimer(milliElapsedSoFar);

			((SwissTournament)t).notifyChanged();
		}
		catch(Exception e)
		{
			//number formating went wrong, don't do anything
		}
	}
}
