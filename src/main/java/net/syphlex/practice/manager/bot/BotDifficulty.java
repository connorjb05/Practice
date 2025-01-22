package net.syphlex.practice.manager.bot;

import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public enum BotDifficulty {
    EASY("&a&lEasy Bot",
            new ItemBuilder()
                    .setMaterial(Material.INK_SACK)
                    .setDurability((short)10)
                    .setName("&a&lEasy")
                    .setLore(Arrays.asList(
                            Practice.SECONDARY_COLOR + "&m------------------",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Reach: "
                                    + Practice.PRIMARY_COLOR + 1.75 + " blocks",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Min Cps: "
                                    + Practice.PRIMARY_COLOR + 7  + " Cps",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Max Cps: "
                                    + Practice.PRIMARY_COLOR + 10 + " Cps",
                            Practice.SECONDARY_COLOR + "&m------------------"))
                    .build(), 1.75,
            7, 10),
    MODERATE("&e&lModerate Bot",
            new ItemBuilder()
                    .setMaterial(Material.INK_SACK)
                    .setDurability((short)11)
                    .setName("&e&lModerate")
                    .setLore(Arrays.asList(
                            Practice.SECONDARY_COLOR + "&m------------------",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Reach: "
                                    + Practice.PRIMARY_COLOR + 2.5 + " blocks",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Min Cps: "
                                    + Practice.PRIMARY_COLOR + 9  + " Cps",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Max Cps: "
                                    + Practice.PRIMARY_COLOR + 12 + " Cps",
                            Practice.SECONDARY_COLOR + "&m------------------"))
                    .build(), 2.5,
            9, 12),
    HARD("&6&lHard Bot",
            new ItemBuilder()
                    .setMaterial(Material.INK_SACK)
                    .setDurability((short)14)
                    .setName("&6&lHard")
                    .setLore(Arrays.asList(
                            Practice.SECONDARY_COLOR + "&m------------------",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Reach: "
                                    + Practice.PRIMARY_COLOR + 3.0 + " blocks",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Min Cps: "
                                    + Practice.PRIMARY_COLOR + 11  + " Cps",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Max Cps: "
                                    + Practice.PRIMARY_COLOR + 16 + " Cps",
                            Practice.SECONDARY_COLOR + "&m------------------"))
                    .build(), 3.0,
            11, 16),
    HACKER("&c&lHacker Bot",
            new ItemBuilder()
                    .setMaterial(Material.INK_SACK)
                    .setDurability((short)1)
                    .setName("&c&lHacker")
                    .setLore(Arrays.asList(
                            Practice.SECONDARY_COLOR + "&m------------------",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Reach: "
                                    + Practice.PRIMARY_COLOR + 4.0 + " blocks",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Min Cps: "
                                    + Practice.PRIMARY_COLOR + 16  + " Cps",
                            Practice.PRIMARY_COLOR + " » "
                                    + Practice.SECONDARY_COLOR + "Max Cps: "
                                    + Practice.PRIMARY_COLOR + 20 + " Cps",
                            Practice.SECONDARY_COLOR + "&m------------------"))
                    .build(), 4.0,
            16, 20);

    private final String botName;
    private final ItemStack menuIcon;
    private final double reach;
    private final int minCps, maxCps;

    BotDifficulty(String botName,
                  ItemStack menuIcon,
                  double reach,
                  int minCps,
                  int maxCps){
        this.botName = botName;
        this.menuIcon = menuIcon;
        this.reach = reach;
        this.minCps = minCps;
        this.maxCps = maxCps;
    }
}
