package net.syphlex.practice.manager.match;

import com.ngxdev.knockback.KnockbackModule;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
import net.syphlex.practice.manager.kit.impl.ComboKit;
import net.syphlex.practice.manager.party.Party;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Match {

    private final List<Profile> spectators = new ArrayList<>();

    private final Map<Profile, Boolean> profileMap = new ConcurrentHashMap<>();
    private final Map<Profile, Boolean> teamOne = new ConcurrentHashMap<>();
    private final Map<Profile, Boolean> teamTwo = new ConcurrentHashMap<>();

    private final List<Block> placedBlocks = new ArrayList<>();

    private final Party party;
    private final Arena arena;
    private final Kit kit;
    private boolean queuedMatch;
    private final boolean ffa;

    private int duration = 0;

    private int teamOneScore = 0, teamTwoScore = 0;

    private MatchState matchState = MatchState.STARTING;

    private BukkitTask matchTask;

    private boolean preparing;

    private void prepareProfile(Profile profile) {

        profile.getDuelRequests().clearRequests();

        if (teamOne.containsKey(profile)) {
            profile.teleport(arena.getPosition1());
        }

        if (teamTwo.containsKey(profile)) {
            profile.teleport(arena.getPosition2());
        }

        profile.setPlayerState(PlayerState.IN_MATCH);

        profile.setMatch(this);

        PlayerUtil.resetPlayer(profile.getPlayer());

        if (CitizensAPI.getNPCRegistry().isNPC(profile.getPlayer())) {
            kit.giveKit(profile);
        } else {
            kit.giveBookKits(profile);
        }

        if (kit instanceof ComboKit) {
            profile.setKnockback("combo");
        }

        if (kit instanceof BridgeKit) {
            profile.setKnockback("bridge");
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
        if (party != null) {

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

                prepareProfile(profile);
                Practice.get().getQueueManager().removeFromQueueMap(profile);

                i++;
            }

        } else {

            teamOneParam.forEach(profile -> {
                teamOne.put(profile, true);
                profileMap.put(profile, true);

                prepareProfile(profile);
                for (Profile others : teamTwoParam) {
                    PlayerUtil.showPlayer(profile.getPlayer(), others.getPlayer());
                }
            });

            teamTwoParam.forEach(profile -> {
                teamTwo.put(profile, true);
                profileMap.put(profile, true);

                prepareProfile(profile);
                for (Profile others : teamOneParam) {
                    PlayerUtil.showPlayer(profile.getPlayer(), others.getPlayer());
                }
            });
        }

        startMatch();
    }

    public void addSpectator(Profile profile){
        spectators.add(profile);

        profileMap.keySet().forEach(matchPlayers -> {
            matchPlayers.sendMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                    + Practice.SECONDARY_COLOR + " is now spectating. &7("
                    + spectators.size() + ")");
        });
    }

    public void removeSpectator(Profile profile){
        spectators.remove(profile);

        profileMap.keySet().forEach(matchPlayers -> {
            matchPlayers.sendMessage(Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                    + Practice.SECONDARY_COLOR + " is no longer spectating. &7("
                    + spectators.size() + ")");
        });
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

        //profile.getPlayer().getWorld().strikeLightningEffect(profile.getPlayer().getLocation());
        //profile.getPlayer().setVelocity(profile.getPlayer().getVelocity().multiply(4.5));

        if (profile.getLastAttacker() == null) {
            profile.setLastAttacker(getOpponents(profile).get(0));
        }

        if (party != null) {

            profile.setHits(0);

            party.sendPartyMessage(Practice.TERTIARY_COLOR + "(Party) "
                    + Practice.PRIMARY_COLOR + profile.getPlayer().getName()
                    + Practice.SECONDARY_COLOR + " was eliminated by "
                    + Practice.PRIMARY_COLOR + profile.getLastAttacker().getPlayer().getName()
                    + Practice.SECONDARY_COLOR + ".");


            if (ffa) {
                if (getAlivePlayers(profileMap).size() == 1) {
                    // winner

                    endMatch(false);
                }
            } else {

                if (getAlivePlayers(teamOne).isEmpty()) {
                    // team two wins
                    endMatch(false);
                } else if (getAlivePlayers(teamTwo).isEmpty()) {
                    // team one wins
                    endMatch(false);
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

            if (queuedMatch) {

                int loserElo = Practice.get().getLeaderboardManager().getElo(profile.getPlayer().getUniqueId(), kit);
                int winnerElo = Practice.get().getLeaderboardManager().getElo(winner.getPlayer().getUniqueId(), kit);

                // update winner leaderboard data
                Practice.get().getLeaderboardManager().updateLeaderboardPlayer(
                        winner.getMatch().getKit(),
                        winner,
                        1,
                        0);

                // update loser leaderboard data
                if (Practice.get().getLeaderboardManager().getLeaderboardPlayer(kit, winner.getPlayer().getUniqueId()).getMatchesPlayed() > 5) {

                    Practice.get().getLeaderboardManager().updateLeaderboardPlayer(
                            profile.getMatch().getKit(),
                            profile,
                            0,
                            1);

                    profile.sendMessage(" ");
                    profile.sendMessage(Practice.PRIMARY_COLOR
                            + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + "."
                            + " &c(" + (Practice.get().getLeaderboardManager().getElo(profile.getPlayer().getUniqueId(), kit) - loserElo) + " Elo)");
                    profile.sendMessage(" ");
                    profile.sendMessage(" &aWinner: " + Practice.SECONDARY_COLOR
                            + winner.getPlayer().getName() + " &7| &cLoser: "
                            + profile.getPlayer().getName());
                    profile.sendMessage(" ");

                } else {
                    profile.sendMessage(" ");
                    profile.sendMessage(Practice.PRIMARY_COLOR
                            + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                            + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + ".");
                    profile.sendMessage("&cYour opponent has not played more than 5 matches resulting in you not losing any elo.");
                    profile.sendMessage(" ");
                    profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch Results &7(Click to view)");
                    profile.sendMessage(" &aWinner: " + Practice.SECONDARY_COLOR
                            + winner.getPlayer().getName() + " &7| &cLoser: "
                            + profile.getPlayer().getName());
                    profile.sendMessage(" ");
                }

                winner.sendMessage(" ");
                winner.sendMessage(Practice.PRIMARY_COLOR
                        + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + "."
                        + " &a(+" + (Practice.get().getLeaderboardManager().getElo(winner.getPlayer().getUniqueId(), kit) - winnerElo) + " Elo)");
                winner.sendMessage(" ");
            } else {

                profile.sendMessage(" ");
                profile.sendMessage(Practice.PRIMARY_COLOR
                        + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + ".");
                profile.sendMessage(" ");

                winner.sendMessage(" ");
                winner.sendMessage(Practice.PRIMARY_COLOR
                        + profile.getPlayer().getName() + Practice.SECONDARY_COLOR + " was killed by "
                        + Practice.PRIMARY_COLOR + winner.getPlayer().getName() + Practice.SECONDARY_COLOR + ".");
                winner.sendMessage(" ");
            }

            winner.sendMessage(Practice.PRIMARY_COLOR + "&lMatch Results &7(Click to view)");
            winner.sendMessage(" &aWinner: " + Practice.SECONDARY_COLOR
                    + winner.getPlayer().getName() + " &7| &cLoser: "
                    + profile.getPlayer().getName());
            winner.sendMessage(" ");

            profile.getPlayer().getInventory().setItem(0, ItemUtil.getPlayAgainItem());

            endMatch(false);
        }
    }

    public void startMatch(){

        if (preparing) {
            return;
        }

        preparing = true;

        duration = 5;

        if (kit instanceof BridgeKit && matchState != MatchState.STARTING) {

            if (teamOneScore >= 5) {
                profileMap.keySet().forEach(profile -> {
                    profile.sendTitle(
                            Practice.PRIMARY_COLOR + "&lTeam One Wins!",
                            0, 10, 5);
                });
                endMatch(false);
                return;
            } else if (teamTwoScore >= 5) {
                profileMap.keySet().forEach(profile -> {
                    profile.sendTitle(
                            Practice.PRIMARY_COLOR + "&lTeam Two Wins!",
                            0, 10, 5);
                });
                endMatch(false);
                return;
            }

            matchState = MatchState.STARTING;
            duration = 4;

            teamOne.keySet().forEach(profile -> {
                PlayerUtil.resetPlayer(profile.getPlayer());
                profile.teleport(arena.getPosition1());
                kit.giveKit(profile);
                profile.getPlayer().updateInventory();
            });

            teamTwo.keySet().forEach(profile -> {
                PlayerUtil.resetPlayer(profile.getPlayer());
                profile.teleport(arena.getPosition2());
                kit.giveKit(profile);
                profile.getPlayer().updateInventory();
            });
        }

        matchTask = new BukkitRunnable(){
            @Override
            public void run(){

                if (ffa) {

                    List<Profile> ffaAlive = getAlivePlayers(profileMap);

                    if (ffaAlive.size() <= 1 && matchState != MatchState.ENDED) {
                        endMatch(false);
                        return;
                    }

                    if (matchState == MatchState.STARTING) {
                        if (duration-- > 0) {
                            ffaAlive.forEach(profile -> {

                                if (duration == 4) {
                                    profile.sendMessage(" ");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                            + Practice.SECONDARY_COLOR + "Kit: "
                                            + Practice.PRIMARY_COLOR + ChatColor.stripColor(kit.getName()));

                                    if (getOpponents(profile).size() == 1) {

                                        final Profile opponent = getOpponents(profile).get(0);

                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponent: &c"
                                                + opponent.getPlayer().getName() + " &7("
                                                + Practice.get().getLeaderboardManager().getElo(
                                                        opponent.getPlayer().getUniqueId(), kit)
                                                + " Elo)");
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Ping: "
                                                + Practice.PRIMARY_COLOR + opponent.getPing() + "ms");
                                        profile.sendMessage(" ");

                                    } else {
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponents:");
                                        for (Profile opponents : getOpponents(profile)) {
                                            profile.sendMessage(Practice.SECONDARY_COLOR + "    » &c"
                                                    + opponents.getPlayer().getName()
                                                    + " &7("
                                                    + Practice.get().getLeaderboardManager().getElo(
                                                            opponents.getPlayer().getUniqueId(), kit)
                                                    + " Elo)");
                                        }
                                        profile.sendMessage(" ");
                                    }
                                }

                                profile.sendMessage(Practice.QUATERNARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.QUATERNARY_COLOR + " seconds...");
                                profile.sendTitle(
                                        Practice.PRIMARY_COLOR + "&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {

                            matchState = MatchState.ONGOING;

                            ffaAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                profile.sendTitle(
                                        "&c",
                                        0, 10, 5);
                            });

                            matchTask.cancel();
                        }
                    }
                } else {

                    List<Profile> teamOneAlive = getAlivePlayers(teamOne);
                    List<Profile> teamTwoAlive = getAlivePlayers(teamTwo);

                    if (teamOneAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team two wins

                        endMatch(false);
                        return;
                    }

                    if (teamTwoAlive.isEmpty() && matchState != MatchState.ENDED) {

                        // team one wins

                        endMatch(false);
                        return;
                    }

                    if (matchState == MatchState.STARTING) {

                        if (duration-- > 0) {

                            if (kit instanceof BridgeKit
                                    && duration == 3
                                    && (teamOneScore != 0
                                    || teamTwoScore != 0)) {
                                return;
                            }

                            teamOneAlive.forEach(profile -> {

                                if (duration == 4) {
                                    profile.sendMessage(" ");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                            + Practice.SECONDARY_COLOR + "Kit: "
                                            + Practice.PRIMARY_COLOR + ChatColor.stripColor(kit.getName()));

                                    if (getOpponents(profile).size() == 1) {

                                        final Profile opponent = getOpponents(profile).get(0);

                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponent: &c"
                                                + opponent.getPlayer().getName() + " &7("
                                                + Practice.get().getLeaderboardManager().getElo(
                                                opponent.getPlayer().getUniqueId(), kit)
                                                + " Elo)");
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Ping: "
                                                + Practice.PRIMARY_COLOR + opponent.getPing() + "ms");
                                        profile.sendMessage(" ");

                                    } else {
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponents:");
                                        for (Profile opponents : getOpponents(profile)) {
                                            profile.sendMessage(Practice.SECONDARY_COLOR + "    » &c"
                                                    + opponents.getPlayer().getName()
                                                    + " &7("
                                                    + Practice.get().getLeaderboardManager().getElo(
                                                    opponents.getPlayer().getUniqueId(), kit)
                                                    + " Elo)");
                                        }
                                        profile.sendMessage(" ");
                                    }
                                }

                                profile.sendMessage(Practice.QUATERNARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.QUATERNARY_COLOR + " seconds...");
                                profile.sendTitle(
                                        "&c&l" + (duration + 1),
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {

                                if (duration == 4) {
                                    profile.sendMessage(" ");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + "&lMatch");
                                    profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                            + Practice.SECONDARY_COLOR + "Kit: "
                                            + Practice.PRIMARY_COLOR + ChatColor.stripColor(kit.getName()));

                                    if (getOpponents(profile).size() == 1) {

                                        final Profile opponent = getOpponents(profile).get(0);

                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponent: &c"
                                                + opponent.getPlayer().getName() + " &7("
                                                + Practice.get().getLeaderboardManager().getElo(
                                                opponent.getPlayer().getUniqueId(), kit)
                                                + " Elo)");
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Ping: "
                                                + Practice.PRIMARY_COLOR + opponent.getPing() + "ms");
                                        profile.sendMessage(" ");

                                    } else {
                                        profile.sendMessage(Practice.PRIMARY_COLOR + " » "
                                                + Practice.SECONDARY_COLOR + "Opponents:");
                                        for (Profile opponents : getOpponents(profile)) {
                                            profile.sendMessage(Practice.SECONDARY_COLOR + "    » &c"
                                                    + opponents.getPlayer().getName()
                                                    + " &7("
                                                    + Practice.get().getLeaderboardManager().getElo(
                                                    opponents.getPlayer().getUniqueId(), kit)
                                                    + " Elo)");
                                        }
                                        profile.sendMessage(" ");
                                    }
                                }

                                profile.sendMessage(Practice.QUATERNARY_COLOR
                                        + "Match starting in "
                                        + Practice.PRIMARY_COLOR + (duration + 1)
                                        + Practice.QUATERNARY_COLOR + " seconds...");
                                profile.sendTitle(
                                        "&c&l" + (duration + 1),
                                        0, 10, 5);
                            });
                        }

                        if (duration < 0) {

                            matchState = MatchState.ONGOING;

                            teamOneAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                profile.sendTitle(
                                        "&c",
                                        0, 10, 5);
                            });

                            teamTwoAlive.forEach(profile -> {
                                profile.sendMessage("&aMatch started!");
                                profile.sendTitle(
                                        "&c",
                                        0, 10, 5);
                            });

                            preparing = false;
                            matchTask.cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    public void endMatch(boolean force){

        if (force) {
            profileMap.keySet().forEach(this::handleEnded);
            spectators.forEach(this::handleEnded);

            // todo fix this for performance
            for (Block block : placedBlocks) {
                block.setType(Material.AIR);
            }

            arena.setOpen(true);
            Practice.get().getMatchManager().removeMatch(Match.this);
            return;
        }

        duration = 3;
        matchState = MatchState.ENDED;

        if (party == null && !ffa && queuedMatch) {
            profileMap.keySet().forEach(profile -> {
                profile.getPlayer().getInventory().setItem(0, ItemUtil.getPlayAgainItem());
            });
        }

        profileMap.keySet().forEach(profile -> {
            profile.setLastMatchKit(kit);
            profile.setMatch(null);
            profile.setHits(0);
            profile.setLastAttacker(null);
            profile.setKnockback(KnockbackModule.getDefault().title);
            Practice.get().getQueueManager().getPlayersInMatch().remove(profile);
        });

        matchTask = new BukkitRunnable(){
            @Override
            public void run(){

                if (duration-- <= 0) { // 3 seconds before teleporting
                    // fully end the match (teleport players and reset them)

                    profileMap.keySet().forEach(profile -> {
                        handleEnded(profile);
                    });

                    // todo fix this for performance
                    for (Block block : placedBlocks) {
                        block.setType(Material.AIR);
                    }

                    arena.setOpen(true);
                    Practice.get().getMatchManager().removeMatch(Match.this);
                    matchTask.cancel();
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    private void handleEnded(Profile profile){
        if (profile.getPlayer().isOnline() && !profile.isInMatch()) {
            profile.teleport(Practice.get().getConfigManager().getMainSpawn());
            profile.setPlayerState(PlayerState.IN_SPAWN);
            //profile.setMatch(null);
            //profile.setHits(0);
            //profile.setLastAttacker(null);
            //profile.setKnockback(KnockbackModule.getDefault().title);
            PlayerUtil.resetPlayer(profile.getPlayer());

            for (Profile others : Practice.get().getProfileManager().getProfileMap().values()) {
                if (others != profile) {
                    PlayerUtil.showPlayer(profile.getPlayer(), others.getPlayer());
                }
            }

            Practice.get().getProfileManager().getProfileMap().values()
                    .forEach(others -> {
                        if (profile != others) {
                            PlayerUtil.showPlayer(others.getPlayer(), profile.getPlayer());
                            PlayerUtil.showPlayer(profile.getPlayer(), others.getPlayer());
                            //profile.getPlayer().showPlayer(others.getPlayer());
                            //others.getPlayer().showPlayer(profile.getPlayer());
                        }
                    });

            if (party == null) {
                InventoryUtil.setSpawnInventory(profile.getPlayer());
            } else {
                InventoryUtil.setPartyInventory(profile.getPlayer());
            }
        }

    }

    public boolean isTeamOne(Profile profile){
        return teamOne.containsKey(profile);
    }

    public boolean isTeamTwo(Profile profile){
        return teamTwo.containsKey(profile);
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

    public List<Profile> getTeamAlive(Profile profile){
        List<Profile> team = new ArrayList<>();
        if (isTeamOne(profile)) {
            teamOne.forEach((players, alive) -> {
                if (alive) {
                    team.add(players);
                }
            });
        } else if (isTeamTwo(profile)){
            teamTwo.forEach((players, alive) -> {
                if (alive) {
                    team.add(players);
                }
            });
        }
        return team;
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
        } else {

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
        }

        return opponents;
    }
}
