package net.syphlex.practice.command;

import net.syphlex.practice.Practice;
import net.syphlex.practice.manager.profile.Profile;
import net.syphlex.practice.util.AbstractCmd;

public class SpawnCmd extends AbstractCmd {
    public SpawnCmd(String command) {
        super(command);
    }

    @Override
    public void onCommand(Profile profile, String[] args) {

        if (profile.isInMatch()) {
            profile.getMatch().eliminate(profile);
            profile.reset();
        }

        profile.teleport(Practice.get().getConfigManager().getMainSpawn());
    }
}
