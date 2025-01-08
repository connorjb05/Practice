package net.syphlex.practice.manager.match;

import com.ngxdev.knockback.KnockbackModule;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
import net.syphlex.practice.manager.kit.impl.ComboKit;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.PlayerState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Match {

    private final Map<Profile, Boolean> profileMap = new ConcurrentHashMap<>();
    private final Map<Profile, Boolean> teamOne = new ConcurrentHashMap<>();
    private final Map<Profile, Boolean> teamTwo = new ConcurrentHashMap<>();

    private final List<Block> placedBlocks = new ArrayList<>();

    private final Party party;
    private final Arena arena;
    private final Kit kit;
    private boolean queuedMatch;
    private final boolean ffa;

    private int duration = 5; // 5 second of players getting ready, then 10 minute matches

    private MatchState matchState = MatchState.STARTING;

    private BukkitTask matchTask;

    private void prepareProfile(Profile profile) {

        if (teamOne.containsKey(profile)) {
            profile.teleport(arena.getPosition1());
        }

        if (teamTwo.containsKey(profile)) {
            profile.teleport(arena.getPosition2());
        }

        profile.setPlayerState(PlayerState.IN_MATCH);

        profile.setMatch(this);

        PlayerUtil.resetPlayer(profile.getPlayer());

        kit.giveBookKits(profile);

        if (kit instanceof ComboKit) {
            profile.setKnockback("combo");
        }

        Practice.get().getQueueManager().getPlayersInMatch().add(profile);
    }

    public Match(List<Profile> teamOneParam, List<Profile> teamTwoParam,
                 Party party, Arena arena, Kit kit, boolean queuedMatch, boolean ffa){

        // FFA is impossible if the match was queued
        // queued matches MEAN its either queued or from a /duel request
        if (ffa) {
            queuedMatch = false;
        } else {
            this.queuedMatch = queuedMatch;
        }

        this.party = party;
        this.arena = arena;
        this.kit = kit;

        this.ffa = ffa;

        // close arena from everyone else
        arena.setOpen(false);

        // party event match if this is true
        if (party != null
                || teamOneParam == null
                || teamTwoParam == null
                || teamOneParam.isEmpty()
                || teamTwoParam.isEmpty()) {

            // Create list of members include the party leader
            List<Profile> partyMembers = new ArrayList<>(party.getMembers());
            partyMembers.add(party.getLeader());

            // mix order of party members
            Collections.shuffle(partyMembers);

            int i = 0;
            for (Profile profile : partyMembers) {
                profileMap.put(profile, true);

                if (i % 2 == 0) {
                    teamOne.put(profile, true);
                } else {
                    teamTwo.put(profile, true);
                }
                i++;
            }

        } else {
            teamOneParam.forEach(profile -> {
                teamOne.put(profile, true);
                profileMap.put(profile, true);
            });

            teamTwoParam.forEach(profile -> {
                teamTwo.put(profile, true);
                profileMap.put(profile, true);
            });
        }

        for (Profile profile : profileMap.keySet()) {
            prepareProfile(profile);
        }

        startMatch();

        /*
        matchTask = new BukkitRunnable(){
            @Override
            public void run(){

                if (ffa) {

                    List<Profile> ffaAlive = getAlivePlayers(profileMap);

                    if (ffaAlive.size() <= 1 && matchState != MatchState.ENDED) {
                        endMatch();
                        return;
                    }

                    if (matchState == MatchState.STARTING) {
                        if (duration-- > 0) {
                            ffaAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {
                            // getting ready is over, match starts
                            duration = 600; // 10 minute match
                            matchState = MatchState.ONGOING;

                            ffaAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            cancel();
                        }
                    }
                } else {

                    List<Profile> teamOneAlive = getAlivePlayers(teamOne);
                    List<Profile> teamTwoAlive = getAlivePlayers(teamTwo);

                    if (teamOneAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team two wins

                        endMatch();
                        return;
                    }

                    if (teamTwoAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team one wins

                        endMatch();
                        return;
                    }

                    if (matchState == MatchState.STARTING) {
                        if (duration-- > 0) {
                            teamOneAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {
                            // getting ready is over, match starts
                            duration = 600; // 10 minute match
                            matchState = MatchState.ONGOING;

                            teamOneAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);

         */
    }

    public void eliminate(Profile profile) {

        if (matchState == MatchState.ENDED) {
            handleEnded(profile);
            return;
        }

        // profile is already eliminated
        if (!profileMap.get(profile)) {
            return;
        }

        profileMap.put(profile, false);

        if (teamOne.containsKey(profile)) {
            teamOne.put(profile, false);
        }

        if (teamTwo.containsKey(profile)) {
            teamTwo.put(profile, false);
        }

        profile.getPlayer().getWorld().strikeLightningEffect(profile.getPlayer().getLocation());
        profile.getPlayer().setVelocity(profile.getPlayer().getVelocity().multiply(3.1));

        if (profile.getLastAttacker() == null) {
            profile.setLastAttacker(getOpponents(profile).get(0));
        }

        if (party != null) {

            profile.setHits(0);

            party.sendPartyMessage(" ");
            party.sendPartyMessage("&7(Party) &c"
                    + profile.getPlayer().getName() + " &7was eliminated by &c"
                    + profile.getLastAttacker().getPlayer().getName() + "&7.");
            party.sendPartyMessage(" ");


            if (ffa) {
                if (getAlivePlayers(profileMap).size() == 1) {
                    // winner

                    endMatch();
                }
            } else {

                if (getAlivePlayers(teamOne).isEmpty()) {
                    // team two wins
                    endMatch();
                } else if (getAlivePlayers(teamTwo).isEmpty()) {
                    // team one wins
                    endMatch();
                }
            }

        } else {

            List<Profile> opponents = getAliveOpponents(profile);

            if (opponents.isEmpty()) {
                return;
            }

            Profile winner = opponents.get(0);

            winner.setHits(0);
            profile.setHits(0);

            // update winner leaderboard data
            Practice.get().getLeaderboardManager().updateLeaderboardPlayer(
                    winner.getMatch().getKit(),
                    winner.getPlayer(),
                    1,
                    0);

            // update loser leaderboard data
            Practice.get().getLeaderboardManager().updateLeaderboardPlayer(
                    profile.getMatch().getKit(),
                    profile.getPlayer(),
                    0,
                    1);

            profile.sendMessage("&7");
            profile.sendMessage(Practice.PRIMARY_COLOR
                    + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                    + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + ".");
            profile.sendMessage("&7");

            winner.sendMessage("&7");
            winner.sendMessage(Practice.PRIMARY_COLOR
                    + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                    + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + ".");
            winner.sendMessage("&7");

            endMatch();
        }
    }

    public void startMatch(){

        if (kit instanceof BridgeKit && matchState != MatchState.STARTING) {

            matchState = MatchState.STARTING;

            teamOne.keySet().forEach(profile -> {
                PlayerUtil.resetPlayer(profile.getPlayer());
                profile.teleport(arena.getPosition1());
                kit.giveKit(profile);
            });

            teamTwo.keySet().forEach(profile -> {
                PlayerUtil.resetPlayer(profile.getPlayer());
                profile.teleport(arena.getPosition2());
                kit.giveKit(profile);
            });
        }

        matchTask = new BukkitRunnable(){
            @Override
            public void run(){

                if (ffa) {

                    List<Profile> ffaAlive = getAlivePlayers(profileMap);

                    if (ffaAlive.size() <= 1 && matchState != MatchState.ENDED) {
                        endMatch();
                        return;
                    }

                    if (matchState == MatchState.STARTING) {
                        if (duration-- > 0) {
                            ffaAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {
                            // getting ready is over, match starts
                            duration = 600; // 10 minute match
                            matchState = MatchState.ONGOING;

                            ffaAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            cancel();
                        }
                    }
                } else {

                    List<Profile> teamOneAlive = getAlivePlayers(teamOne);
                    List<Profile> teamTwoAlive = getAlivePlayers(teamTwo);

                    if (teamOneAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team two wins

                        endMatch();
                        return;
                    }

                    if (teamTwoAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team one wins

                        endMatch();
                        return;
                    }

                    if (matchState == MatchState.STARTING) {
                        if (duration-- > 0) {
                            teamOneAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {
                                profile.sendMessage(Practice.SECONDARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.SECONDARY_COLOR + " seconds...");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {
                            // getting ready is over, match starts
                            duration = 600; // 10 minute match
                            matchState = MatchState.ONGOING;

                            teamOneAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                PlayerUtil.sendTitle(profile.getPlayer(),
                                        "&c&lFight!",
                                        0, 10, 5);
                            });

                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    public void endMatch(){
        duration = 3;
        matchState = MatchState.ENDED;

        matchTask = new BukkitRunnable(){
            @Override
            public void run(){
                if (duration-- <= 0) { // 3 seconds before teleporting
                    // fully end the match (teleport players and reset them)

                    profileMap.keySet().forEach(profile -> {
                        handleEnded(profile);
                    });

                    Practice.get().getMatchManager().removeMatch(Match.this);
                    cancel();
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    private void handleStarting(){}

    private void handleEnded(Profile profile){
        if (profile.getPlayer().isOnline()) {
            profile.teleport(Practice.get().getConfigManager().getMainSpawn());
            profile.setPlayerState(PlayerState.IN_SPAWN);
            profile.setMatch(null);
            profile.setLastAttacker(null);
            profile.setKnockback(KnockbackModule.getDefault().title);
            PlayerUtil.resetPlayer(profile.getPlayer());

            Practice.get().getProfileManager().getProfileMap().values()
                    .forEach(others -> {
                        if (profile != others) {
                            others.getPlayer().showPlayer(profile.getPlayer());
                        }
                    });

            if (party == null) {
                InventoryUtil.setSpawnInventory(profile.getPlayer());
            } else {
                InventoryUtil.setPartyInventory(profile.getPlayer());
            }

        }

        Practice.get().getQueueManager().getPlayersInMatch().remove(profile);
    }

    public List<Profile> getAlivePlayers(Map<Profile, Boolean> team){
        List<Profile> alive = new ArrayList<>();
        for (Map.Entry<Profile, Boolean> entry : team.entrySet()) {
            if (entry.getValue()) {
                alive.add(entry.getKey());
            }
        }
        return alive;
    }

    public List<Profile> getAliveOpponents(Profile profile) {
        List<Profile> opponents = new ArrayList<>();

        if (ffa) {
            profileMap.forEach((opponent, alive) -> {
                if (opponent != profile) {
                    if (alive)
                        opponents.add(opponent);
                }
            });
        }

        if (teamOne.containsKey(profile)) {
            teamTwo.forEach((opponent, alive) -> {
                if (alive)
                    opponents.add(opponent);
            });
        }

        if (teamTwo.containsKey(profile)) {
            teamOne.forEach((opponent, alive) -> {
                if (alive)
                    opponents.add(opponent);
            });
        }

        return opponents;
    }

    public List<Profile> getOpponents(Profile profile){
        List<Profile> opponents = new ArrayList<>();

        if (ffa) {
            profileMap.forEach((opponent, alive) -> {
                if (opponent != profile) {
                    opponents.add(opponent);
                }
            });
        }

        if (teamOne.containsKey(profile)) {
            teamTwo.forEach((opponent, alive) -> {
                opponents.add(opponent);
            });
        }

        if (teamTwo.containsKey(profile)) {
            teamOne.forEach((opponent, alive) -> {
                opponents.add(opponent);
            });
        }

        return opponents;
    }
}
