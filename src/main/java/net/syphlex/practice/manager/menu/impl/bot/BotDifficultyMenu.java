package net.syphlex.practice.manager.menu.impl.bot;

import net.syphlex.practice.Practice;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.arena.Arena;
import net.syphlex.practice.manager.bot.Bot;
import net.syphlex.practice.manager.bot.BotDifficulty;
import net.syphlex.practice.manager.ladder.Ladder;
import net.syphlex.practice.manager.match.Match;
import net.syphlex.practice.manager.menu.Menu;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.Messages;
import net.syphlex.practice.util.Permissions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class BotDifficultyMenu extends Menu {

    private final Ladder ladder;

    public BotDifficultyMenu(Ladder ladder) {
        super("Bot Difficulty", 27);

        this.ladder = ladder;

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

        int slot = 10;
        for (BotDifficulty difficulty : BotDifficulty.values()) {
            inventory.setItem(slot, difficulty.getMenuIcon());
            slot++;
        }
    }

    @Override
    public void onClickEvent(MenuClickEvent e) {

        final Profile profile = e.getProfile();

        if (!profile.hasPermission(Permissions.ADMIN)) {
            profile.sendMessage(Messages.NO_PERMISSION);
            profile.sendMessage("&cThis feature is under development... We apologize for the inconvenience.");
            return;
        }

        int slot = 10;
        for (BotDifficulty difficulty : BotDifficulty.values()) {
            if (e.getSlot() == slot) {

                Arena arena = Practice.get().getArenaManager().getFreeArena(ladder);

                if (arena == null) {
                    profile.sendMessage("&cNo arenas found.");
                    return;
                }

                Bot bot = new Bot(difficulty);

                Practice.get().getMatchManager().getMatchMap().get(ladder)
                        .add(new Match(
                                Collections.singletonList(profile),
                                Collections.singletonList(bot),
                                null, arena, ladder, false, false));
                profile.getPlayer().closeInventory();
                return;
            }

            slot++;
        }
    }
}
