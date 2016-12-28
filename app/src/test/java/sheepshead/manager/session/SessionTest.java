package sheepshead.manager.session;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SessionTest {

    private Session createDefaultSession(Player[] players) {
        return createSession(players, new Stake(10, 50, 10));
    }

    private Session createSession(Player[] players, Stake stake) {
        Session session = new Session(Arrays.asList(players), stake);
        assertEquals(0, session.getGameAmount());
        for (Player p : session.getPlayers()) {
            boolean found = false;
            for (int i = 0; i < players.length && !found; i++) {
                found = players[i].equals(p);
            }
            if (!found) {
                fail("Player " + p.getName() + " was not added to session");
            }

            assertEquals(0, p.getSessionMoney());
        }
        return session;
    }

    private SingleGameResult createGame(Player[] callers, Player[] nonCallers, GameType type, boolean callersWon, StakeModifier modifier) {
        List<PlayerRole> roles = new ArrayList<>(callers.length + nonCallers.length);
        for (Player caller : callers) {
            roles.add(new PlayerRole(caller, true, callersWon));
        }
        for (Player nonCaller : nonCallers) {
            roles.add(new PlayerRole(nonCaller, false, !callersWon));
        }
        return new SingleGameResult(roles, type, modifier);
    }

    private void testMoney(int[] expectedMoney, Player[] players) {
        assertEquals(expectedMoney.length, players.length);
        for (int i = 0; i < expectedMoney.length; i++) {
            assertEquals(expectedMoney[i], players[i].getSessionMoney());
        }
    }

    @Test
    public void test4PlayerSession() {
        Player[] players = {new Player("Anton", 1), new Player("Berta", 2), new Player("Cäsar", 3), new Player("Dora", 4)};
        Session session = createDefaultSession(players);

        //now add a game
        Player[] callers = {players[0], players[1]};
        Player[] nonCallers = {players[2], players[3]};
        StakeModifier gameMod = new StakeModifier();
        gameMod.setSchneider(true);
        gameMod.setNumberOfLaufende(3);
        SingleGameResult game = createGame(callers, nonCallers, GameType.SAUSPIEL, true, gameMod);
        session.addGame(game);
        assertEquals(1, session.getGameAmount());
        int[] expectedMoney = {50, 50, -50, -50};
        testMoney(expectedMoney, players);

        //now add another game
        Player[] callers2 = {players[3]};
        Player[] nonCallers2 = {players[0], players[1], players[2]};
        gameMod = new StakeModifier();
        gameMod.setSchneider(true);
        gameMod.setSchwarz(true);
        SingleGameResult game2 = createGame(callers2, nonCallers2, GameType.WENZ, true, gameMod);
        session.addGame(game2);
        assertEquals(2, session.getGameAmount());
        int[] expectedMoney2 = {-20, -20, -120, 160};
        testMoney(expectedMoney2, players);
    }

    @Test
    public void test5PlayerSession() {
        Player[] players = {new Player("Anton", 1), new Player("Berta", 2), new Player("Cäsar", 3), new Player("Dora", 4), new Player("Emil", 5)};
        Session session = createDefaultSession(players);

        //add a game: Anton, Berta vs Cäsar,Dora
        Player[] callers = {players[0], players[1]};
        Player[] nonCallers = {players[2], players[3]};
        StakeModifier gameMod = new StakeModifier();
        gameMod.setSchneider(true);
        gameMod.setNumberOfLaufende(3);
        SingleGameResult game = createGame(callers, nonCallers, GameType.SAUSPIEL, true, gameMod);
        session.addGame(game);
        assertEquals(1, session.getGameAmount());
        int[] expectedMoney = {50, 50, -50, -50, 0};
        testMoney(expectedMoney, players);

        //add another game: Emil vs Berta,Cäsar,Dora
        Player[] callers2 = {players[4]};
        Player[] nonCallers2 = {players[1], players[2], players[3]};
        gameMod = new StakeModifier();
        gameMod.setSchneider(true);
        gameMod.setSchwarz(true);
        SingleGameResult game2 = createGame(callers2, nonCallers2, GameType.WENZ, true, gameMod);
        session.addGame(game2);
        assertEquals(2, session.getGameAmount());
        int[] expectedMoney2 = {50, -20, -120, -120, 210};
        testMoney(expectedMoney2, players);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAmountOfPlayers() {
        Player[] players = {new Player("Anton", 1), new Player("Berta", 2), new Player("Cäsar", 3)};
        new Session(Arrays.asList(players), new Stake(1, 1, 1));
    }
}