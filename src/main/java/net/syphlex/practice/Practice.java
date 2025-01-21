package net.syphlex.practice;

import lombok.Getter;
import net.syphlex.practice.command.*;
import net.syphlex.practice.listener.*;
import net.syphlex.practice.manager.arena.ArenaManager;
import net.syphlex.practice.manager.arena.chunk.ChunkManager;
import net.syphlex.practice.manager.config.ConfigManager;
import net.syphlex.practice.manager.event.EventManager;
import net.syphlex.practice.manager.ladder.LadderManager;
import net.syphlex.practice.manager.leaderboards.LeaderboardManager;
import net.syphlex.practice.manager.match.MatchManager;
import net.syphlex.practice.manager.menu.MenuManager;
import net.syphlex.practice.manager.party.PartyManager;
import net.syphlex.practice.manager.profile.ProfileManager;
import net.syphlex.practice.manager.queue.QueueManager;
import net.syphlex.practice.manager.scoreboard.ScoreboardManager;
import net.syphlex.practice.command.ArenaCmd;
import net.syphlex.practice.command.PartyCmd;
import net.syphlex.practice.listener.PlayerListener;
import net.syphlex.practice.manager.system.ThreadManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Practice extends JavaPlugin {

    private static Practice instance;

    public static final String PRIMARY_COLOR = "&b";
    public static final String SECONDARY_COLOR = "&f";
    public static final String TERTIARY_COLOR = "&3";
    public static final String QUATERNARY_COLOR = "&e";

    private final ThreadManager threadManager = new ThreadManager();
    private final ConfigManager configManager = new ConfigManager();
    private final ScoreboardManager scoreboardManager = new ScoreboardManager();
    private final LadderManager ladderManager = new LadderManager();
    private final ProfileManager profileManager = new ProfileManager();
    private final ArenaManager arenaManager = new ArenaManager();
    private final ChunkManager chunkManager = new ChunkManager();
    private final MatchManager matchManager = new MatchManager();
    private final QueueManager queueManager = new QueueManager();
    private final EventManager eventManager = new EventManager();
    private final MenuManager menuManager = new MenuManager();
    private final PartyManager partyManager = new PartyManager();
    private final LeaderboardManager leaderboardManager = new LeaderboardManager();

    @Override
    public void onEnable(){

        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        configManager.onEnable();
        scoreboardManager.onEnable();
        ladderManager.onEnable();
        profileManager.onEnable();
        arenaManager.onEnable();
        //chunkManager.onEnable();
        matchManager.onEnable();
        queueManager.onEnable();
        eventManager.onEnable();
        leaderboardManager.onEnable();
        menuManager.onEnable();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new MatchListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PotionListener(), this);

        new PracticeCmd("practice");
        new ArenaCmd("arena");
        new PartyCmd("party");
        new SpectateCmd("spectate");
        new SpawnCmd("spawn");
        new BuildCmd("build");
        new DuelCmd("duel");
        new EventCmd("event");
        new InventoryCmd("inventory");
    }

    @Override
    public void onDisable(){

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        matchManager.onDisable();
        leaderboardManager.onDisable();
        eventManager.onDisable();
        queueManager.onDisable();
        chunkManager.onDisable();
        arenaManager.onDisable();
        profileManager.onDisable();
        configManager.onDisable();
        threadManager.onDisable();
    }

    public static Practice get(){
        return instance;
    }
}
