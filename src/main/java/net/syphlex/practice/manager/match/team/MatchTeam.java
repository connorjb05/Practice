package net.syphlex.practice.manager.match.team;

import com.ngxdev.knockback.KnockbackModule;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.ladder.impl.ComboLadder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.InventorySnapshot;
import net.syphlex.practice.manager.profile.objects.PlayerState;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class MatchTeam {

    private final Map<Profile, Boolean> teamMap = new ConcurrentHashMap<>();

    private int score = 0, teamNumber = 0;

    private boolean hasJustScored = false, hasBed = true;

    private String teamColor = "", teamName = "";

    public MatchTeam(String teamColor, String teamName, int teamNumber){
        this.teamColor = teamColor;
        this.teamName = teamName;
        this.teamNumber = teamNumber;
    }

    public void add(Profile profile){
        teamMap.put(profile, true);
    }

    public void remove(Profile profile){
        teamMap.remove(profile);
    }

    public void setEliminated(Profile profile){
        teamMap.put(profile, false);
    }

    public void startEndOfMatch(Match match){

        if (teamMap.isEmpty()) {
            return;
        }

        for (Profile profile : teamMap.keySet()) {
            profile.setMatch(null); // Remove the match reference from the player's profile
            profile.setLastAttacker(null); // Clear the last attacker (if applicable)
            profile.setLastMatchLadder(match.getLadder()); // Record the ladder used in the last match
            profile.setKnockback(KnockbackModule.getDefault().title); // Reset knockback settings
            profile.setPlayerState(PlayerState.IN_SPAWN); // Set player state to spawn (they're out of the match)

            // Add this matches post match data
            profile.getPostMatchInventories().put(match.getUuid(), match.getMatchInventories());

            // If any remaining players in the match are not in the post match inventory data
            // we will add them here.
            if (!profile.getPostMatchInventories().get(match.getUuid()).getInventories().containsKey(profile.getPlayer().getUniqueId())) {
                profile.getPostMatchInventories().get(match.getUuid()).getInventories().put(
                        profile.getPlayer().getUniqueId(),
                        new InventorySnapshot(profile));
            }

            // Remove the player from the queue manager's list of players in a match
            Practice.get().getQueueManager().getPlayersInMatch().remove(profile);
        }
    }

    public void deletePlayer(Profile profile) {
        profile.reset();
        profile.setLobbyInventory();
        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
        profile.setPlayerState(PlayerState.IN_SPAWN);
    }

    public void deletePlayers(){

        if (teamMap.isEmpty()) {
            return;
        }

        for (Profile profile : teamMap.keySet()) {
            if (!profile.isInMatch()) {
                deletePlayer(profile);
            }
        }
    }

    public void setupPlayer(Profile profile, Match match) {
        profile.getDuelRequests().clearRequests();

        profile.setMatch(match);
        profile.setPlayerState(PlayerState.IN_MATCH);
        profile.setHits(0);
        profile.setLongestCombo(0);
        profile.setMissedPotions(0);
        profile.setUnmissedPotions(0);
        profile.setSwings(0);
        profile.setLastAttacker(null);
        profile.setLadderQueued(null);

        profile.reset();

        if (match.getLadder() instanceof ComboLadder) {
            profile.setKnockback("combo");
        }

        profile.teleport(teamNumber == 0 ? match.getArena().getSpectate() : teamNumber == 1
                ? match.getArena().getPosition1() : match.getArena().getPosition2());

        match.getLadder().giveBookKits(profile);
    }

    public void setupPlayers(Match match){

        if (teamMap.isEmpty()) {
            return;
        }

        for (Profile profile : teamMap.keySet()) {
            setupPlayer(profile, match);
        }
    }

    public boolean isInTeam(Profile profile){
        return teamMap.containsKey(profile);
    }

    public int getTeamHits(){
        int hits = 0;
        for (Profile profile : teamMap.keySet()) {
            hits += profile.getHits();
        }
        return hits;
    }

    public int getCount(){
        return teamMap.size();
    }

    public int getAliveCount(){
        return getAliveList().size();
    }

    public boolean isAlive(Profile profile){
        return teamMap.getOrDefault(profile, false);
    }

    public void hideFromTeam(Profile profile){
        for (Profile teamMembers : teamMap.keySet()) {
            teamMembers.hide(profile);
        }
    }

    public void showToTeam(Profile profile){
        for (Profile teamMembers : teamMap.keySet()) {
            teamMembers.show(profile);
        }
    }

    public void sendClickableMessage(String message, String command){
        teamMap.keySet().forEach(profile -> profile.sendClickableMessage(message, command));
    }

    public void sendMessage(String message){
        teamMap.keySet().forEach(profile -> profile.sendMessage(message));
    }

    public void sendTitle(String title){
        teamMap.keySet().forEach(profile -> profile.sendTitle(title, 5, 10, 5));
    }

    public void sendTitle(String title, String subTitle){
        teamMap.keySet().forEach(profile -> profile.sendTitle(title, subTitle, 5, 10, 5));
    }

    public void sendSound(Sound sound){
        teamMap.keySet().forEach(profile -> profile.getPlayer()
                .playSound(profile.getPlayer().getLocation(), sound, 7.5f, 2f));
    }

    public List<Profile> getAliveList(){
        List<Profile> players = new ArrayList<>();
        for (Map.Entry<Profile, Boolean> entry : teamMap.entrySet()) {
            if (entry.getValue()) {
                players.add(entry.getKey());
            }
        }
        return players;
    }

    public List<Profile> getListWithoutProfile(Profile profile){
        List<Profile> players = new ArrayList<>();
        for (Profile key : teamMap.keySet()) {
            if (key != profile) {
                players.add(key);
            }
        }
        return players;
    }

    public List<Profile> getAsList(){
        return new ArrayList<>(teamMap.keySet());
    }
}
