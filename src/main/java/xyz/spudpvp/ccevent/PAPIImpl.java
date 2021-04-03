package xyz.spudpvp.ccevent;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PAPIImpl extends PlaceholderExpansion {
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "cc";
    }

    @Override
    public String getAuthor() {
        return "JakeMT04";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (identifier.startsWith("points_")) {
            String team = identifier.replace("points_", "").trim();
            CCTeam t = CCTeam.get(team);
            if (t != null) {
                return String.valueOf(t.getPoints());
            } else {
                return "0";
            }
        } else if (identifier.equalsIgnoreCase("po")) {
            return String.valueOf(Bukkit.getOnlinePlayers().stream().filter(p::canSee).count());
        }
        return null;
    }
}
