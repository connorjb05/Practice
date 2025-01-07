package net.syphlex.practice.util;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class ItemBuilder {
    private ItemStack itemStack = null;
    private String displayName = "";
    private List<String> lore = new ArrayList<>();
    private int amount = 1;
    private Material material = Material.AIR;
    private short durability = 0;
    private boolean glowing = false;
    private boolean unbreakable = false;
    private int[] color = new int[]{-1, -1, -1};

    private Map<Enchantment, Integer> vanillaEnchants = new HashMap<>();

    public ItemBuilder setItemStack(ItemStack itemStack){
        this.itemStack = itemStack;
        return this;
    }

    public ItemBuilder setName(String displayName){
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setLore(List<String> lore){
        this.lore = lore;
        return this;
    }

    public ItemBuilder setAmount(int amount){
        this.amount = amount;
        return this;
    }

    public ItemBuilder setMaterial(Material material){
        this.material = material;
        return this;
    }

    public ItemBuilder setDurability(short durability){
        this.durability = durability;
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing){
        this.glowing = glowing;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable){
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder addVanillaEnchant(Enchantment enchantment, int level){
        this.vanillaEnchants.remove(enchantment);
        this.vanillaEnchants.put(enchantment, level);
        return this;
    }

    public ItemBuilder removeVanillaEnchant(Enchantment enchantment){
        this.vanillaEnchants.remove(enchantment);
        return this;
    }

    public ItemBuilder setVanillaEnchants(Map<Enchantment, Integer> vanillaEnchants){
        this.vanillaEnchants = vanillaEnchants;
        return this;
    }

    public ItemBuilder setColor(int... rgb){
        if (rgb.length == 3) {
            color = new int[]{rgb[0], rgb[1], rgb[2]};
        }
        return this;
    }

    public ItemBuilder clone(){
        return new ItemBuilder()
                .setItemStack(itemStack)
                .setName(displayName)
                .setLore(lore)
                .setAmount(amount)
                .setMaterial(material)
                .setDurability(durability)
                .setGlowing(glowing)
                .setUnbreakable(unbreakable)
                .setColor(color)
                .setVanillaEnchants(vanillaEnchants);
    }

    public ItemStack build(){

        ItemStack itemStack = this.itemStack;

        if (itemStack == null) {
            itemStack = new ItemStack(material, amount, (short) durability);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (color[0] != -1 && color[1] != -1 && color[2] != -1) {
            ((LeatherArmorMeta) itemMeta).setColor(Color.fromRGB(color[0], color[1], color[2]));
        }

        itemMeta.setDisplayName(StringUtil.CC(displayName));

        List<String> lore = new ArrayList<>(this.lore);

        if (!vanillaEnchants.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : vanillaEnchants.entrySet()) {
                itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        itemMeta.setLore(StringUtil.CC(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}

