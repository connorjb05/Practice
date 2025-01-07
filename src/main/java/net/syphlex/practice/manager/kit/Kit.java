package net.syphlex.practice.manager.kit;

import lombok.Getter;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Kit {

    private final String name;

    public ItemStack menuIcon;

    public ItemStack[] inventory;
    public ItemStack[] armor;

    public List<PotionEffect> potionEffects = new ArrayList<>();

    public Kit(String name){
        this.name = name;
    }

    public void giveBookKits(Profile profile){

        if (profile == null || profile.getPlayer() == null) {
            return;
        }

        if (profile.getMatch() != null
                && profile.getMatch().getKit() instanceof SumoKit) {
            giveKit(profile);
            return;
        }

        // todo loop through all of their presets and give them a book

        profile.getPlayer().getInventory().setItem(0, ItemUtil.getBookKit());
    }

    public void giveKit(Profile profile){

        if (profile == null || profile.getPlayer() == null) {
            return;
        }

        if (inventory == null) {
            throw new NullPointerException("Kit inventory is not initialized!");
        }

        if (armor == null) {
            throw  new NullPointerException("Kit armor is not initialized!");
        }

        // todo get profiles specific assortment of the kit

        profile.getPlayer().getInventory().setContents(inventory);
        profile.getPlayer().getInventory().setArmorContents(armor);

        if (!potionEffects.isEmpty()) {
            profile.getPlayer().addPotionEffects(potionEffects);
        }
    }
}
