package net.syphlex.practice.manager.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.ladder.impl.BedFightLadder;
import net.syphlex.practice.manager.ladder.impl.BoxingLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.match.Match;
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

                if (!(profile.getMatch().getLadder() instanceof BridgeLadder)) {
                    lines.add(" ");
                    lines.add(Practice.PRIMARY_COLOR + "» "
                            + Practice.SECONDARY_COLOR
                            + "Your Team: "
                            + Practice.PRIMARY_COLOR + profile.getMatch().getTeam(profile).getAliveCount()
                            + "/" + profile.getMatch().getTeam(profile).getCount());
                    lines.add(Practice.PRIMARY_COLOR + "» "
                            + Practice.SECONDARY_COLOR
                            + "Other Team: "
                            + Practice.PRIMARY_COLOR + profile.getMatch().getOpponents(profile).getAliveCount()
                            + "/" + profile.getMatch().getOpponents(profile).getCount());
                }

                if (profile.getMatch().getLadder() instanceof BoxingLadder) {

                    int teamHits = profile.getMatch().getTeam(profile).getTeamHits();
                    int opponentsHits = profile.getMatch().getOpponents(profile).getTeamHits();

                    int difference = teamHits - opponentsHits;

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
                            + Practice.PRIMARY_COLOR + teamHits);
                    lines.add(Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + "Other Team: "
                            + Practice.PRIMARY_COLOR + opponentsHits);

                    lines.add(" ");
                    lines.add(Practice.PRIMARY_COLOR + "» "
                            + Practice.SECONDARY_COLOR + "Combo: "
                            + Practice.PRIMARY_COLOR + profile.getCombo());
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
            } else if (profile.getMatch().getLadder() instanceof BedFightLadder) {
                getBedFightBoard(profile, lines);
            } else {
                lines.add(Practice.PRIMARY_COLOR + "» "
                        + Practice.SECONDARY_COLOR + "Their Ping: "
                        + Practice.PRIMARY_COLOR + profile.getMatch().getOpponents(profile).getAsList().get(0).getPing() + "ms");
            }

            if (profile.getMatch().getLadder() instanceof BoxingLadder) {

                if (profile.getMatch().getOpponents(profile).getAsList().get(0) != null) {

                    int profileHits = profile.getHits();
                    int opponentHits = profile.getMatch().getOpponents(profile).getAsList().get(0).getHits();

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
            }

            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "syphlex.net");
            lines.add("&f&m-------------------"); // Bottom line with unique padding
        }

        return StringUtil.CC(lines);
    }

    private List<String> getBridgeKitBoard(Profile profile, List<String> lines){

        if (profile.getMatch().getLadder() instanceof BridgeLadder) {

            final Match match = profile.getMatch();

            StringBuilder teamOneScoreString = new StringBuilder();
            StringBuilder teamTwoScoreString = new StringBuilder();

            for (int i = 0; i < match.getTeamOne().getScore(); i++) {
                teamOneScoreString.append(match.getTeamOne().getTeamColor()).append("⬤");
            }

            for (int i = 0; i < match.getTeamTwo().getScore(); i++) {
                teamTwoScoreString.append(match.getTeamTwo().getTeamColor()).append("⬤");
            }

            for (int i = match.getTeamOne().getScore(); i < 5; i++) {
                teamOneScoreString.append("&7⬤");
            }

            for (int i = match.getTeamTwo().getScore(); i < 5; i++) {
                teamTwoScoreString.append("&7⬤");
            }

            lines.add(" ");
            lines.add(Practice.PRIMARY_COLOR + "» " + match.getTeamOne().getTeamColor() + "[R] &7: " + teamOneScoreString);
            lines.add(Practice.PRIMARY_COLOR + "» " + match.getTeamTwo().getTeamColor() + "[B] &7: " + teamTwoScoreString);
        }

        return lines;
    }

    public List<String> getBedFightBoard(Profile profile, List<String> lines){

        final Match match = profile.getMatch();

        final boolean redBed = match.getTeamOne().isHasBed();
        final boolean blueBed = match.getTeamTwo().isHasBed();

        lines.add(" ");
        lines.add(Practice.PRIMARY_COLOR + "» " + match.getTeamOne().getTeamColor() + "[R] &7: " + (redBed ? "&a✔" : "&c✘"));
        lines.add(Practice.PRIMARY_COLOR + "» " + match.getTeamTwo().getTeamColor() + "[B] &7: " + (blueBed ? "&a✔" : "&c✘"));

        return lines;
    }
}
