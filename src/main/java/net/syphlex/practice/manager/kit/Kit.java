package net.syphlex.practice.manager.kit;

import lombok.Getter;
import net.syphlex.practice.manager.kit.impl.BridgeKit;
import net.syphlex.practice.manager.kit.impl.SumoKit;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.ItemUtil;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemUtil;
import org.bukkit.Material;
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

        profile.getPlayer().getInventory().setItem(0, ItemUtil.getBookKit(this));
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

        ItemStack[] inventory = this.inventory.clone();
        ItemStack[] armor = this.armor.clone();

        if (profile.getKitPresets().get(this) != null) {
            inventory = profile.getKitPreset(this).clone();
        }

        if (profile.getMatch().getKit() instanceof BridgeKit) {

            int claySlot1 = -1, claySlot2 = -1;

            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null && inventory[i].getType() == Material.STAINED_CLAY) {
                    if (claySlot1 == -1) {
                        claySlot1 = i;
                    } else {
                        claySlot2 = i;
                    }
                }
            }

            // red team (team 1)
            if (profile.getMatch().isTeamOne(profile)) {

                inventory[claySlot1] = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setDurability((short)14)
                        .setAmount(64)
                        .build();

                inventory[claySlot2] = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setDurability((short)14)
                        .setAmount(64)
                        .build();

                armor[0] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_BOOTS)
                        .setColor(255, 0, 0)
                        .setUnbreakable(true)
                        .build();

                armor[1] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_LEGGINGS)
                        .setColor(255, 0, 0)
                        .setUnbreakable(true)
                        .build();

                armor[2] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_CHESTPLATE)
                        .setColor(255, 0, 0)
                        .setUnbreakable(true)
                        .build();

            } else {
                // blue team (team 2)

                inventory[claySlot1] = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setDurability((short)11)
                        .setAmount(64)
                        .build();

                inventory[claySlot2] = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setDurability((short)11)
                        .setAmount(64)
                        .build();

                armor[0] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_BOOTS)
                        .setColor(0, 0, 255)
                        .setUnbreakable(true)
                        .build();

                armor[1] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_LEGGINGS)
                        .setColor(0, 0, 255)
                        .setUnbreakable(true)
                        .build();

                armor[2] = new ItemBuilder()
                        .setMaterial(Material.LEATHER_CHESTPLATE)
                        .setColor(0, 0, 255)
                        .setUnbreakable(true)
                        .build();
            }
        }

        profile.getPlayer().getInventory().setContents(inventory);
        profile.getPlayer().getInventory().setArmorContents(armor);

        if (!potionEffects.isEmpty()) {
            profile.getPlayer().addPotionEffects(potionEffects);
        }
    }
}
