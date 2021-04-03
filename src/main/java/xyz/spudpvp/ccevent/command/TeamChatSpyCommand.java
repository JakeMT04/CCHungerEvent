package xyz.spudpvp.ccevent.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jakemt04.gapi.GCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.spudpvp.ccevent.EventListener;

import java.util.Collections;
import java.util.List;

public class TeamChatSpyCommand extends GCommand {
    public TeamChatSpyCommand() {
        super(
                "teamchatspy",
                "Toggle team chat spy",
                "spudpvp.event",
                Collections.emptyList(),
                false,
                "tcspy"
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if (EventListener.SPYING.contains(player.getUniqueId())) {
            EventListener.SPYING.remove(player.getUniqueId());
            player.sendMessage(Utils.c("&6You've &cdisabled Team Chat Spy&6."));
        } else {
            EventListener.SPYING.add(player.getUniqueId());
            player.sendMessage(Utils.c("&6You've &aenabled Team Chat Spy&6."));
        }

    }
}
