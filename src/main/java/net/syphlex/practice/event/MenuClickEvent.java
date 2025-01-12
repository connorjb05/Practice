package net.syphlex.practice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.syphlex.practice.manager.profile.Profile;
import org.bukkit.event.inventory.ClickType;

@Getter
@AllArgsConstructor
public class MenuClickEvent {
    private final Profile profile;
    private final int slot;
    private final ClickType clickType;

    @Setter
    private boolean cancelled = false;
}
