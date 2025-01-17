package net.syphlex.practice.manager.match;

import com.ngxdev.knockback.KnockbackModule;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.core.Core;
import net.syphlex.core.rank.Rank;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.Pair;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
@Setter
public class Match {

    private final List<Profile> spectators = new ArrayList<>();

    private final Map<Profile, Pair<Integer, Boolean>> profileMap = new HashMap<>();

    private final List<Block> placedBlocks = new ArrayList<>();

    private final Party party;
    private final Arena arena;
    private final Ladder ladder;
    private final boolean queuedMatch;
    private final boolean ffa;

    private int duration = 6;

    private int teamOneScore = 0, teamTwoScore = 0;

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

        this.party = party;
        this.arena = arena;
        this.ladder = ladder;
        this.queuedMatch = queuedMatch;
        this.ffa = ffa;

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
                    profileMap.put(profile, new Pair<>(0, true));
                    setupPlayer(profile, 0);
                }

            }
            // if match is not free for all and is a party split event teams will be assigned.
            else {

                // randomize the teams
                Collections.shuffle(members);

                // sort and assign players to teams '1' & '2'
                int i = 0;
                for (Profile profile : members) {
                    int teamNumber = (i % 2) + 1;
                    profileMap.put(profile, new Pair<>(teamNumber, true));
                    setupPlayer(profile, teamNumber);
                    i++;
                }
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
                profileMap.put(profile, new Pair<>(teamNumber, true));
                setupPlayer(profile, teamNumber);
                i++;
            }
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

        // Loop through each profile in the match to send initial match information
        for (Profile profile : profileMap.keySet()) {

            // Send match title and ladder info to each player
            profile.sendMessage(" ");
            profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch");
            profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                    + Practice.QUATERNARY_COLOR + "Ladder: "
                    + Practice.PRIMARY_COLOR + ladder.getName());

            // Get the player's opponents in the match
            List<Profile> opponents = getOpponentList(profile);

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
                    List<Profile> teamOne = getAliveFromTeam(1);
                    List<Profile> teamTwo = getAliveFromTeam(2);

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
                    List<Profile> ffa = getAliveFromTeam(0);

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
                } else if (duration <= 0) {
                    sendMatchMessage("&aMatch started!"); // Inform players that the match has started
                    sendMatchTitle(" "); // Clear the countdown title
                    playMatchSound(Sound.FIREWORK_BLAST); // Play a sound indicating the match has started

                    matchState = MatchState.ONGOING; // Set the match state to ONGOING

                    cancel(); // Cancel the countdown task as the match is now live
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L); // Schedule this task to run every second (20 ticks)
    }

    /**
     * Ends the match, either normally or due to a forced end (e.g., server restart/crash).
     *
     * This method handles the termination of the match, including:
     * - Cancelling any ongoing match tasks.
     * - Handling the forced end scenario (e.g., server restart/crash).
     * - Properly resetting the state of all players.
     * - Cleaning up any match-related resources (e.g., blocks, match state).
     *
     * @param forceEnd If true, forces the match to end immediately (e.g., for server crashes or restarts).
     */
    public void endMatch(boolean forceEnd) {

        // If there's an ongoing match task, cancel it to stop any further match actions
        if (matchTask != null) {
            matchTask.cancel();
        }

        // If the match is being forcefully ended (e.g., server restarted/crashed)
        if (forceEnd) {

            // Remove all players from the match
            for (Profile profile : profileMap.keySet()) {
                removePlayer(profile);
            }

            // Reset any blocks placed during the match by setting them to air
            for (Block block : placedBlocks) {
                block.setType(Material.AIR);
            }

            // Mark the arena as open again, allowing it to be used for future matches
            arena.setOpen(true);
            return;
        }

        // Update match state to "ENDED" and set the duration for the end phase
        matchState = MatchState.ENDED;
        duration = 4;

        // Reset state for all players in the match
        for (Profile profile : profileMap.keySet()) {
            profile.setMatch(null); // Remove the match reference from the player's profile
            profile.setLastAttacker(null); // Clear the last attacker (if applicable)
            profile.setLastMatchLadder(ladder); // Record the ladder used in the last match
            profile.setKnockback(KnockbackModule.getDefault().title); // Reset knockback settings
            profile.setPlayerState(PlayerState.IN_SPAWN); // Set player state to spawn (they're out of the match)

            // Remove the player from the queue manager's list of players in a match
            Practice.get().getQueueManager().getPlayersInMatch().remove(profile);

            // If it's not a party match or FFA, give the player the option to play again
            if (party == null && !ffa && queuedMatch) {
                profile.getPlayer().getInventory().setItem(0, ItemUtil.getPlayAgainItem());
            }
        }

        // Set up a delayed task to finalize the end of the match
        matchTask = new BukkitRunnable() {
            @Override
            public void run() {
                --duration; // Decrease the duration counter each tick

                // Once the duration reaches zero, complete the match ending process
                if (duration <= 0) {

                    // Remove players who are no longer in the match (in case they were disconnected or teleported)
                    for (Profile profile : profileMap.keySet()) {
                        if (!profile.isInMatch()) {
                            removePlayer(profile);
                        }
                    }

                    // Reset any blocks placed during the match (return them to their original state)
                    for (Block block : placedBlocks) {
                        block.setType(Material.AIR);
                    }

                    // Mark the arena as open again for future use
                    arena.setOpen(true);

                    // Remove the match from the match manager
                    Practice.get().getMatchManager().removeMatch(Match.this);

                    // Clear the player map (indicating the match is no longer active)
                    profileMap.clear();

                    // Cancel the task since the match is over
                    cancel();
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L); // Schedule this task to run every second (20 ticks)
    }

    /**
     * Removes a player from the match, resets their state, and teleports them to the main spawn.
     *
     * This method handles the following:
     * - Resets the player's state by clearing any match-related properties.
     * - Restores the player's inventory to a predefined "spawn" inventory.
     * - Teleports the player to the main spawn location, effectively removing them from the match.
     *
     * @param profile The profile of the player to be removed.
     */
    private void removePlayer(Profile profile) {

        // Reset the player's state (e.g., clear any active match-related effects, attributes)
        PlayerUtil.resetPlayer(profile.getPlayer());

        // Set the player's inventory to the spawn inventory (could be a standard inventory when they leave the match)
        InventoryUtil.setSpawnInventory(profile.getPlayer());

        // Teleport the player to the main spawn location (e.g., out of the match arena)
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
    }

    /**
     * Eliminates a player from the match, updates their status, and broadcasts the elimination event.
     *
     * This method handles the logic of eliminating a player from the match, including:
     * - Updating the player's status in the `profileMap` to reflect that they are no longer alive.
     * - Broadcasting messages to all players in the match about the player's elimination.
     * - Checking if the match should end based on the number of remaining players (whether it's a team or Free-For-All match).
     *
     * @param profile The profile of the player to be eliminated.
     */
    public void eliminate(Profile profile) {

        // Get the team number of the eliminated player
        int teamNumber = profileMap.get(profile).getX();

        // Mark the player as eliminated (alive status set to false)
        profileMap.put(profile, new Pair<>(teamNumber, false));

        // Broadcast message based on the elimination context (whether it was by an attacker or not)
        if (profile.getLastAttacker() == null) {
            // If no attacker, simply broadcast the player's death
            if (profileMap.size() > 2) {
                sendMatchMessage(" ");
                sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " died.");
                sendMatchMessage(" ");
            } else {
                // If only 2 players are left, broadcast who killed the eliminated player
                sendMatchMessage(" ");
                sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR + getOpponentList(profile).get(0).getPlayer().getName()
                        + Practice.QUATERNARY_COLOR + ".");
                sendMatchMessage(" ");
            }
        } else {
            // If the player was killed by another player, broadcast the name of the attacker
            sendMatchMessage(" ");
            sendMatchMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                    + Practice.QUATERNARY_COLOR + " was killed by "
                    + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                    + Practice.QUATERNARY_COLOR + ".");
            sendMatchMessage(" ");
        }

        // If the match is not Free-For-All (FFA), check team elimination conditions
        if (!ffa) {

            // Get the list of alive players for each team
            List<Profile> teamOne = getAliveFromTeam(1);
            List<Profile> teamTwo = getAliveFromTeam(2);

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
            List<Profile> ffa = getAliveFromTeam(0);

            // If only one player is left, the match ends
            if (ffa.size() == 1) {
                endMatch(false);
            }
        }
    }

    /**
     * Sets up the player's state for the match.
     *
     * @param profile    The profile of the player to be set up.
     * @param teamNumber The team number the player is assigned to (0 for spectate, 1 or 2 for teams).
     */
    private void setupPlayer(Profile profile, int teamNumber) {

        profile.getDuelRequests().clearRequests();

        profile.setMatch(this);
        profile.setPlayerState(PlayerState.IN_MATCH);
        profile.setHits(0);
        profile.setLastAttacker(null);
        profile.setLadderQueued(null);
        PlayerUtil.resetPlayer(profile.getPlayer());

        // Teleports the player to the appropriate location based on the team number.
        // If teamNumber is 0, teleport to the spectating position.
        // If teamNumber is 1, teleport to the position for team 1.
        // If teamNumber is 2, teleport to the position for team 2.
        profile.teleport(teamNumber == 0 ? arena.getSpectate() : teamNumber == 1
                ? arena.getPosition1() : arena.getPosition2());

        ladder.giveBookKits(profile);
    }

    /**
     * Sends a message to all participants in the match.
     *
     * @param message The message to be sent to all players in the match.
     */
    public void sendMatchMessage(String message) {
        for (Profile profile : profileMap.keySet()) {
            profile.sendMessage(message);
        }
    }

    /**
     * Sends a title message to all profiles in the match.
     * <p>
     * This method sends a title (or subtitle) message to all players (profiles) in the match.
     * The title appears for a brief period (0-1-0 timing), which is typically used to display
     * match-related messages such as announcements or alerts to all players in the match.
     *
     * @param message The title message to be sent to all players in the match.
     */
    public void sendMatchTitle(String message) {
        for (Profile profile : profileMap.keySet()) {
            profile.sendTitle(message, 0, 1, 0);
        }
    }

    /**
     * Plays a sound for all profiles in the match.
     * <p>
     * This method plays a specified sound at the location of each player (profile) in the match.
     * It ensures all players hear the same sound at the same time, which can be used for match-related events,
     * such as starting a match, announcing a round win, etc.
     *
     * @param sound The sound to be played for all players in the match.
     */
    public void playMatchSound(Sound sound) {
        for (Profile profile : profileMap.keySet()) {
            profile.getPlayer().playSound(profile.getPlayer().getLocation(), sound, 1, 1);
        }
    }

    /**
     * Retrieves a list of opponents for a given profile in a match.
     * <p>
     * Depending on the team the provided profile is in, this method will:
     * - For team '1', it will add all profiles from team '2' to the opponent list.
     * - For team '2', it will add all profiles from team '1' to the opponent list.
     * - For Free-For-All (FFA) mode, it will add all other players to the opponent list (excluding the provided profile).
     *
     * @param profile The profile of the player whose opponents are being queried.
     * @return A list of profiles representing the player's opponents in the match.
     */
    public List<Profile> getOpponentList(Profile profile) {
        List<Profile> opponents = new ArrayList<>();

        int team = profileMap.get(profile).getX();

        // If FFA mode or team-based match, process accordingly.
        if (team == 0) { // FFA
            for (Profile p : profileMap.keySet()) {
                if (p != profile) { // Skip the player itself
                    opponents.add(p);
                }
            }
        } else { // Teams 1 or 2
            int opponentTeam = (team == 1) ? 2 : 1; // Opposing team
            for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : profileMap.entrySet()) {
                if (entry.getValue().getX() == opponentTeam) {
                    opponents.add(entry.getKey());
                }
            }
        }

        return opponents;
    }

    /**
     * Retrieves a list of alive opponents for a given profile.
     * <p>
     * This method filters out the players who are considered alive (based on the `y` value in the `profileMap`).
     * It will only include players who are alive and are in the opponent team.
     *
     * @param profile The profile of the player whose opponents are being retrieved.
     * @return A list of profiles representing the alive opponents of the player.
     */
    public List<Profile> getAliveOpponentList(Profile profile) {
        List<Profile> opponents = new ArrayList<>();
        // Iterate through all the player's opponents
        for (Profile players : getOpponentList(profile)) {
            // Check if the opponent is alive (y == true)
            if (profileMap.get(players).getY()) {
                opponents.add(players);
            }
        }
        return opponents;
    }

    /**
     * Retrieves a list of alive players from a specific team.
     * <p>
     * This method iterates through the `profileMap` and checks for players in the given team
     * who are alive (based on the `y` value in the `profileMap`).
     *
     * @param teamNumber The team number to retrieve players from (e.g., 1 or 2).
     * @return A list of profiles representing the alive players in the specified team.
     */
    public List<Profile> getAliveFromTeam(int teamNumber) {
        List<Profile> alive = new ArrayList<>();
        // Iterate through all players and check if they belong to the specified team and are alive
        for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : profileMap.entrySet()) {
            if (entry.getValue().getX() == teamNumber && entry.getValue().getY()) {
                alive.add(entry.getKey());
            }
        }
        return alive;
    }

    /**
     * Retrieves the team of a specific player.
     * <p>
     * This method finds all players in the same team as the given profile by checking the team number
     * (based on the `x` value in the `profileMap`).
     *
     * @param profile The profile of the player whose team is being retrieved.
     * @return A list of profiles representing the players in the same team as the given player.
     */
    public List<Profile> getPlayerTeam(Profile profile) {
        List<Profile> team = new ArrayList<>();
        // Get the team number for the given player
        int teamNumber = profileMap.get(profile).getX();
        // Iterate through all players and add those who belong to the same team
        for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : profileMap.entrySet()) {
            if (entry.getValue().getX() == teamNumber) {
                team.add(entry.getKey());
            }
        }
        return team;
    }

    /**
     * Retrieves the list of alive players from the same team as the given player.
     * <p>
     * This method filters out the players who are in the same team as the given profile and checks if they are alive.
     * It uses the team number and the alive status (y value) to determine the result.
     *
     * @param profile The profile of the player whose team's alive players are being retrieved.
     * @return A list of profiles representing the alive players in the same team as the given player.
     */
    public List<Profile> getAliveFromPlayerTeam(Profile profile) {
        List<Profile> team = new ArrayList<>();
        // Get the team number for the given player
        int teamNumber = profileMap.get(profile).getX();
        // Iterate through all players, checking for those who are on the same team and are alive
        for (Map.Entry<Profile, Pair<Integer, Boolean>> entry : profileMap.entrySet()) {
            if (entry.getValue().getX() == teamNumber && entry.getValue().getY()) {
                team.add(entry.getKey());
            }
        }
        return team;
    }

    /**
     * Checks if a given player is part of team 1.
     * <p>
     * This method verifies if the profile belongs to team 1 by checking the `profileMap` entry
     * and comparing the team number (x value).
     *
     * @param profile The profile of the player to check.
     * @return `true` if the player is on team 1, `false` otherwise.
     */
    public boolean isTeamOne(Profile profile) {
        // Check if the profile exists in the profileMap before verifying the team number
        if (profileMap.containsKey(profile)) {
            return profileMap.get(profile).getX() == 1;
        }
        return false;
    }

    /**
     * Checks if a given player is part of team 2.
     * <p>
     * This method verifies if the profile belongs to team 2 by checking the `profileMap` entry
     * and comparing the team number (x value).
     *
     * @param profile The profile of the player to check.
     * @return `true` if the player is on team 2, `false` otherwise.
     */
    public boolean isTeamTwo(Profile profile) {
        // Check if the profile exists in the profileMap before verifying the team number
        if (profileMap.containsKey(profile)) {
            return profileMap.get(profile).getX() == 2;
        }
        return false;
    }
}