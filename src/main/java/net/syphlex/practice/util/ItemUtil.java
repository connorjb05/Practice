package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;
import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.kit.Kit;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@UtilityClass
public class ItemUtil {

    public List<String> serializeItemStack(ItemStack[] itemStacks){
        List<String> itemList = new ArrayList<>();

        int i = 0;
        for (ItemStack itemStack : itemStacks) {

            if (itemStack == null) {
                i++;
                continue;
            }

            StringBuilder serializedItem = new StringBuilder();

            Material material = itemStack.getType();
            int slot = i;
            int amount = itemStack.getAmount();
            short durability = itemStack.getDurability();
            boolean unbreakable = false;

            if (itemStack.hasItemMeta()) {
                unbreakable = itemStack.getItemMeta().spigot().isUnbreakable();
            }

            serializedItem.append(material.name())
                    .append(";").append(slot)
                    .append(";").append(amount)
                    .append(";").append(durability)
                    .append(";").append(unbreakable)
                    .append(";");

            String itemAsString;

            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasEnchants()) {
                StringBuilder enchants = new StringBuilder();
                for (Map.Entry<Enchantment, Integer> entry : itemStack.getItemMeta().getEnchants().entrySet()) {
                    enchants.append(entry.getKey().getName())  // Use the NamespacedKey to avoid issues with different enchantment names
                            .append(":").append(entry.getValue()).append(",");
                }
                // Remove the last comma
                if (enchants.length() > 0) {
                    enchants.setLength(enchants.length() - 1);  // Remove the trailing comma
                }
                serializedItem.append(enchants);
            }

            itemList.add(serializedItem.toString());
            i++;
        }

        return itemList;
    }

    public ItemStack[] deserializeItemStack(List<String> strings){

        ItemStack[] itemStacks = new ItemStack[36];

        for (int i = 0; i < strings.size(); i++) {

            String[] split = strings.get(i).split(";");

            String nameAsString = split[0];
            String slotAsString = split[1];
            String amountAsString = split[2];
            String durabilityAsString = split[3];
            String unbreakableAsString = split[4];

            Map< Enchantment, Integer> enchantmentMap = new HashMap<>();
            // split enchantments format ('PROTECTION_ENVIRONMENTAL:1,DAMAGE_ALL:1')
            if (split.length > 5) {

                // as a string value it is displayed: 'DAMAGE_ALL:1' per enchant here:
                String[] enchantmentsSplit = split[5].split(",");

                // use for loop to dissect each enchantment
                for (String s : enchantmentsSplit) {

                    // split individual enchants into enchant name and level
                    String[] individualEnchantSplit = s.split(":");

                    String enchantName = individualEnchantSplit[0];
                    int level = Integer.parseInt(individualEnchantSplit[1]);

                    enchantmentMap.put(Enchantment.getByName(enchantName), level);
                }
            }

            Material material = Material.getMaterial(nameAsString);

            if (material == null) {
                Practice.get().getLogger().log(Level.SEVERE, "Material not found, parsing as AIR.");
                material = Material.AIR;
            }

            int amount;

            if (!NumberUtils.isNumber(amountAsString)) {
                Practice.get().getLogger().log(Level.SEVERE, "Amount was not a number, parsing as 1.");
                amount = 1;
            } else {
                amount = Integer.parseInt(amountAsString);
            }

            short durability;

            if (!NumberUtils.isNumber(durabilityAsString)) {
                Practice.get().getLogger().log(Level.SEVERE, "Durability was not a number, parsing as 0.");
                durability = 0;
            } else {
                durability = Short.parseShort(durabilityAsString);
            }

            boolean unbreakable = false;

            if (unbreakableAsString.equalsIgnoreCase("true")) {
                unbreakable = true;
            }

            ItemStack itemStack = new ItemBuilder()
                    .setMaterial(material)
                    .setAmount(amount)
                    .setDurability(durability)
                    .setUnbreakable(unbreakable)
                    .build();

            if (!enchantmentMap.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : enchantmentMap.entrySet()) {
                    itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                }
            }

            itemStacks[Integer.parseInt(slotAsString)] = itemStack;
        }
        return itemStacks;
    }

    public ItemStack getLeaveEventItem(){
        return new ItemBuilder()
                .setMaterial(Material.INK_SACK)
                .setDurability((short)1)
                .setName("&cLeave Event &7(Right Click)")
                .build();
    }

    public ItemStack getPlayAgainItem(){
        return new ItemBuilder()
                .setMaterial(Material.PAPER)
                .setName(Practice.PRIMARY_COLOR + "Play Again &7(Right Click)")
                .build();
    }

    public ItemStack getQueueMatchItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Queue Match &7(Right Click)")
                .setMaterial(Material.IRON_SWORD)
                .setUnbreakable(true)
                .build();
    }

    public ItemStack getBotMatchItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Duel a Bot &7(Right Click)")
                .setMaterial(Material.GOLD_SWORD)
                .setUnbreakable(true)
                .build();
    }

    public ItemStack getEventHostItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Event Host &7(Right Click)")
                .setMaterial(Material.EYE_OF_ENDER)
                .build();
    }

    public ItemStack getCreatePartyItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Create Party &7(Right Click)")
                .setMaterial(Material.NAME_TAG)
                .build();
    }

    public ItemStack getLayoutEditorItem(){
        return new ItemBuilder()
                .setName(Practice.PRIMARY_COLOR + "Layout Editor &7(Right Click)")
                .setMaterial(Material.BOOK)
                .build();
    }

    public ItemStack getLeaderboardsItem(){
        return new ItemBuilder()
                .setMaterial(Material.ITEM_FRAME)
                .setName(Practice.PRIMARY_COLOR + "Leaderboards &7(Right Click)")
                .build();
    }

    public ItemStack getSettingsItem(){
        return new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setName(Practice.PRIMARY_COLOR + "Profile Settings &7(Right Click)")
                .build();
    }

    public ItemStack getPartyMatchItem(){
        return new ItemBuilder()
                .setMaterial(Material.IRON_AXE)
                .setName(Practice.PRIMARY_COLOR + "Start Party Match &7(Right Click)")
                .build();
    }

    public ItemStack getPartyFightOtherPartyItem(){
        return new ItemBuilder()
                .setMaterial(Material.DIAMOND_AXE)
                .setName(Practice.PRIMARY_COLOR + "Fight Other Party &7(Right Click)")
                .build();
    }

    public ItemStack getPartyMemberListItem(){
        return new ItemBuilder()
                .setMaterial(Material.PAPER)
                .setName(Practice.PRIMARY_COLOR + "Party Members &7(Right Click)")
                .build();
    }

    public ItemStack getPartySettingsItem(){
        return new ItemBuilder()
                .setMaterial(Material.DIODE)
                .setName(Practice.PRIMARY_COLOR + "Party Settings &7(Right Click)")
                .build();
    }

    public ItemStack getLeavePartyItem(){
        return new ItemBuilder()
                .setName("&cLeave Party &7(Right Click)")
                .setMaterial(Material.NETHER_STAR)
                .build();
    }

    public ItemStack getLeaveQueueItem(){
        return new ItemBuilder()
                .setName("&cLeave Queue &7(Right Click)")
                .setMaterial(Material.INK_SACK)
                .setDurability((short)1)
                .build();
    }

    public ItemStack getBookKit(Kit kit){
        return new ItemBuilder()
                .setMaterial(Material.ENCHANTED_BOOK)
                .setName(Practice.PRIMARY_COLOR + kit.getName() + " Kit")
                .build();
    }
}
