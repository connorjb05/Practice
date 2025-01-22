package net.syphlex.practice.manager.ladder;

import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.syphlex.practice.manager.bot.Bot;
import net.syphlex.practice.manager.ladder.impl.BedFightLadder;
import net.syphlex.practice.manager.ladder.impl.BridgeLadder;
import net.syphlex.practice.manager.ladder.impl.SumoLadder;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.ItemBuilder;
import net.syphlex.practice.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Ladder {

    private final String name;

    public ItemStack menuIcon;

    public ItemStack[] inventory;
    public ItemStack[] armor;

    public boolean freeze = false;

    public List<PotionEffect> potionEffects = new ArrayList<>();

    public Ladder(String name){
        this.name = name;
    }

    public void giveBookKits(Profile profile){

        if (profile == null || profile.getPlayer() == null) {

            if (profile instanceof Bot) {
                Bot bot = (Bot) profile;

                // Set bot's inventory using NPC metadata
                NPC npc = bot.getNpc();
                if (npc != null && npc.isSpawned()) {
                    LivingEntity botEntity = (LivingEntity) npc.getEntity();

                    // Equip the bot's inventory
                    botEntity.getEquipment().setItemInHand(inventory[0]);


                    // Equip the bot's armor
                    botEntity.getEquipment().setArmorContents(armor);

                    // Apply potion effects
                    if (!potionEffects.isEmpty()) {
                        for (PotionEffect effect : potionEffects) {
                            botEntity.addPotionEffect(effect);
                        }
                    }
                }
                return;
            }

            return;
        }

        if (profile.getMatch() != null
                && profile.getMatch().getLadder() instanceof SumoLadder) {
            giveKit(profile);
            return;
        }

        giveKit(profile);
        //profile.getPlayer().getInventory().setItem(0, ItemUtil.getBookKit(this));
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

        if (profile.getMatch().getLadder() instanceof BridgeLadder) {

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
            if (profile.getMatch().getTeamOne().isInTeam(profile)) {

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

        if (profile.getMatch().getLadder() instanceof BedFightLadder) {

            int woolSlot = -1;

            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null && inventory[i].getType() == Material.WOOL) {
                    woolSlot = i;
                }
            }

            // red team (team 1)
            if (profile.getMatch().getTeamOne().isInTeam(profile)) {

                inventory[woolSlot] = new ItemBuilder()
                        .setMaterial(Material.WOOL)
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

                inventory[woolSlot] = new ItemBuilder()
                        .setMaterial(Material.WOOL)
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

        profile.getPlayer().updateInventory();
    }
}
