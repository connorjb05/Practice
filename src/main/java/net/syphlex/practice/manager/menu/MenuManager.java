package net.syphlex.practice.manager.menu;

import lombok.Getter;
import net.syphlex.practice.manager.menu.impl.event.EventMenu;
import net.syphlex.practice.manager.menu.impl.QueueMatchMenu;

/***
 * This class is for fixed Menus (menus that are not personalized and are server-sided)
 */

@Getter
public class MenuManager {

    private QueueMatchMenu queueMatchMenu;
    private EventMenu eventMenu;

    public void onEnable(){
        queueMatchMenu = new QueueMatchMenu();
        eventMenu = new EventMenu();
    }
}
