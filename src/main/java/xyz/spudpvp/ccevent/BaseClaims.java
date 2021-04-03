package xyz.spudpvp.ccevent;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jakemt04.gapi.Utils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseClaims implements Listener {


    public static final HashMap<String, List<Location>> CLAIMS = new HashMap<>();

    public static Wool chatColorToWoolColour(String color) {
        String colorReplace = color
                .replace("&o", "")
                .replace("&l", "")
                .replace("&n", "")
                .replace("&m", "")
                .replace("&k", "");
        Wool wool = new Wool();
        switch (colorReplace) {
            case "&0":
                wool.setColor(DyeColor.BLACK);
                break;
            case "&1":
            case "&9":
                wool.setColor(DyeColor.BLUE);
                break;
            case "&2":
                wool.setColor(DyeColor.GREEN);
                break;
            case "&3":
                wool.setColor(DyeColor.CYAN);
                break;
            case "&4":
            case "&c":
                wool.setColor(DyeColor.RED);
                break;
            case "&5":
                wool.setColor(DyeColor.PURPLE);
                break;
            case "&6":
                wool.setColor(DyeColor.ORANGE);
                break;
            case "&7":
                wool.setColor(DyeColor.SILVER);
                break;
            case "&8":
                wool.setColor(DyeColor.GRAY);
                break;
            case "&a":
                wool.setColor(DyeColor.LIME);
                break;
            case "&b":
                wool.setColor(DyeColor.LIGHT_BLUE);
                break;
            case "&d":
                wool.setColor(DyeColor.PINK);
                break;
            case "&e":
                wool.setColor(DyeColor.YELLOW);
                break;
            case "&f":
            default:
                wool.setColor(DyeColor.WHITE);
                break;
        }
        return wool;
    }

    public static void createClaimStructure(CCTeam team, Location location) {
        if (team == null) {
            setBlock(location.clone().add(0, 1, 0), Material.WOOL, DyeColor.GRAY.getWoolData());
        } else {
            setBlock(location.clone().add(0, 1, 0), Material.WOOL, chatColorToWoolColour(team.getColor()).getColor().getWoolData());
        }
        Bukkit.getScheduler().runTaskLater(CCEventPlugin.get(), () -> location.clone().getBlock().setType(Material.QUARTZ_BLOCK), 1L);
        location.clone().add(0, 2, 0).getBlock().setType(Material.QUARTZ_BLOCK);

        setBlock(location.clone().add(-1, 0, 0), Material.QUARTZ_STAIRS, 4);
        setBlock(location.clone().add(1, 0, 0), Material.QUARTZ_STAIRS, 5);
        setBlock(location.clone().add(0, 0, -1), Material.QUARTZ_STAIRS, 6);
        setBlock(location.clone().add(0, 0, 1), Material.QUARTZ_STAIRS, 7);
        /*
        setBlock(location.clone().add(1, 0, 1), Material.QUARTZ_STAIRS, 5);
        setBlock(location.clone().add(1, 0, -1), Material.QUARTZ_STAIRS, 5);
        setBlock(location.clone().add(-1, 0, -1), Material.QUARTZ_STAIRS, 4);
        setBlock(location.clone().add(-1, 0, 1), Material.QUARTZ_STAIRS, 4);

         */
        setBlock(location.clone().add(-1, 2, 0), Material.QUARTZ_STAIRS, 0);
        setBlock(location.clone().add(1, 2, 0), Material.QUARTZ_STAIRS, 1);
        setBlock(location.clone().add(0, 2, -1), Material.QUARTZ_STAIRS, 2);
        setBlock(location.clone().add(0, 2, 1), Material.QUARTZ_STAIRS, 3);

        placeAndUpdateSigns(team, location.clone().add(0, 1, 0));
        /*

        setBlock(location.clone().add(1, 2, 1), Material.QUARTZ_STAIRS, 1);
        setBlock(location.clone().add(1, 2, -1), Material.QUARTZ_STAIRS, 1);
        setBlock(location.clone().add(-1, 2, -1), Material.QUARTZ_STAIRS, 0);
        setBlock(location.clone().add(-1, 2, 1), Material.QUARTZ_STAIRS, 0);


         */

    }

    public static void placeAndUpdateSigns(CCTeam team, Location center) {
        setBlock(center.clone().add(1, 0, 0), Material.WALL_SIGN, 5);
        setBlock(center.clone().add(0, 0, 1), Material.WALL_SIGN, 3);
        setBlock(center.clone().add(0, 0, -1), Material.WALL_SIGN, 2);
        setBlock(center.clone().add(-1, 0, 0), Material.WALL_SIGN, 4);
        Sign sign1 = (Sign) center.clone().add(1, 0, 0).getBlock().getState();
        Sign sign2 = (Sign) center.clone().add(0, 0, 1).getBlock().getState();
        Sign sign3 = (Sign) center.clone().add(0, 0, -1).getBlock().getState();
        Sign sign4 = (Sign) center.clone().add(-1, 0, 0).getBlock().getState();

        String line = team == null ? "&9Click to claim" : team.getColor() + team.getName();
        sign1.setLine(0, Utils.c("&9[Base Claim]"));
        sign1.setLine(2, Utils.c(line));
        sign2.setLine(0, Utils.c("&9[Base Claim]"));
        sign2.setLine(2, Utils.c(line));
        sign3.setLine(0, Utils.c("&9[Base Claim]"));
        sign3.setLine(2, Utils.c(line));
        sign4.setLine(0, Utils.c("&9[Base Claim]"));
        sign4.setLine(2, Utils.c(line));
        sign1.update();
        sign2.update();
        sign3.update();
        sign4.update();
    }

    private static void setBlock(Location location, Material material, int data) {
        location.getBlock().setType(material);
        location.getBlock().setData((byte) data);
    }


    public static boolean isBlockPartOfAClaimStructure(Location location) {
        for (List<Location> claimsA : CLAIMS.values()) {
            for (Location claim : claimsA) {
                if (location.getWorld() != claim.getWorld()) {
                    continue;
                }
                if (location.distanceSquared(claim) <= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Location getCenterPoint(Location location) {
        for (List<Location> claimsA : CLAIMS.values()) {
            for (Location claim : claimsA) {
                if (location.distanceSquared(claim) <= 2) {
                    return claim;
                }
            }
        }
        return null;
    }

    public static void load() {
        for (String key : File.get().getKeys(false)) {
            List<Location> locs = File.get().getStringList(key).stream().map(s -> {
                String[] ss = s.split(Pattern.quote(","));
                String world = ss[0];
                double x = Double.parseDouble(ss[1]);
                double y = Double.parseDouble(ss[2]);
                double z = Double.parseDouble(ss[3]);
                return new Location(Bukkit.getWorld(world), x, y, z);
            }).collect(Collectors.toList());
            CLAIMS.put(key, locs);
        }
    }

    @EventHandler
    public void onFire(BlockBurnEvent event) {
        if (isBlockPartOfAClaimStructure(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        CCTeam t = CCTeam.get(event.getPlayer());
        if (event.getItemInHand().hasItemMeta()) {
            if (event.getItemInHand().getItemMeta().hasDisplayName()) {
                if (event.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.c("&6&lBase claim marker")) && event.getItemInHand().getType() == Material.CHEST) {
                    event.setCancelled(true);
                    if (isBlockPartOfAClaimStructure(event.getBlock().getLocation())) {
                        event.getPlayer().sendMessage(Utils.c("&cYou can't place this here."));
                        return;
                    }
                    List<Location> claim = CLAIMS.getOrDefault(t == null ? "none" : t.getName(), new ArrayList<>());
                    claim.add(event.getBlock().getLocation().clone().add(0, 1, 0));
                    CLAIMS.put(t == null ? "none" : t.getName(), claim);
                    saveClaims();
                    createClaimStructure(t, event.getBlockPlaced().getLocation().clone());
                    if (t == null) {
                        event.getPlayer().sendMessage(Utils.c("&6You've created a blank claim structure."));
                    } else {
                        event.getPlayer().sendMessage(Utils.c("&6You've created a claim structure for your team."));
                    }
                    return;
                }
            }
        }
        /*
        if (event.getItemInHand().getType() == Material.WOOL) {
            if (isBlockPartOfAClaimStructure(event.getBlockPlaced().getLocation())) {
                Location loc = getCenterPoint(event.getBlockPlaced().getLocation());
                if (loc != null) {
                    for (Map.Entry<String, List<Location>> claimsA : claims.entrySet()) {
                        if (claimsA.getValue().contains(loc)) {
                            if (!claimsA.getKey().equals("none")) {
                                event.getPlayer().sendMessage(Utils.c("&cThis base is currently claimed. Unclaim it to claim it for your own team."));
                                event.setCancelled(true);
                                return;
                            } else {
                                claimsA.getValue().remove(loc);
                            }
                        }
                    }
                    List<Location> claim = claims.getOrDefault(t.getName(), new ArrayList<>());
                    claim.add(loc.clone());
                    setBlock(loc.clone(), Material.WOOL, chatColorToWoolColour(t.getColor()).getColor().getWoolData());
                    claims.put(t.getName(), claim);
                    saveClaims();
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Utils.c("&6You've claimed this base for your team."));
                    placeAndUpdateSigns(t, loc.clone());
                    return;
                }
            }
        }

         */
        if (isBlockPartOfAClaimStructure(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.c("&cYou can't place this here."));
        }

    }

    private static boolean isBlockCenterOfClaimStructure(Location location) {
        for (List<Location> claimsA : CLAIMS.values()) {
            if (claimsA.contains(location)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        CCTeam t = CCTeam.get(event.getPlayer());
        if (t == null) {
            return;
        }
        Block block;
        if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_AIR) {
            Block targetBlock = null;
            try {
                targetBlock = event.getPlayer().getTargetBlock((Set<Material>) null, 5);
            } catch (IllegalStateException ignored) {

            }
            block = targetBlock;
        } else {
            block = event.getClickedBlock();
        }
        if (block == null) {
            return;
        }
        Material mat = block.getType();
        if (mat == Material.WALL_SIGN) {
            if (isBlockPartOfAClaimStructure(block.getLocation())) {
                Location loc = getCenterPoint(block.getLocation());
                if (loc != null) {
                    event.setCancelled(true);
                    String oldTeam = "&fUnclaimed";
                    for (Map.Entry<String, List<Location>> claimsA : CLAIMS.entrySet()) {
                        if (claimsA.getValue().contains(loc)) {
                            if (!claimsA.getKey().equals("none")) {
                                CCTeam tt = CCTeam.get(claimsA.getKey());
                                if (tt != null) {
                                    if (tt.getName().equals(t.getName())) {
                                        return;
                                    }
                                    oldTeam = tt.getColor() + tt.getName();
                                    if (EventListener.WAR_START_DATE.after(new Date())) {
                                        event.getPlayer().sendMessage(Utils.c("&cYou can't claim another teams base until war has started."));
                                        return;
                                    }

                                }
                            }
                            claimsA.getValue().remove(loc);
                            break;
                        }
                    }
                    List<Location> claim = CLAIMS.getOrDefault(t.getName(), new ArrayList<>());
                    claim.add(loc.clone());
                    setBlock(loc.clone(), Material.WOOL, chatColorToWoolColour(t.getColor()).getColor().getWoolData());
                    CLAIMS.put(t.getName(), claim);
                    saveClaims();

                    event.getPlayer().sendMessage(Utils.c("&6You've claimed this base for your team."));
                    Bukkit.broadcastMessage(Utils.c("&2&lEvent &8» " + t.getColor() + event.getPlayer().getName() + " &6has claimed a " + oldTeam + " Base&6."));
                    placeAndUpdateSigns(t, loc.clone());
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (isBlockPartOfAClaimStructure(event.getBlock().getLocation()) && event.getPlayer().isOp() && event.getPlayer().isSneaking()) {
            Location loc = getCenterPoint(event.getBlock().getLocation());
            if (loc != null) {
                for (Map.Entry<String, List<Location>> claimsA : CLAIMS.entrySet()) {
                    claimsA.getValue().remove(loc);
                }
                saveClaims();
                removeClaimStructure(loc);
            }
            return;
        }
        if (isBlockPartOfAClaimStructure(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        /*
        if (isBlockCenterOfClaimStructure(event.getBlock().getLocation())) {
            event.setCancelled(true);
            String oldTeam = "Error";
            for (Map.Entry<String, List<Location>> claimsA : claims.entrySet()) {
                if (claimsA.getValue().remove(event.getBlock().getLocation())) {
                    oldTeam = claimsA.getKey();
                    break;
                }
            }
            List<Location> claim = claims.getOrDefault("none", new ArrayList<>());
            claim.add(event.getBlock().getLocation().clone());
            claims.put("none", claim);
            if (oldTeam.equals("none")) {
                return;
            }
            CCTeam team = CCTeam.get(oldTeam);
            if (team != null) {
                oldTeam = team.getColor() + team.getName();
            }
            event.getPlayer().sendMessage(Utils.c("&cYou've removed the base claim for this base (was claimed by &f" + oldTeam + " team&c)"));
            setBlock(event.getBlock().getLocation(), Material.WOOL, DyeColor.GRAY.getWoolData());
            placeAndUpdateSigns(null, event.getBlock().getLocation().clone());
        } else if (isBlockPartOfAClaimStructure(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }

         */
    }

    private void removeClaimStructure(Location location) {
        location.clone().add(1, 0, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, 0, 1).getBlock().setType(Material.AIR);
        location.clone().add(0, 0, -1).getBlock().setType(Material.AIR);
        location.clone().add(-1, 0, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, 0, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
        location.clone().getBlock().setType(Material.AIR);
        location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
        location.clone().add(-1, -1, 0).getBlock().setType(Material.AIR);
        location.clone().add(1, -1, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, -1, -1).getBlock().setType(Material.AIR);
        location.clone().add(0, -1, 1).getBlock().setType(Material.AIR);
        location.clone().add(-1, 1, 0).getBlock().setType(Material.AIR);
        location.clone().add(1, 1, 0).getBlock().setType(Material.AIR);
        location.clone().add(0, 1, -1).getBlock().setType(Material.AIR);
        location.clone().add(0, 1, 1).getBlock().setType(Material.AIR);


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent e) {
        e.blockList().removeIf(b -> isBlockPartOfAClaimStructure(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode2(EntityExplodeEvent e) {
        e.blockList().removeIf(b -> isBlockPartOfAClaimStructure(b.getLocation()));
    }

    private static void saveClaims() {
        for (Map.Entry<String, List<Location>> locs : CLAIMS.entrySet()) {
            List<String> l = new ArrayList<>();
            for (Location loc : locs.getValue()) {
                l.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ());
            }
            File.get().set(locs.getKey(), l.toArray());
        }
        File.save();
    }

    public static final class File {
        private static YamlConfiguration cfg;
        private static java.io.File configFile;

        public static void init(JavaPlugin plugin) {
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }
                configFile = new java.io.File(plugin.getDataFolder(), "claims.yml");
                if (!configFile.exists()) {
                    plugin.saveResource("claims.yml", false);
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
