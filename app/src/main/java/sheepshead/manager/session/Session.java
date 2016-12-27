package sheepshead.manager.session;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;

/**
 * Created by Nicolas on 06.10.2016.
 */

public class Session {

    private List<Player> players;

    private List<SingleGameResult> savedGames;

    private Stake sessionStake;

    public Session(List<Player> allPlayers, Stake stake) {
        if (allPlayers.size() < SingleGameResult.PLAYERS_PER_GAME) {
            throw new IllegalArgumentException("The playerlist must contain at least " + SingleGameResult.PLAYERS_PER_GAME + " players");
        }
        players = allPlayers;
        sessionStake = stake;
        savedGames = new LinkedList<>();
    }

    public void addGame(SingleGameResult game) {
        savedGames.add(game);
        game.calculate(sessionStake);
        //update players money
        for (PlayerRole role : game.getParticipants()) {
            int delta = role.getMoney();
            Player player = role.getPlayer();
            player.setSessionMoney(player.getSessionMoney() + delta);
        }
    }

    public int getGameAmount() {
        return savedGames.size();
    }

    public Collection<Player> getPlayers() {
        return players;
    }

}
