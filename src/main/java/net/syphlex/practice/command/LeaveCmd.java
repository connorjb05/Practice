package net.syphlex.practice.command;

import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

public class LeaveCmd extends AbstractCmd {
    public LeaveCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {
        if (!profile.isInMatch()) {

            return;
        }
    }
}
