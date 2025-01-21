package net.syphlex.practice.manager.menu.impl.arena;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainArenaManageMenu extends Menu {

    private final List<Arena> arenaList;
    private final int page, endIndex, startIndex;

    public MainArenaManageMenu(int page) {
        super("Manage Arenas", 54);

        // Fill the top and bottom rows with glass panes
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)); // Black Stained Glass Pane
        }

        // Fill the left and right columns with glass panes
        for (int i = 0; i < 45; i += 9) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
            inventory.setItem(i + 8, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        this.arenaList = new ArrayList<>(Practice.get().getArenaManager().getArenaMap().values());

        this.startIndex = (page - 1) * 28;
        this.endIndex = Math.min(startIndex + 28, arenaList.size());
        this.page = page;

        int slotIndex = 10; // Starting from the first valid inner slot
        for (int i = startIndex; i < endIndex; i++) {
            while (isOutlineSlot(slotIndex)) {
                slotIndex++; // Skip slots reserved for the outline
            }

            Arena arena = arenaList.get(i);

            ItemStack itemStack = new ItemBuilder()
                    .setMaterial(Material.MAP)
                    .setName(Practice.QUATERNARY_COLOR + "Manage: "
                            + Practice.PRIMARY_COLOR + arena.getName())
                    .build();

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());

            List<String> lore = new ArrayList<>();

            lore.addAll(Arrays.asList(
                    "&f&m----------------------",
                    Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + "Size: "
                            + Practice.PRIMARY_COLOR + arena.getSize(),
                    Practice.PRIMARY_COLOR + " » "
                            + Practice.SECONDARY_COLOR + "Status: "
                            + (arena.isOpen() ? "&aOpen" : "&cIn Use / Closed")));

            if (!arena.getLadders().isEmpty()) {
                lore.add(" ");
                lore.add(Practice.PRIMARY_COLOR + "&lLadders:");
                for (Ladder ladder : arena.getLadders()) {
                    lore.add(Practice.TERTIARY_COLOR + "  » "
                            + Practice.SECONDARY_COLOR + ladder.getName());
                }
            }

            lore.add("&f&m----------------------");

            itemMeta.setLore(StringUtil.CC(lore));

            itemStack.setItemMeta(itemMeta);

            inventory.setItem(slotIndex, itemStack);
            slotIndex++;
        }

        if (page > 1) {
            inventory.setItem(45, new ItemBuilder()
                    .setMaterial(Material.PAPER)
                    .setName(Practice.PRIMARY_COLOR + "Previous Page")
                    .build());
        }

        if (endIndex < arenaList.size()) {
            inventory.setItem(53, new ItemBuilder()
                    .setMaterial(Material.PAPER)
                    .setName(Practice.PRIMARY_COLOR + "Next Page")
                    .build());
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {
        switch (e.getSlot()) {
            case 45:
                if (page > 1) {
                    e.getProfile().openMenu(new MainArenaManageMenu(page - 1));
                }
                break;
            case 53:
                if (endIndex < arenaList.size()) {
                    e.getProfile().openMenu(new MainArenaManageMenu(page + 1));
                }
                break;
        }

        int slotIndex = 10; // Starting from the first valid inner slot
        for (int i = startIndex; i < endIndex; i++) {
            while (isOutlineSlot(slotIndex)) {
                slotIndex++; // Skip slots reserved for the outline
            }

            if (e.getSlot() == slotIndex) {
                Arena arena = arenaList.get(i);
                e.getProfile().openMenu(new ManageArenaMenu(arena));
                return;
            }
            slotIndex++;
        }
    }

    /**
     * Determines if a given slot index is part of the black-stained glass outline.
     *
     * @param slot The slot index to check.
     * @return True if the slot is part of the outline; otherwise, false.
     */
    private boolean isOutlineSlot(int slot) {
        // Top and bottom rows
        if (slot < 9 || slot >= 45) return true;

        // Left and right columns
        return slot % 9 == 0 || slot % 9 == 8;
    }
}