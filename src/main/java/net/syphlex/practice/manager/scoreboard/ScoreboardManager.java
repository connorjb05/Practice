package net.syphlex.practice.manager.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.impl.BoxingLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.match.MatchState;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.manager.profile.objects.PlayerSettings;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager {

    public void onEnable(){
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Profile profile : Practice.get().getProfileManager().getProfileMap().values()) {

                    // scoreboard either not loaded yet or is disabled
                    if (profile.getScoreboard() == null) {
                        continue;
                    }

                    if (!profile.getSetting(PlayerSettings.SCOREBOARD)) {
                        if (!profile.getScoreboard().isDeleted()) {
                            profile.getScoreboard().delete();
                        }
                        continue;
                    } else if (profile.getScoreboard().isDeleted()) {
                        profile.setScoreboard(new FastBoard(profile.getPlayer()));
                    }

                    profile.getScoreboard().updateTitle(StringUtil.CC(Practice.PRIMARY_COLOR + "&lSyphlex &7❘ &fPractice"));

                    if (profile.isInMatch()) {
                        profile.getScoreboard().updateLines(getInMatchScoreboardLines(profile));
                    } else {
                        profile.getScoreboard().updateLines(getMainScoreboardLines(profile));
                    }
                }
            }
        }.runTaskTimer(Practice.get(), 0L, 20L);
    }

    public List<String> getMainScoreboardLines(Profile profile) {
        List<String> lines = new ArrayList<>();
        lines.add("&f&m-------------------"); // Top line

        lines.add(" ");
        lines.add(Practice.PRIMARY_COLOR + "» "
                + Practice.SECONDARY_COLOR + "Region: "
                + Practice.PRIMARY_COLOR + "NA");
        lines.add(" ");

        lines.add(Practice.PRIMARY_COLOR + "» "
                + Practice.SECONDARY_COLOR + "Online: "
                + Practice.PRIMARY_COLOR + Bukkit.getOnlinePlayers().size());

        lines.add(Practice.PRIMARY_COLOR + "» "
                + Practice.SECONDARY_COLOR + "In Match: "
                + Practice.PRIMARY_COLOR + Practice.get().getMatchManager().getTotalInMatch());

        lines.add(Practice.PRIMARY_COLOR + "» "
                + Practice.SECONDARY_COLOR + "In Queue: "
                + Practice.PRIMARY_COLOR + Practice.get().getQueueManager().getTotalInQueue());

        lines.add(" ");
        lines.add(Practice.PRIMARY_COLOR + "syphlex.net");
        lines.add("&f&m-------------------"); // Bottom line with unique padding
        return StringUtil.CC(lines);
    }

    public List<String> getInMatchScoreboardLines(Profile profile) {
        List<String> lines = new ArrayList<>();

        if (profile.isInParty() && profile.isInMatch()) {
            lines.add("&f&m-------------------"); // Top
            lines.add(Practice.PRIMARY_COLOR + "» "
                    + Practice.SECONDARY_COLOR
                    + "Ladder: " + Practice.PRIMARY_COLOR
                    + ChatColor.stripColor(profile.getMatch().getLadder().getName()));
            lines.add(Practice.PRIMARY_COLOR + "» "
                    + Practice.SECONDARY_COLOR
                    + "Your Ping: " + Practice.PRIMARY_COLOR
                    + profile.getPing() + "ms");

            if (!profile.getMatch().isFfa()) {

                int team = profile.getMatch().getProfileMap().get(profile).getX();

                if (!(profile.getMatch().getLadder() instanceof BridgeLadder)) {
                    lines.add(" ");
                    lines.add(Practice.PRIMARY_COLOR + "» "
                            + Practice.SECONDARY_COLOR
                            + "Your Team: "
                            + Practice.PRIMARY_COLOR + profile.getMatch().getAliveFromPlayerTeam(profile).size()
                            + "/" + profile.getMatch().getPlayerTeam(profile).size());
                    lines.add(Practice.PRIMARY_COLOR + "» "
                            + Practice.SECONDARY_COLOR
                            + "Other Team: "
                            + Practice.PRIMARY_COLOR + profile.getMatch().getAliveOpponentList(profile).size()
                            + "/" + profile.getMatch().getOpponentList(profile).size());
                }

                if (profile.getMatch().getLadder() instanceof BoxingLadder) {

                    int teamOneHits = 0;
                    int teamTwoHits = 0;

                    for (Profile t1 : profile.getMatch().getAliveFromTeam(1)) {
                        teamOneHits += t1.getHits();
                    }

                    for (Profile t2 : profile.getMatch().getAliveFromTeam(2)) {
                        teamTwoHits += t2.getHits();
                    }

                    int difference = team == 1 ? teamOneHits - teamTwoHits : teamTwoHits - teamOneHits;

                    String differenceAsString = "&7(0)";

                    if (difference < 0) {
                        differenceAsString = "&c(" + (difference) + ")";
                    } else if (difference > 0) {
                        differenceAsString = "&a(+" + (difference) + ")";
                    }

                    lines.add(" ");
                    lines.add("&b&lHits: " + differenceAsString);
                    lines.add(Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + "Your Team: "
                            + Practice.PRIMARY_COLOR + (team == 1 ? teamOneHits : teamTwoHits));
                    lines.add(Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + "Them: "
                            + Practice.PRIMARY_COLOR + (team == 1 ? teamTwoHits : teamOneHits));
                }
            }

            if (profile.getMatch().getLadder() instanceof BridgeLadder) {
                getBridgeKitBoard(profile, lines);
            }

            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "syphlex.net");
            lines.add("&f&m-------------------"); // Bottom line with unique padding
            return StringUtil.CC(lines);
        }

        if (profile.isInMatch()) {
            lines.add("&f&m-------------------"); // Top line

            if (profile.getMatch().getMatchState() == MatchState.STARTING) {
                lines.add(Practice.PRIMARY_COLOR + "» "
                        + Practice.SECONDARY_COLOR + "Ladder: "
                        + Practice.PRIMARY_COLOR + profile.getMatch().getLadder().getName());
                lines.add(" ");
            }

            lines.add(Practice.PRIMARY_COLOR + "» "
                    + Practice.SECONDARY_COLOR + "Your Ping: "
                    + Practice.PRIMARY_COLOR + profile.getPing() + "ms");

            if (profile.getMatch().getLadder() instanceof BridgeLadder) {
                getBridgeKitBoard(profile, lines);
            } else {
                lines.add(Practice.PRIMARY_COLOR + "» "
                        + Practice.SECONDARY_COLOR + "Their Ping: "
                        + Practice.PRIMARY_COLOR + profile.getMatchOpponent().getPing() + "ms");
            }

            if (profile.getMatch().getLadder() instanceof BoxingLadder) {

                int profileHits = profile.getHits();
                int opponentHits = profile.getMatchOpponent().getHits();

                int difference = profileHits - opponentHits;

                String differenceAsString = "&7(0)";

                if (difference < 0) {
                    differenceAsString = "&c(" + (difference) + ")";
                } else if (difference > 0) {
                    differenceAsString = "&a(+" + (difference) + ")";
                }

                lines.add(" ");
                lines.add(Practice.PRIMARY_COLOR + "&lHits: " + differenceAsString);
                lines.add(Practice.PRIMARY_COLOR + " » "
                        + Practice.SECONDARY_COLOR + "You: "
                        + Practice.PRIMARY_COLOR + profileHits);
                lines.add(Practice.PRIMARY_COLOR + " » "
                        + Practice.SECONDARY_COLOR + "Them: "
                        + Practice.PRIMARY_COLOR + opponentHits);
            }

            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "syphlex.net");
            lines.add("&f&m-------------------"); // Bottom line with unique padding
        }

        return StringUtil.CC(lines);
    }

    private List<String> getBridgeKitBoard(Profile profile, List<String> lines){

        if (profile.getMatch().getLadder() instanceof BridgeLadder) {

            StringBuilder teamOneScoreString = new StringBuilder();
            StringBuilder teamTwoScoreString = new StringBuilder();

            for (int i = 0; i < profile.getMatch().getTeamOneScore(); i++) {
                teamOneScoreString.append("&b⬤");
            }

            for (int i = 0; i < profile.getMatch().getTeamTwoScore(); i++) {
                teamTwoScoreString.append("&b⬤");
            }

            for (int i = profile.getMatch().getTeamOneScore(); i < 5; i++) {
                teamOneScoreString.append("&7⬤");
            }

            for (int i = profile.getMatch().getTeamTwoScore(); i < 5; i++) {
                teamTwoScoreString.append("&7⬤");
            }

            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "&lTeam One:");
            lines.add(Practice.SECONDARY_COLOR + " » " + teamOneScoreString.toString());
            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "&lTeam Two:");
            lines.add(Practice.SECONDARY_COLOR + " » " + teamTwoScoreString.toString());
        }

        return lines;
    }
}
