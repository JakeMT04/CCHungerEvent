package xyz.spudpvp.ccevent.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.jakemt04.gapi.Argument;
import xyz.jakemt04.gapi.GSubCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.spudpvp.ccevent.CCTeam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyPointsCommand extends GSubCommand {
    public ModifyPointsCommand() {
        super(
                "modifypoints",
                "event modifypoints",
                "Modifies points to a team",
                "spudpvp.event",
                Arrays.asList(
                        new Argument("team", true, "The team to change points of", s -> CCTeam.getTeams().stream().map(CCTeam::getName).sorted().collect(Collectors.toList())),
                        new Argument("points", false, "The points to add (or remove if negative), defaults to 1", s -> Collections.emptyList())

                ),
                false,
                "modpoints"
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sender.sendMessage(buildHelpMessage());
            return;
        }
        String teamName = args.get(0);
        int points;
        try {
            points = Integer.parseInt(args.get(1));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            points = 1;
        }
        CCTeam t = CCTeam.get(teamName);
        if (t == null) {
            sender.sendMessage(Utils.c("&cA team with the name of &f" + teamName + "&c does not exist."));
            return;
        }
        t.addPoints(points);
        sender.sendMessage(Utils.c("&6You've " + (points > 0 ? "added" : "taken") + " &f" + (points > 0 ? points : -1 * points) + "&6 points " + (points > 0 ? "to" : "from") + " the " + t.getColor() + t.getName() + " team&6."));
        Bukkit.broadcastMessage(Utils.c("&2&lEvent &8» &f" + (points > 0 ? points : -1 * points) + "&6 points have been " + (points > 0 ? "added to" : "taken from") + " the " + t.getColor() + t.getName() + " team&6."));
    }
}
