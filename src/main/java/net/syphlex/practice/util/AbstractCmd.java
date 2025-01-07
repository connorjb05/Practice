package net.syphlex.practice.util;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCmd implements CommandExecutor {

    public AbstractCmd(String command){
        Practice.get().getCommand(command).setExecutor(this);
    }

    public abstract void onCommand(final Profile profile, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args){

        if (sender instanceof Player) {
            final Player p = (Player) sender;
            final Profile profile = Practice.get().getProfileManager().get(p);
            onCommand(profile, args);
            return true;
        }

        return true;
    }
}
