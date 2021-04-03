package xyz.spudpvp.ccevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.jakemt04.gapi.Utils;
import xyz.jakemt04.gperms.bukkit.abovehead.Manager;
import xyz.jakemt04.gperms.common.holder.Rank;

public class AboveHeadNamesImpl implements Manager {
    @Override
    public void update(Player player) {
        createTeams();
        CCTeam team = CCTeam.get(player);
        String teamName;
        if (team == null) {
            teamName = "zzzznoteam";
        } else {
            teamName = team.getName().toLowerCase();
        }
        for (Player p: Bukkit.getOnlinePlayers()) {
            Scoreboard b = p.getScoreboard();
            if (b == null) {
                continue;
            }
            Team t = b.getTeam(teamName);
            if (t != null) {
                t.addEntry(player.getName());
            }
        }
    }

    @Override
    public void updateAllFor(Player p) {
        createTeams();
        Scoreboard b = p.getScoreboard();
        if (b == null) {
            return;
        }
        for (Player player: Bukkit.getOnlinePlayers()) {
            CCTeam team = CCTeam.get(player);
            String teamName;
            if (team == null) {
                teamName = "zzzznoteam";
            } else {
                teamName = team.getName().toLowerCase();
            }
            Team t = b.getTeam(teamName);
            if (t != null) {
                t.addEntry(player.getName());
            }
        }
    }

    public void createTeams() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard b = p.getScoreboard() == null ? Bukkit.getScoreboardManager().getNewScoreboard() : p.getScoreboard();
            p.setScoreboard(b);
            Objective objective = b.getObjective("healthdisp");
            if (objective == null) {
                objective = b.registerNewObjective("healthdisp", "health");
                objective.setDisplayName(Utils.c("&4❤"));
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    objective.getScore(player.getName()).setScore((int) player.getHealth());
                }

            }
            for (CCTeam team : CCTeam.getTeams()) {
                Team t = b.getTeam(team.getName().toLowerCase());
                if (t == null) {
                    t = b.registerNewTeam(team.getName().toLowerCase());
                }
                t.setCanSeeFriendlyInvisibles(true);
                t.setAllowFriendlyFire(false);
                t.setPrefix(Utils.c(team.getColor()));
            }
            Team no = b.getTeam("zzzznoteam");
            if (no == null) {
                no = b.registerNewTeam("zzzznoteam");
            }

            no.setAllowFriendlyFire(false);
            no.setPrefix(Utils.c("&7&o"));
        }
    }

    @Override
    public void createTeamFor(Rank rank) {
        // the system doesn't work this way
    }
}
