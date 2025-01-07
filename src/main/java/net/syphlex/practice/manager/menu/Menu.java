package net.syphlex.practice.manager.menu;

import lombok.Getter;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.StringUtil;
import net.syphlex.practice.event.MenuClickEvent;
import net.syphlex.practice.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {
    @Getter
    public final Inventory inventory;

    public final String title;
    public final int size;
    
    public Menu(String title, int size){

        this.title = title;
        this.size = size;

        inventory = Bukkit.createInventory(this, size, StringUtil.CC(title));
    }

    public abstract void onClickEvent(MenuClickEvent e);

}
