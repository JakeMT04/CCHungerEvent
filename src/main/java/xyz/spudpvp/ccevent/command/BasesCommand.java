package xyz.spudpvp.ccevent.command;

import org.bukkit.command.CommandSender;
import xyz.jakemt04.gapi.GCommand;
import xyz.jakemt04.gapi.Utils;
import xyz.spudpvp.ccevent.BaseClaims;
import xyz.spudpvp.ccevent.CCTeam;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BasesCommand extends GCommand {
    public BasesCommand() {
        super(
                "bases",
                "Lists claimed bases",
                "spudpvp.bases",
                Collections.emptyList(),
                false
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        boolean empty = true;
        StringBuilder response = new StringBuilder("&6Base claims:");
        for (CCTeam t : CCTeam.getTeams().stream().sorted(Comparator.comparing(CCTeam::getName)).collect(Collectors.toList())) {
            if (BaseClaims.CLAIMS.containsKey(t.getName())) {
                int amount = BaseClaims.CLAIMS.get(t.getName()).size();
                if (amount > 0) {
                    response.append("\n&8-> ").append(t.getColor()).append(t.getName()).append(" Team&6 controls &f").append(amount).append("&6 ").append(amount == 1 ? "base" : "bases").append(".");
                    empty = false;
                }
            }
        }
        if (empty) {
            sender.sendMessage(Utils.c("&cNo teams currently control any bases."));
        } else {
            sender.sendMessage(Utils.c(response.toString()));
        }
    }
}
