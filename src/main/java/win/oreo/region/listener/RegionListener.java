package win.oreo.region.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.Main;
import win.oreo.region.command.RegionCommand;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;
import win.oreo.region.util.Area;
import win.oreo.region.util.pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionListener implements Listener {
    public static HashMap<Player, Area> map = new HashMap<>();
    public static HashMap<Player, pos> pos1Map = new HashMap<>();
    public static HashMap<Player, pos> pos2Map = new HashMap<>();

    List<Player> coolDown = new ArrayList<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (coolDown.contains(player)) return;
        delay(player);
        if (!RegionCommand.editorSet.contains(player)) return;
        e.setCancelled(true);
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (map.containsKey(player)) {
                map.get(player).setX1(e.getClickedBlock().getX());
                map.get(player).setZ1(e.getClickedBlock().getZ());
            } else {
                pos pos = new pos(e.getClickedBlock().getX(), e.getClickedBlock().getZ());
                pos1Map.put(player, pos);
            }
            player.sendMessage("#1 설정 완료.");
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (map.containsKey(player)) {
                map.get(player).setX2(e.getClickedBlock().getX());
                map.get(player).setZ2(e.getClickedBlock().getZ());
            } else {
                pos pos = new pos(e.getClickedBlock().getX(), e.getClickedBlock().getZ());
                pos2Map.put(player, pos);
            }
            player.sendMessage("#2 설정 완료.");
        }

        if (map.containsKey(player)) {
            buyArea(player);
        } else {
            if (pos1Map.containsKey(player) && pos2Map.containsKey(player)) {
                map.put(player, new Area(pos1Map.get(player).getX(), pos2Map.get(player).getX(), pos1Map.get(player).getZ(), pos2Map.get(player).getZ()));
                pos1Map.remove(player);
                pos2Map.remove(player);
                buyArea(player);
            } else if (!pos1Map.containsKey(player) && !pos2Map.containsKey(player)){
                player.sendMessage("#1과 #2를 설정해 주세요.");
            } else if (!pos1Map.containsKey(player)) {
                player.sendMessage("#1을 설정해 주세요.");
            } else if (!pos2Map.containsKey(player)) {
                player.sendMessage("#2를 설정해 주세요.");
            }
        }
    }

    public void buyArea(Player player) {
        Area area = map.get(player);
        player.sendMessage(String.valueOf(RegionUtil.getSize(area)));
        if (!RegionUtil.checkOverlapping(area)) {
            if (!RegionUtil.playerCountMap.containsKey(player)) RegionUtil.playerCountMap.put(player, 0);
            if (RegionUtil.playerCountMap.get(player) >= RegionUtil.getSize(area)) {
                RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) - RegionUtil.getSize(area));
                RegionUtil.createRegion(area, player);
                player.sendMessage(Main.getConfigMessage("messages.edit.set"));
            } else {
                player.sendMessage(Main.getConfigMessage("messages.error.no-region"));
            }
        } else {
            player.sendMessage(Main.getConfigMessage("messages.edit.overlap"));
        }
        map.remove(player);
        RegionCommand.editorSet.remove(player);
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 10);
    }

    private static final HashMap<Player, Region> playerMap = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        for (Region region : RegionUtil.regionSet) {
            if (playerMap.containsKey(player)) {
                if (isPlayerIn(player, region)) {
                    if (checkRegionPermission(player, region)) return;
                    if (!playerMap.get(player).equals(region)) {
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&6&l" + region.getOwner() + "님의 지역"), "", 10, 70, 20);
                        playerMap.put(player, region);
                    }
                }
            } else {
                if (isPlayerIn(player, region)) {
                    if (checkRegionPermission(player, region)) return;
                    playerMap.put(player, region);
                }
            }
        }
    }

    public boolean checkRegionPermission(Player player, Region region) {
        if (!region.getRegionPermission().getAccessPlayers().contains(player)) {
            if (!region.getOwner().equals(player.getName())) {
                if (!player.hasPermission("administrators")) {
                    player.sendMessage("출입 권한이 부족합니다.");
                    if (region.getX1() >= player.getLocation().getBlockX()) {
                        player.teleport(player.getLocation().add(-2, 0, 0));
                    }
                    if (region.getX2() <= player.getLocation().getBlockX()) {
                        player.teleport(player.getLocation().add(2, 0, 0));
                    }
                    if (region.getZ1() >= player.getLocation().getBlockZ()) {
                        player.teleport(player.getLocation().add(0, 0, -2));
                    }
                    if (region.getZ2() <= player.getLocation().getBlockZ()) {
                        player.teleport(player.getLocation().add(0, 0, 2));
                    }
                    else if (isPlayerIn(player, region)) {
                        player.teleport(player.getLocation().set(0, 60, 0));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (isBlockIn(e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (isEntityIn(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker) {
            if (e.getEntity() instanceof Player victim) {
                if (isPlayerIn(attacker) || isPlayerIn(victim)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();
        List<EntityType> types = new ArrayList<>();

        types.add(EntityType.HORSE);
        types.add(EntityType.DONKEY);
        types.add(EntityType.COW);
        types.add(EntityType.MUSHROOM_COW);
        types.add(EntityType.SHEEP);
        types.add(EntityType.PIG);
        types.add(EntityType.CHICKEN);
        types.add(EntityType.WOLF);
        types.add(EntityType.OCELOT);
        types.add(EntityType.RABBIT);

        if (types.contains(entity.getType())) {
            if (isPlayerIn(player) || isEntityIn(entity)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGen(EntitySpawnEvent e) {
        if (isEntityIn(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    public static void initialize() {
        /*
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Region region : RegionUtil.regionSet) {
                    if (playerMap.containsKey(player)) {
                        if (isPlayerIn(player, region)) {
                            if (!playerMap.get(player).equals(region)) {
                                player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&6&l" + region.getOwner() + "님의 지역"), "", 10, 70, 20);
                                playerMap.put(player, region);
                            }
                        }
                    } else {
                        if (isPlayerIn(player, region)) {
                            playerMap.put(player, region);
                        }
                    }
                }
            }
        }, 0, 2); */
    }

    public static boolean isEntityIn(Entity entity) {
        int[] pos = getPos(entity);
        for (Region region : RegionUtil.regionSet) {
            if (isEntityIn(entity, region)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEntityIn(Entity entity, Region region) {
        int[] pos = getPos(entity);
        return region.getX1() <= pos[0] && region.getX2() >= pos[0] && region.getZ1() <= pos[1] && region.getZ2() >= pos[1];
    }

    public static boolean isBlockIn(Block block) {
        int[] pos = getPos(block.getLocation());
        for (Region region : RegionUtil.regionSet) {
            if (isBlockIn(block, region)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockIn(Block block, Region region) {
        int[] pos = getPos(block.getLocation());
        return region.getX1() <= pos[0] && region.getX2() >= pos[0] && region.getZ1() <= pos[1] && region.getZ2() >= pos[1];
    }

    public static boolean isPlayerIn(Player player) {
        int[] pos = getPos(player);
        for (Region region : RegionUtil.regionSet) {
            if (isPlayerIn(player, region)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerIn(Player player, Region region) {
        int[] pos = getPos(player);
        return region.getX1() <= pos[0] && region.getX2() >= pos[0] && region.getZ1() <= pos[1] && region.getZ2() >= pos[1];
    }

    public static int[] getPos(Player player) {
        return new int[]{player.getLocation().getBlockX(), player.getLocation().getBlockZ()};
    }

    public static int[] getPos(Entity entity) {
        return new int[]{entity.getLocation().getBlockX(), entity.getLocation().getBlockZ()};
    }

    public static int[] getPos(Location location) {
        return new int[]{location.getBlockX(), location.getBlockZ()};
    }
}
