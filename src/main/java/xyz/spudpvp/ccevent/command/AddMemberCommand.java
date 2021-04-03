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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddMemberCommand extends GSubCommand {
    public AddMemberCommand() {
        super(
                "addmember",
                "event addmember",
                "Adds a member to a team",
                "spudpvp.event",
                Arrays.asList(
                        new Argument("player", true, "The player to add to a team", s -> Utils.getPlayers(s).stream().map(Player::getName).collect(Collectors.toList())),
                        new Argument("team", true, "The team to add the player to", s -> CCTeam.getTeams().stream().map(CCTeam::getName).sorted().collect(Collectors.toList()))
                ),
                false
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
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
        if (t != null) {
            sender.sendMessage(Utils.c("&c" + player.getName() + "&c is already a member of a team (" + t.getColor() + t.getName() + "&c)."));
            return;
        }
        t = CCTeam.get(args.get(1));
        if (t == null) {
            sender.sendMessage(Utils.c("&cA team with the name of &f" + args.get(1) + "&c does not exist."));
            return;
        }
        t.addMember(player.getUniqueId());
        sender.sendMessage(Utils.c("&6You've added &f" + player.getName() + "&6 to the " + t.getColor() + t.getName() + " team&6."));
        if (player instanceof Player) {
            AboveHeadNamesManager.manager.update((Player) player);
        }
    }
}
