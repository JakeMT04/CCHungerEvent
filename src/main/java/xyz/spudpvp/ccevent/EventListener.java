package xyz.spudpvp.ccevent;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.jakemt04.gapi.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventListener implements Listener {
    public static final Date WAR_START_DATE;
    public static final List<UUID> TOGGLED = new ArrayList<>();
    public static final List<UUID> SPYING = new ArrayList<>();

    static {
        try {
            WAR_START_DATE = new SimpleDateFormat("dd.MM.yyyy.kk.mm").parse("30.07.2020.16.00");
        } catch (ParseException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @EventHandler
    public void onAchievemnt(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        CCTeam team = CCTeam.get(event.getPlayer());
        format = format.replace("{raw-name}", Utils.f(event.getPlayer().getDisplayName()));
        if (team == null) {
            format = format.replace("{cc-team}", "Spectator");
            format = format.replace("{cc-team-c}", Utils.c("&7&o"));
        } else {
            format = format.replace("{cc-team}", team.getName());
            format = format.replace("{cc-team-c}", Utils.c(team.getColor()));
        }
        event.setFormat(format);
    }

    @EventHandler
    public void teamChat(AsyncPlayerChatEvent event) {
        CCTeam team = CCTeam.get(event.getPlayer());
        if (team == null) {
            return;
        }
        if (event.isCancelled()) return;
        if (TOGGLED.contains(event.getPlayer().getUniqueId())) {
            String message = Utils.c("&8(&2Team Chat&8) " + event.getPlayer().getDisplayName() + "&f: " + event.getMessage());
            for (UUID uuid : team.getMembers()) {
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(message);
                }
            }
            String msg = Utils.c("&8(&2Team Chat Spy&8) &8(" + team.getColor() + team.getName() + "&8) " + event.getPlayer().getDisplayName() + "&f: " + event.getMessage());
            for (UUID uuid : SPYING) {
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(msg);
                }
            }
            Bukkit.getConsoleSender().sendMessage(msg);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftItem(PrepareItemCraftEvent e) {
        ItemStack item = e.getRecipe().getResult();
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Player) {
            Player player = (Player) holder;
            if (item.getType().equals(Material.GOLDEN_APPLE) && item.getDurability() == (short) 1) {
                player.sendMessage(Utils.c("&cGod apples are not allowed."));
                e.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
        CCTeam t = CCTeam.get(event.getEntity());
        if (t == null) {
            return;
        }
        String m = getDeathMessage(event.getEntity());
        Bukkit.broadcastMessage(Utils.c("&c&lDeath &8» " + m + "&6."));
        if (WAR_START_DATE.after(new Date())) {
            return;
        }
        t.addPoints(-1);
        if (event.getEntity().getKiller() != null) {
            CCTeam tt = CCTeam.get(event.getEntity().getKiller());
            if (tt == null) {
                Bukkit.broadcastMessage(Utils.c("&2&lEvent &8» &f1&6 points have been taken from the " + t.getColor() + t.getName() + " team &6because of a death."));
            } else {
                tt.addPoints(2);
                Bukkit.broadcastMessage(Utils.c(
                        "&2&lEvent &8» &f1&6 point has been taken from the " +
                                t.getColor() + t.getName() +
                                " team&6 and &f2&6 points have been added to the " +
                                tt.getColor() + tt.getName() + " team&6 because of a kill."));

            }
        } else {
            Bukkit.broadcastMessage(Utils.c("&2&lEvent &8» &f1&6 point has been taken from the " + t.getColor() + t.getName() + " team &6because of a death."));
        }

    }

    public static String getDeathMessage(Player player) {
        ChatMessage deathMessage = ((ChatMessage) ((CraftPlayer) player).getHandle().bs().b());
        String code = deathMessage.i();
        String text = LocaleI18n.get(code);
        return Utils.c("&6" +
                String.format(text,
                        Arrays.stream(deathMessage.j())
                                .map(o -> {
                                    if (o instanceof IChatBaseComponent) {
                                        StringBuilder builder = new StringBuilder();
                                        (((IChatBaseComponent) o).a()).forEach(a -> builder.append(a.getText()));
                                        return Utils.f(((IChatBaseComponent) o).getText() + builder.toString());
                                    }
                                    return Utils.f(o.toString());
                                })
                                .map(o -> {
                                    if (Bukkit.getPlayer(o) != null) {
                                        CCTeam t = CCTeam.get(Bukkit.getPlayer(o));
                                        if (t != null) {
                                            return t.getColor() + Bukkit.getPlayer(o).getName() + "&6";
                                        } else {
                                            return "&7&o" + Bukkit.getPlayer(o).getName() + "&6";
                                        }
                                    }
                                    return "&f" + o + "&6";
                                }).toArray())
        );

    }
}
