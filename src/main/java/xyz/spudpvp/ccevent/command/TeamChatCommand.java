package xyz.spudpvp.ccevent.command;

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jakemt04.gapi.Argument;
import xyz.jakemt04.gapi.GCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.spudpvp.ccevent.CCTeam;
import xyz.spudpvp.ccevent.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TeamChatCommand extends GCommand {
    public TeamChatCommand() {
        super(
                "teamchat",
                "Enters team chat",
                "spudpvp.teamchat",
                Collections.singletonList(
                        new Argument("message", false, "The message to send", s -> new ArrayList<>())
                ),
                false,
                "tc"
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (args.size() == 0) {
            if (EventListener.TOGGLED.contains(player.getUniqueId())) {
                EventListener.TOGGLED.remove(player.getUniqueId());
                player.sendMessage(Utils.c("&6You're &cno longer talking in Team Chat&6."));
            } else {
                EventListener.TOGGLED.add(player.getUniqueId());
                player.sendMessage(Utils.c("&6You're &anow talking in Team Chat&6."));
            }
        } else {
            CCTeam team = CCTeam.get(player);
            if (team == null) {
                return;
            }

            String rawMessage = Joiner.on(" ").join(args);
            String message = Utils.c("&8(&2Team Chat&8) " + player.getDisplayName() + "&f: " + rawMessage);
            for (UUID uuid : team.getMembers()) {
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
            }
            String msg = Utils.c("&8(&2Team Chat Spy&8) &8(" + team.getColor() + team.getName() + "&8) " + player.getDisplayName() + "&f: " + rawMessage);
            for (UUID uuid : EventListener.SPYING) {
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(msg);
                }
            }
            Bukkit.getConsoleSender().sendMessage(msg);
        }
    }
}
