package xyz.spudpvp.ccevent;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jakemt04.gapi.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CCTeam {
    private static final List<CCTeam> teams = new ArrayList<>();

    public static List<CCTeam> getTeams() {
        return ImmutableList.copyOf(teams);
    }

    private final List<UUID> members = new ArrayList<>();
    @Getter
    private final String name;
    @Getter
    private final String color;
    @Getter
    @Setter
    private int points;

    private CCTeam(String name, String color) {
        this.color = color;
        this.name = name;
        this.points = 0;
        teams.add(this);
    }

    public List<UUID> getMembers() {
        return ImmutableList.copyOf(members);
    }

    public static CCTeam get(Player player) {
        for (CCTeam team : getTeams()) {
            if (team.getMembers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public static CCTeam get(UUID player) {

        for (CCTeam team : getTeams()) {
            if (team.getMembers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public static CCTeam get(String name) {
        for (CCTeam team : getTeams()) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public static CCTeam create(String name, String code) {
        code = Utils.colourOnly(code);
        CCTeam t = new CCTeam(name, code);
        t.save();
        return t;
    }

    public void save() {
        File.get().set("teams." + name + ".color", color);
        File.get().set("teams." + name + ".points", points);
        File.get().set("teams." + name + ".members", members.stream().map(UUID::toString).toArray());
        File.save();
    }

    public void load() {
        points = File.get().getInt("teams." + name + ".points", 0);
        members.clear();
        members.addAll(File.get().getStringList("teams." + name + ".members").stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    public static void loadAll() {
        ConfigurationSection c = File.get().getConfigurationSection("teams");
        if (c == null) {
            return;
        }
        for (String team : c.getKeys(false)) {
            new CCTeam(team, c.getString(team + ".color")).load();
        }
    }

    public void addMember(UUID uniqueId) {
        members.add(uniqueId);
        save();
    }

    public void removeMember(UUID uniqueId) {
        members.remove(uniqueId);
        save();
    }

    public void addPoints(int points) {
        this.points += points;
        save();
    }


    public static final class File {
        private static YamlConfiguration cfg;
        private static java.io.File configFile;

        public static void init(JavaPlugin plugin) {
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }
                configFile = new java.io.File(plugin.getDataFolder(), "teams.yml");
                if (!configFile.exists()) {
                    plugin.saveResource("teams.yml", false);
                }

                cfg = YamlConfiguration.loadConfiguration(configFile);
                cfg.options().copyDefaults(true);
                plugin.getLogger().info("Created and loaded configuration file");
            } catch (Exception e) {
                plugin.getLogger().severe("Failed parsing config file");


            }
        }

        public static Configuration get() {
            return cfg;
        }

        public static void save() {
            try {
                cfg.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
