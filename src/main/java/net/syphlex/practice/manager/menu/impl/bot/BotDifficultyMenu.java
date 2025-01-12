package net.syphlex.practice.manager.menu.impl.bot;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.kit.Kit;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BotDifficultyMenu extends Menu {

    private final Kit kit;

    public BotDifficultyMenu(Kit kit) {
        super("Bot Difficulty", 27);

        this.kit = kit;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        inventory.setItem(12, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 4)
                .setName("&e&lEasy")
                .setLore(Arrays.asList(
                        "&f&m-----------------------",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "Reach: "
                                + Practice.PRIMARY_COLOR + "2.0 Blocks",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "CPS: "
                                + Practice.PRIMARY_COLOR + "9 Cps",
                        "&f&m-----------------------"))
                .build());

        inventory.setItem(13, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 1)
                .setName("&6&lModerate")
                .setLore(Arrays.asList(
                        "&f&m-----------------------",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "Reach: "
                                + Practice.PRIMARY_COLOR + "2.5 Blocks",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "CPS: "
                                + Practice.PRIMARY_COLOR + "13 Cps",
                        "&f&m-----------------------"))
                .build());

        inventory.setItem(14, new ItemBuilder()
                .setMaterial(Material.WOOL)
                .setDurability((short) 14)
                .setName("&c&lHard")
                .setLore(Arrays.asList(
                        "&f&m-----------------------",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "Reach: "
                                + Practice.PRIMARY_COLOR + "3.0 Blocks",
                        Practice.PRIMARY_COLOR + " » "
                                + Practice.SECONDARY_COLOR + "CPS: "
                                + Practice.PRIMARY_COLOR + "17 Cps",
                        "&f&m-----------------------"))
                .build());
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        if (e.getSlot() != -1) {
            //profile.sendMessage("&cThis feature is currently under going development...");
            //return;
        }

        switch (e.getSlot()) {
            case 12:

                Arena arena = Practice.get().getArenaManager().getFreeArena(kit);

                // no arena was found!
                if (arena == null) {
                    profile.sendMessage("&cNo arena found.");
                    return;
                }

                //Bot bot = new Bot("&e&lEasy Bot", BotDifficulty.EASY, new NoDebuffBotTrait());


                profile.getPlayer().closeInventory();
                break;
            case 13:

                break;
            case 14:

                break;
        }
    }
}
