package net.syphlex.practice.manager.profile.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.MathUtil;
import net.syphlex.practice.util.StringUtil;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public class InventorySnapshot {
    private final String playerName;
    private final UUID uuid;
    private final ItemStack[] armorContent;
    private final ItemStack[] mainContent;
    private final Collection<PotionEffect> potionEffects;
    private final double health;
    private final int hunger, remainingPotions, missedPotions, landedPotions, hits, longestCombo, swings;

    private Inventory inventory;

    public InventorySnapshot(Profile profile){
        this.playerName = profile.getPlayer().getName();
        this.uuid = profile.getPlayer().getUniqueId();
        this.armorContent = profile.getPlayer().getInventory().getArmorContents();
        this.mainContent = profile.getPlayer().getInventory().getContents();
        this.potionEffects = profile.getPlayer().getActivePotionEffects();
        this.health = profile.getPlayer().getHealth();
        this.hunger = profile.getPlayer().getFoodLevel();

        int remainingPots = 0;
        for (ItemStack item : profile.getPlayer().getInventory().getContents()) {
            if (item != null) {
                if (item.getType() == Material.POTION && item.getDurability() == (short)16421){
                    remainingPots++;
                }
            }
        }
        this.remainingPotions = remainingPots;

        this.missedPotions = profile.getMissedPotions();
        this.landedPotions = profile.getUnmissedPotions();
        this.hits = profile.getHits();
        this.swings = profile.getSwings();
        this.longestCombo = profile.getLongestCombo();
        initInventory();

        profile.setHits(0);
        profile.setLongestCombo(0);
        profile.setMissedPotions(0);
        profile.setUnmissedPotions(0);
        profile.setSwings(0);
    }

    private void initInventory() {
        inventory = Bukkit.createInventory(null, 54, this.playerName + "'s inventory");
        String enabled = ChatColor.GRAY + " " + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
        double roundedHealth = (double)Math.round(this.health / 2.0 * 2.0) / 2.0;

        ItemStack healthItem = new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setAmount((int)Math.round(health))
                .setName(Practice.QUATERNARY_COLOR + "&lHealth: " + Practice.PRIMARY_COLOR + "&l" + roundedHealth + "❤")
                .build();

        inventory.setItem(52, healthItem);

        ItemStack hungerItem = new ItemBuilder()
                .setMaterial(Material.COOKED_BEEF)
                .setAmount(hunger)
                .setName(Practice.QUATERNARY_COLOR + "&lHunger: " + Practice.PRIMARY_COLOR + "&l" + hunger)
                .build();

        inventory.setItem(53, hungerItem);

        ArrayList<String> loreLines = new ArrayList<String>();
        for (PotionEffect effect : this.potionEffects) {
            int duration = effect.getDuration();
            String durationMinuteSecond = MathUtil.convertTicksToMinutes(duration);
            String effectAmplifierRoman = StringUtil.romanNumerals(effect.getAmplifier() + 1);
            String effectName = effect.getType().getName().toLowerCase();
            effectName = effectName.replace('_', ' ');
            effectName = WordUtils.capitalizeFully(effectName);
            loreLines.add(Practice.QUATERNARY_COLOR + " " + effectName + " "
                    + effectAmplifierRoman + Practice.PRIMARY_COLOR + " (" + durationMinuteSecond + ")");
        }

        ItemStack potionEffects = new ItemBuilder()
                .setMaterial(Material.BREWING_STAND_ITEM)
                .setName(Practice.QUATERNARY_COLOR + "&lPotion Effects")
                .setLore(loreLines.isEmpty() ? Collections.singletonList("&cNone") : loreLines)
                .build();

        inventory.setItem(49, potionEffects);

        ItemStack statsItem = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setName(Practice.QUATERNARY_COLOR + "&lHits: " + Practice.PRIMARY_COLOR + "&l" + hits)
                .setLore(Arrays.asList(
                        Practice.QUATERNARY_COLOR + " Longest Combo: " + Practice.PRIMARY_COLOR + longestCombo,
                        Practice.QUATERNARY_COLOR + " Hit/Miss Ratio: " + Practice.PRIMARY_COLOR
                                + String.format("%.1f", (float)hits / (float)Math.max(swings, 1) * 100.f) + "%"))
                .build();

        inventory.setItem(46, statsItem);

        int j = 0;
        while (j < 36) {
            if (this.mainContent[j] != null) {
                this.inventory.setItem(j, this.mainContent[j]);
            }
            ++j;
        }
        j = 0;
        while (j < 9) {
            this.inventory.setItem(j + 27, this.inventory.getItem(j));
            ++j;
        }
        j = 0;
        while (j < 18) {
            this.inventory.setItem(j, this.inventory.getItem(j + 9));
            ++j;
        }
        j = 36;
        while (j <= 39) {
            if (this.armorContent[39 - j] != null) {
                this.inventory.setItem(j, this.armorContent[39 - j]);
            }
            ++j;
        }

        ItemStack potionsLeftItem = new ItemBuilder()
                .setMaterial(Material.POTION)
                .setDurability((short)16421)
                .setName(Practice.QUATERNARY_COLOR + "&lHealth Potions: "
                        + Practice.PRIMARY_COLOR + "&l" + remainingPotions)
                .setAmount(Math.max(remainingPotions, 1))
                .setLore(Arrays.asList(
                        Practice.QUATERNARY_COLOR + " Landed: " + Practice.PRIMARY_COLOR + landedPotions,
                        Practice.QUATERNARY_COLOR + " Missed: " + Practice.PRIMARY_COLOR + missedPotions,
                        Practice.QUATERNARY_COLOR + " Accuracy: " + Practice.PRIMARY_COLOR
                                + String.format("%.1f", (float)landedPotions / (float)(Math.max(missedPotions, 1)) * 100.f) + "%"))
                .build();

        inventory.setItem(45, potionsLeftItem);
    }
}
