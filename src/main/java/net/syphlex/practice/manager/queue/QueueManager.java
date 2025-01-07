package net.syphlex.practice.manager.queue;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.InventoryUtil;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class QueueManager {

    private final Map<Kit, Queue<Profile>> queueMap = new HashMap<>();

    @Getter
    private final List<Profile> playersInMatch = new ArrayList<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private long lastMessageUpdate = System.currentTimeMillis();

    public void onEnable(){

        for (Kit kit : Practice.get().getKitManager().getKitMap().values()) {
            queueMap.put(kit, new ConcurrentLinkedQueue<>());
        }

        executor.scheduleAtFixedRate(() -> {

            for (Map.Entry<Kit, Queue<Profile>> entry : queueMap.entrySet()) {
                Kit kit = entry.getKey();
                Queue<Profile> queue = entry.getValue();

                if (Math.abs(System.currentTimeMillis() - lastMessageUpdate) > 5000L) {

                    for (Profile queued : queueMap.get(kit)) {
                        queued.sendMessage(" ");
                        queued.sendMessage(Practice.SECONDARY_COLOR + "You are currently in the "
                                + Practice.PRIMARY_COLOR + ChatColor.stripColor(kit.getName())
                                + Practice.SECONDARY_COLOR + " queue.");
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

                        Arena arena = Practice.get().getArenaManager().getFreeArena(kit);

                        // no arena was found!
                        if (arena == null) {
                            p1.sendMessage("&cNo arena was found.");
                            p2.sendMessage("&cNo arena was found.");

                            InventoryUtil.setSpawnInventory(p1.getPlayer());
                            InventoryUtil.setSpawnInventory(p2.getPlayer());

                            continue;
                        }

                        Practice.get().getMatchManager().getMatchMap()
                                .get(kit).add(new Match(
                                        Collections.singletonList(p1),
                                        Collections.singletonList(p2),
                                        null, arena, kit, true, false));
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

    public void queue(Profile profile, Kit kit) {

        if (kit.inventory == null || kit.armor == null) {
            profile.sendMessage("&cThere was an error while trying to queue for this kit.");
            return;
        }
        if (!queueMap.get(kit).contains(profile)) {

            profile.sendMessage(" ");
            profile.sendMessage("&aYou have been added to the "
                    + ChatColor.stripColor(kit.getName()) + " queue.");
            profile.sendMessage("&7Searching for a match...");
            profile.sendMessage(" ");

            InventoryUtil.setQueuedInventory(profile.getPlayer());

            profile.setKitQueued(kit);

            queueMap.get(kit).add(profile);

        } else {
            profile.sendMessage("&cYou are already in a queue.");
        }
    }

    public void dequeue(Profile profile) {

        if (profile.getKitQueued() == null) {
            return;
        }

        if (!queueMap.get(profile.getKitQueued()).contains(profile)) {
            return;
        }

        profile.sendMessage("&cYou have left the "
                + ChatColor.stripColor(profile.getKitQueued().getName()) + " queue.");

        queueMap.get(profile.getKitQueued()).remove(profile);

        profile.setKitQueued(null);

        InventoryUtil.setSpawnInventory(profile.getPlayer());
    }

    public int getInQueue(Kit kit){

        if (kit == null) {
            int total = 0;
            for (Queue<Profile> queue : queueMap.values()) {
                total += queue.size();
            }
            return total;
        }

        return queueMap.get(kit).size();
    }

    public int getTotalInQueue(){
        int total = 0;
        for (Map.Entry<Kit, Queue<Profile>> queueEntry : queueMap.entrySet()) {
            total += queueEntry.getValue().size();
        }
        return total;
    }
}

