package xyz.spudpvp.ccevent.command;

import org.bukkit.command.CommandSender;
import xyz.jakemt04.gapi.Argument;
import xyz.jakemt04.gapi.GSubCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.spudpvp.ccevent.CCTeam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateTeamCommand extends GSubCommand {
    public CreateTeamCommand() {
        super(
                "createteam",
                "event createteam",
                "Creates a new Event Team",
                "spudpvp.event",
                Arrays.asList(
                        new Argument("name", true, "The name of the team", s -> Collections.emptyList()),
                        new Argument("colour", true, "The colour code for the team", s -> Collections.emptyList())
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
        String teamName = args.get(0);
        String teamColor = Utils.colourOnly(args.get(1));
        if (teamColor.equalsIgnoreCase("")) {
            sender.sendMessage(Utils.c(args.get(0) + "&c is not a valid colour."));
            return;
        }
        CCTeam t = CCTeam.get(teamName);
        if (t != null) {
            sender.sendMessage(Utils.c("&cA team with the name &f" + t.getName() + "&c already exists."));
            return;
        }
        t = CCTeam.create(teamName, teamColor);
        String e = Utils.explainColour(teamColor);
        sender.sendMessage(Utils.c("&6You've created a new team called &f" + t.getName() + "&6 with a colour of " + teamColor + e + "&6."));
    }
}
