package xyz.spudpvp.ccevent.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jakemt04.gapi.Argument;
import xyz.jakemt04.gapi.GSubCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.jakemt04.gperms.bukkit.abovehead.AboveHeadNamesManager;
import xyz.spudpvp.ccevent.CCTeam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveMemberCommand extends GSubCommand {
    public RemoveMemberCommand() {
        super(
                "removemember",
                "event removemember",
                "Removes a member from a team",
                "spudpvp.event",
                Collections.singletonList(
                        new Argument("player", true, "The player to remove from a team", s -> Utils.getPlayers(s).stream().map(Player::getName).collect(Collectors.toList()))
                ),
                false
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sender.sendMessage(buildHelpMessage());
            return;
        }
        OfflinePlayer player = Utils.getPlayerOnline(sender, args.get(0));
        if (player == null) {
            // the player is not online, try fetch from offline cache
            for (OfflinePlayer o : Bukkit.getOfflinePlayers()) {
                if (o.getName().equalsIgnoreCase(args.get(0))) {
                    player = o;
                    break;
                }
            }
            if (player == null) {
                sender.sendMessage(Utils.c("&cNo player matching &f" + args.get(0) + "&c has joined this server."));
                return;
            }
        }
        CCTeam t = CCTeam.get(player.getUniqueId());
        if (t == null) {
            sender.sendMessage(Utils.c("&c" + player.getName() + "&c is not a member of any team."));
            return;
        }
        t.removeMember(player.getUniqueId());
        sender.sendMessage(Utils.c("&cYou've removed &f" + player.getName() + "&c from the " + t.getColor() + t.getName() + " team&c."));
        if (player instanceof Player) {
            AboveHeadNamesManager.manager.update((Player) player);
        }
    }
}
