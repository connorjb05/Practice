package net.syphlex.practice.command;

import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

public class TestCmd extends AbstractCmd {
    public TestCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {
    }
}
