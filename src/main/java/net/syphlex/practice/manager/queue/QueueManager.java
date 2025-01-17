package net.syphlex.practice.manager.queue;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class QueueManager {

    private final Map<Ladder, Queue<Profile>> queueMap = new HashMap<>();

    @Getter
    private final List<Profile> playersInMatch = new ArrayList<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private long lastMessageUpdate = System.currentTimeMillis();

    public void onEnable(){

        for (Ladder ladder : Practice.get().getLadderManager().getLadderMap().values()) {
            queueMap.put(ladder, new ConcurrentLinkedQueue<>());
        }

        executor.scheduleAtFixedRate(() -> {

            for (Map.Entry<Ladder, Queue<Profile>> entry : queueMap.entrySet()) {
                Ladder ladder = entry.getKey();
                Queue<Profile> queue = entry.getValue();

                if (Math.abs(System.currentTimeMillis() - lastMessageUpdate) > 5000L) {

                    for (Profile queued : queueMap.get(ladder)) {
                        queued.sendMessage(" ");
                        queued.sendMessage(Practice.QUATERNARY_COLOR + "You are currently in the "
                                + Practice.PRIMARY_COLOR + ChatColor.stripColor(ladder.getName())
                                + Practice.QUATERNARY_COLOR + " queue.");
                        queued.sendMessage("&7Searching for a match...");
                        queued.sendMessage(" ");
                    }
                }

                synchronized (queue) {
                    if (queue.size() >= 2) {
                        final Profile p1 = queue.poll();
                        final Profile p2 = queue.poll();

                        if (p1 == null || p2 == null) {
                            Practice.get().getLogger().log(Level.SEVERE,
                                    "Null error while handling queueing. "
                                            + (p1 == null ? "Profile 1 was found null."
                                            : "Profile 2 was found null."));
                            continue;
                        }

                        Arena arena = Practice.get().getArenaManager().getFreeArena(ladder);

                        // no arena was found!
                        if (arena == null) {
                            p1.sendMessage("&cNo arena was found.");
                            p2.sendMessage("&cNo arena was found.");

                            p1.setLadderQueued(null);
                            p2.setLadderQueued(null);

                            InventoryUtil.setSpawnInventory(p1.getPlayer());
                            InventoryUtil.setSpawnInventory(p2.getPlayer());
                            continue;
                        }

                        if (p1.isInMatch() || p2.isInMatch()) {
                            if (p1.isInMatch()) {
                                removeFromQueueMap(p1);
                            } else {
                                removeFromQueueMap(p2);
                            }
                            continue;
                        }

                        p1.setLadderQueued(null);
                        p2.setLadderQueued(null);

                        Practice.get().getMatchManager().getMatchMap()
                                .get(ladder).add(new Match(
                                        Collections.singletonList(p1),
                                        Collections.singletonList(p2),
                                        null, arena, ladder, true, false));

                    }
                }
            }

            if (Math.abs(System.currentTimeMillis() - lastMessageUpdate) > 5000L) {
                lastMessageUpdate = System.currentTimeMillis();
            }

        }, 0, 1500, TimeUnit.MILLISECONDS);
    }

    public void onDisable(){
        executor.shutdownNow();
    }

    public synchronized void queue(Profile profile, Ladder ladder) {

        if (ladder.inventory == null || ladder.armor == null) {
            profile.sendMessage("&cThere was an error while trying to queue for this kit.");
            return;
        }

        if (!queueMap.get(ladder).contains(profile)) {

            profile.sendMessage(" ");
            profile.sendMessage("&aYou have been added to the "
                    + ChatColor.stripColor(ladder.getName()) + " queue.");
            profile.sendMessage("&7Searching for a match...");
            profile.sendMessage(" ");

            if (!profile.isInMatch()) {
                InventoryUtil.setQueuedInventory(profile.getPlayer());
            }

            profile.setLadderQueued(ladder);

            queueMap.get(ladder).add(profile);

        } else {
            profile.sendMessage("&cYou are already in a queue.");
        }
    }

    public synchronized void dequeue(Profile profile) {

        if (profile.getLadderQueued() == null) {
            return;
        }

        if (!queueMap.get(profile.getLadderQueued()).contains(profile)) {
            return;
        }

        profile.sendMessage("&cYou have left the "
                + ChatColor.stripColor(profile.getLadderQueued().getName()) + " queue.");

        queueMap.get(profile.getLadderQueued()).remove(profile);

        profile.setLadderQueued(null);

        if (!profile.isInMatch()) {
            InventoryUtil.setSpawnInventory(profile.getPlayer());
        }
    }

    public void removeFromQueueMap(Profile profile){

        if (profile.getLadderQueued() == null) {
            return;
        }

        queueMap.get(profile.getLadderQueued()).remove(profile);
        profile.setLadderQueued(null);
    }

    public int getInQueue(Ladder ladder){

        if (ladder == null) {
            int total = 0;
            for (Queue<Profile> queue : queueMap.values()) {
                total += queue.size();
            }
            return total;
        }

        return queueMap.get(ladder).size();
    }

    public int getTotalInQueue(){
        int total = 0;
        for (Map.Entry<Ladder, Queue<Profile>> queueEntry : queueMap.entrySet()) {
            total += queueEntry.getValue().size();
        }
        return total;
    }
}

