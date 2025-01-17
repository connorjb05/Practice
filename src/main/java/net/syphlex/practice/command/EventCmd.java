package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

public class EventCmd extends AbstractCmd {
    public EventCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("host")) {
                profile.openMenu(Practice.get().getMenuManager().getEventMenu());
            } else if (args[0].equalsIgnoreCase("start")) {

                // todo

            } else if (args[0].equalsIgnoreCase("end")) {

                if (Practice.get().getEventManager().getActiveEvent() == null) {
                    profile.sendMessage("&cThere is no active event.");
                    return;
                }

                Practice.get().getEventManager().endEvent();
            }
        }
    }
}
