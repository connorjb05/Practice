package net.syphlex.practice.manager.menu.impl.arena;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

public class ManageArenaMenu extends Menu {

    private final Arena arena;

    private final WeakHashMap<Integer, Integer> buttonVerification = new WeakHashMap<>();

    public ManageArenaMenu(Arena arena) {
        super("Manage: " + arena.getName(), 27);

        this.arena = arena;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        setupButtons();
    }

    private void setupButtons(){

        /*
        inventory.setItem(10, new ItemBuilder()
                .setMaterial(Material.WOOD_AXE)
                .setName(Practice.PRIMARY_COLOR + "&lSet Position 1")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to set position 1",
                        "&ffor this arena.",
                        " "))
                .build());

        inventory.setItem(11, new ItemBuilder()
                .setMaterial(Material.STONE_AXE)
                .setName(Practice.PRIMARY_COLOR + "&lSet Position 2")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to set position 2",
                        "&ffor this arena.",
                        " "))
                .build());
         */

        inventory.setItem(10, new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
                .setName(Practice.PRIMARY_COLOR + "&lTeleport to Position 1")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to teleport to",
                        "&fthis arena's position 1.",
                        " "))
                .build());

        inventory.setItem(11, new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
                .setName(Practice.PRIMARY_COLOR + "&lTeleport to Position 2")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to teleport to",
                        "&fthis arena's position 2.",
                        " "))
                .build());

        /*
        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.BED)
                .setName(Practice.PRIMARY_COLOR + "&lSet Spectate Position")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to set the spectate",
                        "&fposition for this arena.",
                        " "))
                .build());
         */

        inventory.setItem(12, new ItemBuilder()
                .setMaterial(Material.NETHER_STAR)
                .setName(Practice.PRIMARY_COLOR + "&lTeleport to Spectate Position")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to teleport to",
                        "&fthis arena's spectate position.",
                        " "))
                .build());

        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.NAME_TAG)
                .setName(Practice.PRIMARY_COLOR + "&lRename Arena")
                .setLore(Arrays.asList(
                        " ",
                        "&fClick to rename arena.",
                        " "))
                .build());

        List<String> lore = new ArrayList<>();

        if (arena.getLadders().isEmpty()) {
            lore.add("&cNo ladders found...");
        } else {
            lore.add(" ");
            for (Ladder ladder : arena.getLadders()) {
                lore.add(Practice.QUATERNARY_COLOR + " » "
                        + Practice.SECONDARY_COLOR + ladder.getName());
            }
            lore.add(" ");
        }

        inventory.setItem(15, new ItemBuilder()
                .setMaterial(Material.LADDER)
                .setName(Practice.PRIMARY_COLOR + "&lLadders:")
                .setLore(lore)
                .build());

        inventory.setItem(16, new ItemBuilder()
                .setMaterial(Material.INK_SACK)
                .setDurability((short)1)
                .setName("&cDelete Arena")
                .setLore(Arrays.asList(
                        " ",
                        "&cClick to delete this arena.",
                        " ",
                        "&4&lWARNING &cThis does not",
                        "&cremove any blocks.",
                        " "))
                .build());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        ItemStack clickedItem = inventory.getItem(e.getSlot());

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null || lore.isEmpty()) {
            return;
        }

        String lastLine = lore.get(lore.size() - 1);

        switch (e.getSlot()) {
            /*
            case 10:

                // set position 1
                handleVerification(e, "Set Position 1", lastLine, () -> {
                    profile.sendMessage("&aYou have successfully set position 1 for "
                            + arena.getName() + ".");
                    Practice.get().getArenaManager().getArenaMap().get(arena.getName())
                            .setPosition1(profile.getPlayer().getLocation());
                    profile.closeMenu();
                });

                break;
            case 11:

                // set position 2
                handleVerification(e, "Set Position 2", lastLine, () -> {
                    profile.sendMessage("&aYou have successfully set position 2 for "
                            + arena.getName() + ".");
                    Practice.get().getArenaManager().getArenaMap().get(arena.getName())
                            .setPosition2(profile.getPlayer().getLocation());
                    profile.closeMenu();
                });

                break;

             */
            case 10:

                // teleport position 1
                handleVerification(e, "Teleport to Position 1", lastLine, () -> {

                    if (arena.getPosition1() == null) {
                        profile.sendMessage("&cPosition was not found.");
                        return;
                    }

                    profile.teleport(arena.getPosition1());
                    profile.closeMenu();
                });

                break;
            case 11:

                // teleport position 2
                handleVerification(e, "Teleport to Position 2", lastLine, () -> {

                    if (arena.getPosition2() == null) {
                        profile.sendMessage("&cPosition was not found.");
                        return;
                    }

                    profile.teleport(arena.getPosition2());
                    profile.closeMenu();
                });

                break;
                /*
            case 14:

                // set spectate position
                handleVerification(e, "Set Spectate Position", lastLine, () -> {
                    profile.sendMessage("&aYou have successfully set the spectate location for "
                            + arena.getName() + ".");
                    Practice.get().getArenaManager().getArenaMap().get(arena.getName())
                            .setSpectate(profile.getPlayer().getLocation());
                    profile.closeMenu();
                });

                break;

                 */
            case 12:

                // teleport spectate position
                handleVerification(e, "Teleport to Spectate Position", lastLine, () -> {

                    if (arena.getSpectate() == null) {
                        profile.sendMessage("&cPosition was not found.");
                        return;
                    }

                    profile.sendMessage("&aTeleporting to " + arena.getName() + "...");
                    profile.teleport(arena.getSpectate());
                    profile.closeMenu();
                });

                break;
            case 14:
                handleVerification(e, "Rename Arena", lastLine, () -> {
                    profile.sendMessage("&aType in chat what you want the arena's new name to be:");
                    profile.sendMessage("&7Type 'cancel' to cancel.");
                    profile.setRenamingArena(arena);
                    profile.closeMenu();
                });
                break;
            case 15:

                // view ladders (Opens ladders to add/remove menu)

                break;
            case 16:

                // delete arena
                handleVerification(e, "Delete Arena", lastLine, () -> {
                    profile.sendMessage("&cYou have deleted " + arena.getName() + ".");
                    Practice.get().getArenaManager().getArenaMap().remove(arena.getName());
                    profile.openMenu(new MainArenaManageMenu(1));
                });

                break;
        }
    }

    private void handleVerification(MenuClickEvent e, String action, String lastLine, Runnable confirmedAction) {

        setupButtons();

        Profile profile = e.getProfile();
        ItemStack clickedItem = inventory.getItem(e.getSlot());
        ItemMeta meta = clickedItem.getItemMeta();

        if (lastLine.contains("again")) {
            confirmedAction.run();
        } else {
            // Set the confirmation lore

            List<String> lore = meta.getLore();

            lore.add("&cClick again to confirm.");

            meta.setLore(StringUtil.CC(lore));
            profile.sendMessage(Practice.QUATERNARY_COLOR + "Are you sure you want to "
                    + Practice.PRIMARY_COLOR + action.toLowerCase()
                    + Practice.QUATERNARY_COLOR + "?");
        }

        clickedItem.setItemMeta(meta);

        inventory.setItem(e.getSlot(), clickedItem);
    }
}
