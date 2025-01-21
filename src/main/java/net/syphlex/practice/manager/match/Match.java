package net.syphlex.practice.manager.match;

//import com.ngxdev.knockback.KnockbackModule;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.core.Core;
import net.syphlex.core.rank.Rank;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.arena.block.BlockTracker;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.ladder.impl.BedFightLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.match.team.MatchTeam;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.InventorySnapshot;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.*;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
@Setter
public class Match {

    private final UUID uuid;

    private final MatchInventories matchInventories;

    private final List<Profile> spectators = new ArrayList<>();

    private final MatchTeam teamFFA = new MatchTeam("&c", "FFA", 0);
    private final MatchTeam teamOne = new MatchTeam("&c", "Red", 1);
    private final MatchTeam teamTwo = new MatchTeam("&9", "Blue", 2);

    private final Party party;
    private final Arena arena;
    private final Ladder ladder;
    private final boolean queuedMatch;
    private final boolean ffa;

    private final BlockTracker blockTracker;

    private int duration = 6;

    private MatchState matchState = MatchState.STARTING;

    private BukkitTask matchTask = null;

    /**
     * Constructor for initializing a new Match instance with the specified parameters.
     *
     * @param teamOneParam List of Profile objects representing the first team in the match.
     * @param teamTwoParam List of Profile objects representing the second team in the match.
     * @param party        The Party object representing the party involved in the match (if applicable).
     * @param arena        The Arena object where the match will take place.
     * @param ladder          The Ladder object that defines the rules and equipment for the match.
     * @param queuedMatch  A boolean flag indicating whether this match was queued or initiated.
     * @param ffa          A boolean flag indicating whether the match is a Free-for-All (FFA) match or a team-based match.
     */
    public Match(List<Profile> teamOneParam, List<Profile> teamTwoParam,
                 Party party, Arena arena, Ladder ladder, boolean queuedMatch, boolean ffa) {

        // generate a random UUID for the match identification
        this.uuid = UUID.randomUUID();

        this.matchInventories = new MatchInventories();

        this.party = party;
        this.arena = arena;
        this.ladder = ladder;
        this.queuedMatch = queuedMatch;
        this.ffa = ffa;

        this.blockTracker = new BlockTracker(arena);

        setupMatch(teamOneParam, teamTwoParam);
    }

    /**
     * Sets up the match by organizing players into teams or as individuals.
     *
     * This method:
     * - Closes the arena to prevent other matches from using it concurrently.
     * - Checks if the match is a party event or a duel/queued match.
     * - For party matches, assigns players to teams or as free-for-all based on the match type.
     * - For duel/queued matches, assigns players to teams in a randomized manner.
     * - Calls `setupPlayer` for each player to initialize their match state and position.
     * - Starts the match by invoking the `startMatch()` method.
     *
     * @param teamOneParam List of profiles for team one in a duel or queued match.
     * @param teamTwoParam List of profiles for team two in a duel or queued match.
     */
    private void setupMatch(List<Profile> teamOneParam, List<Profile> teamTwoParam) {

        // close the arena so no other matches can use at the same time
        arena.setOpen(false);

        // the match is a party event
        if (party != null) {

            // get all party members including the leader
            // and move them into our matches profile hashmap.
            List<Profile> members = new ArrayList<>(party.getMembers());
            members.add(party.getLeader());

            // if match is free for all team will be identified as '0'
            if (ffa) {

                // setup players

                for (Profile profile : members) {
                    teamFFA.add(profile);
                }

                teamFFA.setupPlayers(this);
            }
            // if match is not free for all and is a party split event teams will be assigned.
            else {

                // randomize the teams
                Collections.shuffle(members);

                // sort and assign players to teams '1' & '2'
                int i = 0;
                for (Profile profile : members) {
                    int teamNumber = (i % 2) + 1;
                    if (teamNumber == 1) {
                        teamOne.add(profile);
                    } else {
                        teamTwo.add(profile);
                    }
                    i++;
                }

                // setup players in each team
                teamOne.setupPlayers(this);
                teamTwo.setupPlayers(this);
            }
        }
        // the match is a duel/queued match
        else {

            // gather all team params into one list
            List<Profile> players = new ArrayList<>(teamOneParam);
            players.addAll(teamTwoParam);

            // randomize the teams
            Collections.shuffle(players);

            // sort and assign players to teams '1' & '2'
            int i = 0;
            for (Profile profile : players) {
                int teamNumber = (i % 2) + 1;
                if (teamNumber == 1) {
                    teamOne.add(profile);
                } else {
                    teamTwo.add(profile);
                }
                i++;
            }

            // setup players in each team
            teamOne.setupPlayers(this);
            teamTwo.setupPlayers(this);
        }

        startMatch();
    }

    /**
     * Starts the match and manages the countdown and match state.
     *
     * This method handles:
     * - Sending initial match information to all players.
     * - Handling match countdown and match state transition.
     * - Checking if teams or players are eliminated (to end the match early if needed).
     * - Playing appropriate sounds and titles during the countdown.
     */
    private void startMatch() {

        List<Profile> players = new ArrayList<>(teamFFA.getAsList());
        players.addAll(teamOne.getTeamMap().keySet());
        players.addAll(teamTwo.getTeamMap().keySet());

        // Loop through each profile in the match to send initial match information
        for (Profile profile : players) {

            // Send match title and ladder info to each player
            profile.sendMessage(" ");
            profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                    + Practice.QUATERNARY_COLOR + "Ladder: "
                    + Practice.PRIMARY_COLOR + ladder.getName());

            // Get the player's opponents in the match
            List<Profile> opponents = getOpponents(profile).getAsList();

            // If there are multiple opponents (e.g., team vs team), list them
            if (opponents.size() > 1) {

                // Display the opponents' rank, Elo, and ping
                for (Profile opponent : opponents) {
                    Rank rank = Core.get().getPlayerDataManager().get(opponent.getPlayer()).getRank();
                    int elo = Practice.get().getLeaderboardManager().getElo(opponent.getPlayer().getUniqueId(), ladder);

                    profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                            + Practice.QUATERNARY_COLOR + "Opponents:");
                    profile.sendMessage(Practice.PRIMARY_COLOR + "   »  "
                            + rank.getColor() + opponent.getPlayer().getName()
                            + "&7(" + elo + " Elo) (" + opponent.getPing() + "ms)");
                }

                // If there is only one opponent (e.g., duel), display single opponent info
            } else {
                Rank rank = Core.get().getPlayerDataManager().get(opponents.get(0).getPlayer()).getRank();
                int elo = Practice.get().getLeaderboardManager().getElo(opponents.get(0).getPlayer().getUniqueId(), ladder);

                profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                        + Practice.QUATERNARY_COLOR + "Opponent: "
                        + rank.getColor() + opponents.get(0).getPlayer().getName()
                        + " &7(" + elo + " Elo)");
                profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                        + Practice.QUATERNARY_COLOR + "Ping: "
                        + Practice.PRIMARY_COLOR + opponents.get(0).getPing() + "ms");
            }
            profile.sendMessage(" ");
        }

        // Set up a task that counts down the match and checks match status periodically
        matchTask = new BukkitRunnable() {
            @Override
            public void run() {
                --duration; // Decrease the duration counter for the countdown

                // If it's a team-based match, check if either team is completely eliminated
                if (!ffa) {
                    List<Profile> teamOne = Match.this.teamOne.getAsList();
                    List<Profile> teamTwo = Match.this.teamTwo.getAsList();

                    // End the match if one team is eliminated
                    if (teamOne.isEmpty()) {
                        endMatch(false);
                        return;
                    }

                    if (teamTwo.isEmpty()) {
                        endMatch(false);
                        return;
                    }

                    // If it's a Free-For-All (FFA) match, check if there's only one player left
                } else {
                    List<Profile> ffa = Match.this.teamFFA.getAsList();

                    // End the match if there's only one player left in FFA
                    if (ffa.size() == 1) {
                        endMatch(false);
                        return;
                    }
                }

                // Continue the countdown if the match has not started yet
                if (duration > 0) {
                    sendMatchMessage(Practice.QUATERNARY_COLOR + "Match starting in "
                            + Practice.PRIMARY_COLOR + duration + Practice.QUATERNARY_COLOR + " seconds...");
                    sendMatchTitle("&c" + duration); // Update title with the remaining countdown
                    playMatchSound(Sound.CLICK); // Play a click sound for countdown

                    // When the countdown reaches zero, start the match
                } else {
                    sendMatchMessage("&aMatch started!"); // Inform players that the match has started
                    sendMatchTitle(" "); // Clear the countdown title
                    playMatchSound(Sound.FIREWORK_BLAST); // Play a sound indicating the match has started

                    matchState = MatchState.ONGOING; // Set the match state to ONGOING

                    cancel(); // Cancel the countdown task as the match is now live
                    matchTask = null;
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L); // Schedule this task to run every second (20 ticks)
    }

    public void endMatch(boolean forceEnd) {

        // If there's an ongoing match task, cancel it to stop any further match actions
        if (matchTask != null) {
            matchTask.cancel();
        }

        sendMatchMessage(" ");
        sendMatchMessage(Practice.PRIMARY_COLOR + "&lMatch Results &7(Click on names to view inventories)");

        if (ffa) {

            // only one player will return from this
            List<Profile> winner = teamFFA.getAliveList();
            //List<Profile> totalPlayers = teamFFA.getAsList();

            //totalPlayers.remove(winner.get(0));

            teamFFA.sendClickableMessage(
                    Practice.QUATERNARY_COLOR + " » &aWinner: &f"
                            + winner.get(0).getPlayer().getDisplayName(),
                    "inventory " + uuid.toString() + " "
                            + winner.get(0).getPlayer().getUniqueId().toString());

            sendMatchMessage(Practice.QUATERNARY_COLOR + " » &cLosers:");

            winner.forEach(players -> {
                players.sendClickableMessage(
                        Practice.SECONDARY_COLOR + "    » "
                                + players.getPlayer().getDisplayName(),
                        "inventory " + uuid.toString() + " " + players.getPlayer().getUniqueId().toString()
                );
            });

            teamFFA.getListWithoutProfile(winner.get(0))
                            .forEach(players -> {
                                players.sendClickableMessage(
                                        Practice.SECONDARY_COLOR + "    » "
                                                + players.getPlayer().getDisplayName(),
                                        "inventory " + uuid.toString() + " "
                                                + players.getPlayer().getUniqueId().toString());
                                    }
                            );

            sendMatchMessage(" ");
        } else {

            List<Profile> teamOne = Match.this.teamOne.getAsList();
            List<Profile> teamTwo = Match.this.teamTwo.getAsList();

            if (Match.this.teamTwo.getAliveList().isEmpty() || Match.this.teamOne.getScore() >= 5) {
                // team one won
                if (Match.this.teamOne.getCount() > 1) {

                    sendMatchMessage(Practice.QUATERNARY_COLOR + " » &aWinners: ");

                    for (Profile winners : teamOne) {
                        sendClickableMatchMessage(Practice.SECONDARY_COLOR + "    » "
                                        + winners.getPlayer().getDisplayName(),
                                "inventory " + uuid.toString() + " "
                                        + winners.getPlayer().getUniqueId().toString());
                    }

                } else {

                    Profile winner = teamOne.get(0);

                    sendClickableMatchMessage(Practice.QUATERNARY_COLOR + " » &aWinner: &f"
                                    + winner.getPlayer().getDisplayName(),
                            "inventory " + uuid.toString() + " "
                                    + winner.getPlayer().getUniqueId().toString());
                }
                if (Match.this.teamTwo.getCount() > 1) {

                    sendMatchMessage(Practice.QUATERNARY_COLOR + " » &cLosers: ");

                    for (Profile losers : teamTwo) {
                        sendClickableMatchMessage(Practice.SECONDARY_COLOR + "    » "
                                        + losers.getPlayer().getDisplayName(),
                                "inventory " + uuid.toString() + " "
                                        + losers.getPlayer().getUniqueId().toString());
                    }

                } else {

                    Profile profile = teamTwo.get(0);

                    sendClickableMatchMessage(
                            Practice.QUATERNARY_COLOR + " » &cLoser: &f"
                                    + profile.getPlayer().getDisplayName(),
                            "inventory " + uuid.toString() + " "
                                    + profile.getPlayer().getUniqueId().toString());
                }
            } else {
                // team two won
                if (Match.this.teamTwo.getCount() > 1) {

                    sendMatchMessage(Practice.QUATERNARY_COLOR + " » &aWinners: ");

                    for (Profile winners : teamTwo) {
                        sendClickableMatchMessage(
                                Practice.SECONDARY_COLOR + "    » "
                                        + winners.getPlayer().getDisplayName(),
                                "inventory " + uuid.toString() + " "
                                        + winners.getPlayer().getUniqueId().toString());
                    }

                } else {

                    Profile winner = teamTwo.get(0);

                    sendClickableMatchMessage(Practice.QUATERNARY_COLOR + " » &aWinner: &f"
                                    + winner.getPlayer().getDisplayName(),
                            "inventory " + uuid.toString() + " "
                                    + winner.getPlayer().getUniqueId().toString());
                }

                if (Match.this.teamOne.getCount() > 1) {

                    sendMatchMessage(Practice.QUATERNARY_COLOR + " » &cLosers: ");

                    for (Profile losers : teamOne) {
                        sendClickableMatchMessage(Practice.SECONDARY_COLOR + "    » "
                                        + losers.getPlayer().getDisplayName(),
                                "inventory " + uuid.toString() + " "
                                        + losers.getPlayer().getUniqueId().toString());
                    }

                } else {

                    Profile loser = teamOne.get(0);

                    sendClickableMatchMessage(Practice.QUATERNARY_COLOR + " » &cLoser: &f"
                                    + loser.getPlayer().getDisplayName(),
                            "inventory " + uuid.toString() + " "
                                    + loser.getPlayer().getUniqueId().toString());
                }
            }

            sendMatchMessage(" ");

            int winnerEloChange = 0;
            int loserEloChange = 0;

            // match is a normal queue match
            if (queuedMatch && party == null) {

                Profile winner;
                Profile loser;

                if (teamOne.isEmpty()) {
                    winner = teamTwo.get(0);
                    loser = getOpponents(winner).getAsList().get(0);
                } else {
                    winner = teamOne.get(0);
                    loser = getOpponents(winner).getAsList().get(0);
                }

                int winnerElo = Practice.get().getLeaderboardManager().getElo(winner.getPlayer().getUniqueId(), ladder);
                int loserElo = Practice.get().getLeaderboardManager().getElo(loser.getPlayer().getUniqueId(), ladder);

                // update loss for loser and win for winner
                Practice.get().getLeaderboardManager().updateLeaderboardPlayer(ladder, loser, 0, 1);
                Practice.get().getLeaderboardManager().updateLeaderboardPlayer(ladder, winner, 1, 0);

                winnerEloChange = Practice.get().getLeaderboardManager().getElo(winner.getPlayer().getUniqueId(), ladder) - winnerElo;
                loserEloChange = Practice.get().getLeaderboardManager().getElo(loser.getPlayer().getUniqueId(), ladder) - loserElo;

                winner.sendMessage(Practice.QUATERNARY_COLOR + "Your updated elo: "
                        + Practice.PRIMARY_COLOR + winnerElo + Practice.QUATERNARY_COLOR + ". &a(+"
                        + winnerEloChange + ")");
                loser.sendMessage(Practice.QUATERNARY_COLOR + "Your updated elo: "
                        + Practice.PRIMARY_COLOR + loserElo + Practice.QUATERNARY_COLOR + ". &c("
                        + loserEloChange + ")");

                sendMatchMessage(" ");
            }
        }

        // If the match is being forcefully ended (e.g., server restarted/crashed)
        if (forceEnd) {

            if (matchTask != null) {
                matchTask.cancel();
            }

            destroyMatch();
            return;
        }

        // Update match state to "ENDED" and set the duration for the end phase
        matchState = MatchState.ENDED;
        duration = 5;

        teamOne.startEndOfMatch(this);
        teamTwo.startEndOfMatch(this);
        teamFFA.startEndOfMatch(this);

        // Set up a delayed task to finalize the end of the match
        matchTask = new BukkitRunnable() {
            @Override
            public void run() {
                --duration; // Decrease the duration counter each tick

                if (duration == 2) {
                    // If it's not a party match or FFA, give the player the option to play again
                    if (party == null && !ffa && queuedMatch) {
                        teamOne.getAsList().forEach(profile ->
                                profile.getPlayer().getInventory().setItem(0, ItemUtil.getPlayAgainItem()));
                        teamTwo.getAsList().forEach(profile ->
                                profile.getPlayer().getInventory().setItem(0, ItemUtil.getPlayAgainItem()));
                    }
                }

                // Once the duration reaches zero, complete the match ending process
                if (duration <= 0) {
                    destroyMatch();
                    cancel();
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L); // Schedule this task to run every second (20 ticks)
    }

    private void destroyMatch(){

        for (Profile spectator : spectators) {
            teamOne.getAsList().forEach(profile -> profile.show(spectator));
            teamTwo.getAsList().forEach(profile -> profile.show(spectator));
            teamFFA.getAsList().forEach(profile -> profile.show(spectator));
        }

        teamOne.deletePlayers();
        teamTwo.deletePlayers();
        teamFFA.deletePlayers();

        for (Profile spectator : spectators) {
            spectator.setSpectatingMatch(null);
            spectator.reset();
            spectator.setLobbyInventory();
            spectator.teleport(Practice.get().getConfigManager().getMainSpawn());
            spectator.setPlayerState(PlayerState.IN_SPAWN);


            // unneccisasry??
            //for (Profile profile : Practice.get().getProfileManager().getProfileMap().values()) {
            //    profile.show(spectator);
            //    spectator.show(profile);
            //}
        }

        if (ladder instanceof BridgeLadder || ladder instanceof BedFightLadder) {
            blockTracker.clear();
        }

        // Remove any left behind entities from the arena
        arena.removeEntities();

        // Remove the match from the match manager
        Practice.get().getMatchManager().removeMatch(Match.this);
    }

    public void eliminate(Profile profile) {

        if (!getTeam(profile).isAlive(profile)) {
            return;
        }

        getTeam(profile).setEliminated(profile);

        arena.getWorld().strikeLightningEffect(profile.getPlayer().getLocation());
        profile.getPlayer().setVelocity(profile.getPlayer().getVelocity().multiply(2.15).setY(0.6).normalize());

        // Take a snapshot of the inventory before we reset it.
        matchInventories.getInventories().put(profile.getPlayer().getUniqueId(), new InventorySnapshot(profile));

        if (!(ladder instanceof BedFightLadder) && !(ladder instanceof BridgeLadder)) {

            // Broadcast message based on the elimination context (whether it was by an attacker or not)
            if (profile.getLastAttacker() == null) {
                sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR
                        + getOpponents(profile).getAsList().get(0).getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + ".");
            } else {
                // If the player was killed by another player, broadcast the name of the attacker
                sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + ".");
            }
        }

        if (!profile.getPlayer().isDead()) {
            // Eliminated player becomes a spectator of the match
            addSpectator(profile);
        }

        // If the match is not Free-For-All (FFA), check team elimination conditions
        if (!ffa) {

            // Get the list of alive players for each team
            List<Profile> teamOne = this.teamOne.getAliveList();
            List<Profile> teamTwo = this.teamTwo.getAliveList();

            // Team one lost, and team two won
            if (teamOne.isEmpty() && !teamTwo.isEmpty()) {
                endMatch(false);
            }
            // Team two lost, and team one won
            else if (!teamOne.isEmpty() && teamTwo.isEmpty()) {
                endMatch(false);
            }
            // If both teams still have players, the match continues
            else {
                // No action needed, as the match continues
            }
        } else {
            // If the match is Free-For-All (FFA), check if only one player remains
            List<Profile> ffa = teamFFA.getAliveList();

            // If only one player is left, the match ends
            if (ffa.size() == 1) {
                endMatch(false);
            }
        }
    }

    public void addSpectator(Profile profile){
        spectators.add(profile);

        // show other spectators to new spectator
        for (Profile otherSpectators : spectators) {
            profile.show(otherSpectators);
        }

        // hide spectator from alive players
        teamOne.hideFromTeam(profile);
        teamTwo.hideFromTeam(profile);
        teamFFA.hideFromTeam(profile);

        profile.getPlayer().setGameMode(GameMode.CREATIVE);

        if (!profile.getPlayer().getAllowFlight()) {
            profile.getPlayer().setAllowFlight(true);
        }

        if (!profile.getPlayer().isFlying()) {
            profile.getPlayer().setFlying(true);
        }

        profile.setSpectatingMatch(this);
        profile.setPlayerState(PlayerState.SPECTATING_MATCH);
        profile.teleport(arena.getSpectate());

        profile.reset();
        profile.setSpectateMatchInventory();
    }

    public void removeSpectator(Profile profile){

        if (!spectators.contains(profile)){
            return;
        }

        spectators.remove(profile);


        teamOne.getAsList().forEach(members -> members.show(profile));
        teamTwo.getAsList().forEach(members -> members.show(profile));
        teamFFA.getAsList().forEach(members -> members.show(profile));

        // Show all players to old-spectator and show old-spectator to all players
        //for (Profile players : Practice.get().getProfileManager().getProfileMap().values()) {
        //    players.show(profile);
        //    profile.show(players);
        //}

        profile.getPlayer().setGameMode(GameMode.SURVIVAL);

        profile.setPlayerState(PlayerState.IN_SPAWN);
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());

        profile.reset();
        profile.setLobbyInventory();
    }

    public void handleBedFightRespawn(Profile profile){

        getOpponents(profile).sendSound(Sound.ORB_PICKUP);

        profile.teleport(arena.getSpectate());

        if (!getTeam(profile).isHasBed()) {
            eliminate(profile);
            return;
        }

        profile.setLastAttacker(null);

        profile.setRespawnTimer(3);

        profile.reset();
        profile.getPlayer().setGameMode(GameMode.SPECTATOR);
        profile.getPlayer().setAllowFlight(true);
        profile.getPlayer().setFlying(true);

        new BukkitRunnable(){
            @Override
            public void run(){

                if (!getTeam(profile).getAliveList().contains(profile)
                        || !profile.getPlayer().isOnline()) {
                    cancel();
                    return;
                }

                if (profile.getRespawnTimer() > 0) {
                    profile.sendTitle("&c&lYOU DIED", Practice.QUATERNARY_COLOR
                            + "Respawning in " + Practice.PRIMARY_COLOR + profile.getRespawnTimer()
                            + Practice.QUATERNARY_COLOR + " seconds...", 5, 10, 5);
                }

                if (profile.getRespawnTimer() <= 0) {

                    if (teamOne.isInTeam(profile)) {
                        profile.teleport(arena.getPosition1());
                    } else {
                        profile.teleport(arena.getPosition2());
                    }

                    if (profile.getPlayer().isFlying()) {
                        profile.getPlayer().setFlying(false);
                    }

                    if (profile.getPlayer().getAllowFlight()) {
                        profile.getPlayer().setAllowFlight(false);
                    }

                    profile.getPlayer().setGameMode(GameMode.SURVIVAL);
                    profile.reset();
                    ladder.giveKit(profile);

                    // clear old title
                    profile.sendTitle("&c", 5, 10, 5);

                    cancel();
                    return;
                }

                profile.setRespawnTimer(profile.getRespawnTimer() - 1);
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    public void sendClickableMatchMessage(String message, String command){
        teamOne.sendClickableMessage(message, command);
        teamTwo.sendClickableMessage(message, command);
        teamFFA.sendClickableMessage(message, command);
    }

    public void sendMatchMessage(String message) {
        teamOne.sendMessage(message);
        teamTwo.sendMessage(message);
        teamFFA.sendMessage(message);
    }

    public void sendMatchTitle(String message) {
        teamOne.sendTitle(message);
        teamTwo.sendTitle(message);
        teamFFA.sendTitle(message);
    }

    public void sendMatchTitle(String message, String subMessage) {
        teamOne.sendTitle(message, subMessage);
        teamTwo.sendTitle(message, subMessage);
        teamFFA.sendTitle(message, subMessage);
    }

    public void playMatchSound(Sound sound) {
        teamOne.sendSound(sound);
        teamTwo.sendSound(sound);
        teamFFA.sendSound(sound);
    }

    public MatchTeam getTeam(Profile profile){
        if (teamOne.isInTeam(profile))
            return teamOne;
        if (teamTwo.isInTeam(profile))
            return teamTwo;
        return teamFFA;
    }

    public MatchTeam getOpponents(Profile profile){
        if (teamOne.isInTeam(profile)) {
            return teamTwo;
        }
        if (teamTwo.isInTeam(profile)) {
            return teamOne;
        }
        return teamFFA;
    }
}