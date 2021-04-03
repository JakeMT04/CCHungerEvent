package xyz.spudpvp.ccevent;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.jakemt04.gapi.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SafeLootListener implements Listener {

    public static final ArrayList<ContainerChest> protectedChests = new ArrayList<>();

    private ContainerChest getChestAt(Location l) {
        for (ContainerChest c : protectedChests) {
            // test for chest 1
            Location c1 = c.getChest1().getLocation();
            Location c2 = c.getChest2().getLocation();

            if (l.getBlockX() == c1.getBlockX() && l.getBlockY() == c1.getBlockY() && l.getBlockZ() == c1.getBlockZ()) {
                return c;
            } else if (l.getBlockX() == c2.getBlockX() && l.getBlockY() == c2.getBlockY() && l.getBlockZ() == c2.getBlockZ()) {
                return c;
            }


        }
        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        List<ItemStack> drops = new ArrayList<>(e.getDrops());
        e.getDrops().removeAll(e.getDrops());
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CCEventPlugin.get(), new SafeLootThread(drops, p.getLocation().getBlock().getLocation(), p.getName(), e.getEntity()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (p.isSneaking()
                && p.getItemInHand() != null
                && p.getItemInHand().getType().isBlock()
                && p.getItemInHand().getType() != Material.AIR)
            return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {

            ContainerChest chest = getChestAt(block.getLocation());
            if (chest == null) {
                return;
            }
            if (chest.getCanOpen() == null) {
                return;
            }
            if (!p.getUniqueId().equals(chest.getCanOpen())) {
                e.setCancelled(true);
                p.sendMessage(Utils.c("&cYou can't open this chest yet."));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();
        if (block == null) return;
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {

            ContainerChest chest = getChestAt(block.getLocation());
            if (chest == null) {
                return;
            }
            e.setCancelled(true);
            p.sendMessage(Utils.c("&cThis chest is a loot chest and cannot be destroyed yet"));


        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent e) {
        //e.blockList().removeIf(b -> getChestAt(b.getLocation()) != null);

        Iterator<Block> blockIterator = e.blockList().iterator();
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            ContainerChest ch = getChestAt(b.getLocation());
            if (ch != null) {
                blockIterator.remove();
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode2(EntityExplodeEvent e) {
        //e.blockList().removeIf(b -> getChestAt(b.getLocation()) != null);

        Iterator<Block> blockIterator = e.blockList().iterator();
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            ContainerChest ch = getChestAt(b.getLocation());
            if (ch != null) {
                blockIterator.remove();
            }

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.HOPPER || event.getBlockPlaced().getType() == Material.HOPPER_MINECART) {
            Location location = event.getBlock().getLocation().clone().add(0, 1, 0);
            for (ContainerChest chest : protectedChests) {
                Location chest1 = chest.getChest1().getLocation();
                Location chest2 = chest.getChest2().getLocation();
                if (chest1.equals(location) || chest2.equals(location)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Utils.c("&cYou can't place a hopper underneath a loot chest."));
                }
            }
        }
    }
    @EventHandler
    public void onThing(ChunkUnloadEvent event) {
        for (ContainerChest chest : protectedChests) {
            if (chest.getChest1().getLocation().getChunk().equals(event.getChunk()) || chest.getChest2().getLocation().getChunk().equals(event.getChunk())) {
                event.setCancelled(true);
            }
        }
    }
}

class ContainerChest {
    private final Block chest1;
    private final Block chest2;
    private UUID canOpen;


    ContainerChest(Block chest1, Block chest2, UUID canOpen) {
        this.chest1 = chest1;
        this.chest2 = chest2;
        this.canOpen = canOpen;
    }

    public Block getChest1() {
        return chest1;
    }

    public Block getChest2() {
        return chest2;
    }

    public UUID getCanOpen() {
        return canOpen;
    }

    public void unlock() {
        this.canOpen = null;
        SafeLootListener.protectedChests.remove(this);
    }

}

class SafeLootThread implements Runnable {
    private ArmorStand armorStand;
    private ContainerChest chest;
    private final SafeLootThread thread;
    private long timeLeft;
    private final Location loc;
    private final List<ItemStack> drops;
    private boolean spawned;
    private final UUID lockedTo;
    private final String nameCache;

    public SafeLootThread(List<ItemStack> drops, Location loc, String name, Player lockedTo) {
        this.drops = drops;
        this.loc = loc;
        this.lockedTo = lockedTo.getUniqueId();
        this.nameCache = lockedTo.getDisplayName();
        thread = this;
        timeLeft = 120;
        spawned = false;
    }

    @Override
    public void run() {
        if (!spawned) {
            if (!spawn()) {
                return;
            }
        }
        try {
            if (timeLeft > 0) {
            /*
            30 - 20 - green
            20 - 10 - yellow
            10 - 0 - red
             */
                timeLeft--;
                String color;
                if (timeLeft > 60) {
                    color = "&a";
                } else if (timeLeft > 30) {
                    color = "&e";
                } else {
                    color = "&c";
                }
                String t = DurationFormatUtils.formatDurationWords(timeLeft * 1000, true, true);
                armorStand.setCustomName(Utils.c("&6Locked to &f" + nameCache + "&6 for " + color + t));
                Bukkit.getScheduler().scheduleSyncDelayedTask(CCEventPlugin.get(), thread, 20L);
            } else {
                armorStand.remove();
                chest.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    private boolean spawn() {
        if (drops.isEmpty()) {
            return false;
        }
        if (loc.getY() < 0) {
            return false;
        }
        Material type = Material.CHEST;
        spawned = true;
        Block chest1;
        Block chest2;
        chest1 = loc.getBlock();
        loc.add(1, 0, 0);
        chest2 = loc.getBlock();
        boolean safe = false;
        while (!safe) {
            if (chest1.getType() == Material.CHEST) {
                chest1 = loc.getBlock();
                loc.add(1, 0, 0);
                chest2 = loc.getBlock();
                continue;
            }
            if (chest2.getType() == Material.CHEST) {
                chest1 = loc.getBlock();
                loc.add(1, 0, 0);
                chest2 = loc.getBlock();
                continue;
            }
            if (chest1.getLocation().add(1, 0, 0).getBlock().getType() == Material.CHEST
                    || chest1.getLocation().subtract(1, 0, 0).getBlock().getType() == Material.CHEST
                    || chest1.getLocation().add(0, 0, 1).getBlock().getType() == Material.CHEST
                    || chest1.getLocation().subtract(0, 0, 1).getBlock().getType() == Material.CHEST
            ) {
                type = Material.TRAPPED_CHEST;
            }
            if (chest2.getLocation().add(1, 0, 0).getBlock().getType() == Material.CHEST
                    || chest2.getLocation().subtract(1, 0, 0).getBlock().getType() == Material.CHEST
                    || chest2.getLocation().add(0, 0, 1).getBlock().getType() == Material.CHEST
                    || chest2.getLocation().subtract(0, 0, 1).getBlock().getType() == Material.CHEST
            ) {
                type = Material.TRAPPED_CHEST;
            }
            safe = true;

        }
        this.chest = new ContainerChest(chest1, chest2, this.lockedTo);
        SafeLootListener.protectedChests.add(this.chest);
        chest1.setType(type);
        chest2.setType(type);


        Chest chest = (Chest) chest1.getState();

        setChestName(chest, Utils.c(nameCache + "'s &9Loot"));

        Inventory inv = chest.getInventory();

        for (ItemStack drop : drops) {
            if (drop == null || drop.getType() == Material.AIR) {
                continue;
            }
            inv.addItem(drop);
        }

        loc.add(0, -1, .5);

        armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName("");
        return true;


    }

    public static void setChestName(Chest chest, String name) {
        ((CraftChest) chest).getTileEntity().a(name);
    }
}