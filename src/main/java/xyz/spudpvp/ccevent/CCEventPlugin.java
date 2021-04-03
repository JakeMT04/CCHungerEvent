package xyz.spudpvp.ccevent;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jakemt04.gperms.bukkit.abovehead.AboveHeadNamesManager;
import xyz.spudpvp.ccevent.command.BasesCommand;
import xyz.spudpvp.ccevent.command.EventCommand;
import xyz.spudpvp.ccevent.command.TeamChatCommand;
import xyz.spudpvp.ccevent.command.TeamChatSpyCommand;

public class CCEventPlugin extends JavaPlugin {
    private PAPIImpl hook;


    public static JavaPlugin get() {
        return getPlugin(CCEventPlugin.class);
    }

    @Override
    public void onEnable() {
        AboveHeadNamesManager.manager = new AboveHeadNamesImpl();
        CCTeam.File.init(this);
        CCTeam.loadAll();
        BaseClaims.File.init(this);
        BaseClaims.load();
        getServer().getPluginManager().registerEvents(new BaseClaims(), this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new SafeLootListener(), this);
        new EventCommand().register(this);
        new TeamChatCommand().register(this);
        new TeamChatSpyCommand().register(this);
        new BasesCommand().register(this);
        hook = new PAPIImpl();
        PlaceholderAPI.registerExpansion(hook);
        for (Player player: Bukkit.getOnlinePlayers()) {
            AboveHeadNamesManager.manager.update(player);
            AboveHeadNamesManager.manager.updateAllFor(player);
        }
        for (World world : Bukkit.getWorlds()) {
            for (ArmorStand entity : world.getEntitiesByClass(ArmorStand.class)) {
                if (!entity.isVisible()) {
                    entity.remove();
                }
            }
        }

    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            for (ArmorStand entity : world.getEntitiesByClass(ArmorStand.class)) {
                if (!entity.isVisible()) {
                    entity.remove();
                }
            }
        }
        PlaceholderAPI.unregisterExpansion(hook);
    }
}
