package net.syphlex.practice.manager.event.impl;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.ProfileDamageEvent;
import net.syphlex.practice.manager.event.EventInfo;
import net.syphlex.practice.manager.event.PracticeEvent;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.Pair;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SumoEvent extends PracticeEvent {

    //private Profile[] team1;
    //private Profile[] team2;

    private boolean preparingTeams = true;

    public SumoEvent(EventInfo info, YamlConfiguration config) {
        super(info, config);
    }

    @Override
    public void startEvent(){
        //team1 = new Profile[teamSize];
        //team2 = new Profile[teamSize];
    }

    @Override
    public void run(){
        super.run();

        switch (eventState) {
            case ONGOING:

                if (gameTime <= 0) {

                    if (preparingTeams) {

                        round++;

                        List<Profile> participants = getEventAlive();

                        int teamCount = 0;
                        int teamIndex = 1;

                        for (Profile player : participants) {
                            if (teamCount == teamSize) {
                                teamCount = 0;
                                teamIndex = (teamIndex == 1) ? 2 : 1;
                            }

                            setTeam(player, teamIndex);
                            teamCount++;
                        }

                        gameTime = 6;
                    } else {
                        sendEventMessage("&aRound started!");
                    }
                } else if (gameTime < 6) {

                    sendEventMessage("Round starting in " + gameTime + " seconds...");

                    gameTime = 120;
                }

                /*else {

                    List<Profile> teamOne = getTeamAlive(1);
                    List<Profile> teamTwo = getTeamAlive(2);

                    if (teamOne.isEmpty()) {
                        // team two wins round

                    } else if (teamTwo.isEmpty()) {
                        // team one wins round

                    }
                }
                 */

                break;
        }
    }

    @EventHandler
    public void onProfileDamageEvent(ProfileDamageEvent e){

        if (e.getVictim() == null) {
            return;
        }

        final Profile attacker = e.getAttacker();
        final Profile victim = e.getVictim();

        if (attacker == null) {
            //e.setCancelled(true);
            return;
        }


    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e){

        final Player p = e.getPlayer();
        final Profile profile = Practice.get().getProfileManager().get(p);

        if (isAlive(profile)) {

            //if (Arrays.asList(team1).contains(profile)) {

                if (e.getTo().getY() <= 0) {
                    eliminate(profile.getLastAttacker(), profile);
                }

            //}

            //if (Arrays.asList(team2).contains(profile)) {

                if (e.getTo().getY() <= 0) {
                    eliminate(profile.getLastAttacker(), profile);
                }
            //}
        }
    }
}
