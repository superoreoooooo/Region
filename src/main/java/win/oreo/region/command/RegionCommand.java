package win.oreo.region.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.Main;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;

import java.util.*;

public class RegionCommand implements CommandExecutor {
    private RegionUtil regionUtil;

    public static Set<Player> editorSet = new HashSet<>();

    public RegionCommand() {
        this.regionUtil = new RegionUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "now" -> {
                        player.sendMessage("regions count : " + RegionUtil.playerCountMap.get(player));
                    }
                    case "buy" -> {
                        if (args.length == 2) {
                            RegionUtil.playerCountMap.putIfAbsent(player, 0);
                            RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) + Integer.parseInt(args[1]));
                            player.sendMessage("buy complete!");
                        }
                    }
                    case "sell" -> {
                        if (args.length == 2) {
                            if (RegionUtil.playerCountMap.get(player) >= Integer.parseInt(args[1])) {
                                RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) - (Integer.parseInt(args[1])));
                                player.sendMessage("sell complete!");
                            } else player.sendMessage("no more asset!");
                        }
                    }
                    case "set" -> {
                        editorSet.add(player);
                        player.sendMessage("edit mode on!");
                    }
                    case "remove" -> {
                        Set<Region> set = RegionUtil.getPlayerRegions(player);
                        for (Region region : set) {
                            RegionUtil.removeRegion(player, region);
                        }
                        player.sendMessage("remove complete!");
                    }
                    case "show" -> {
                        List<FallingBlock> fbs = new ArrayList<>();
                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                            int x1 = region.getX1();
                            int x2 = region.getX2();
                            int z1 = region.getZ1();
                            int z2 = region.getZ2();
                            for (int x = x1; x <= x2; x++) {
                                for (int z = z1; z <= z2; z++) {
                                    for (int y = 56; y <= 60; y++) {
                                        Location loc = new Location(player.getWorld(), x, y, z).add(0.5, 0, 0.5);
                                        fbs.add(spawnBlock(player.getWorld(), loc, region.getId()));
                                    }
                                }
                            }
                        }
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                            for (FallingBlock fb : fbs) {
                                fb.remove();
                            }
                        }, 100);
                    }
                    default -> {
                        player.sendMessage("error");
                    }
                }
            }
        }
        return false;
    }

    public FallingBlock spawnBlock(World world, Location location, UUID id) {
        MaterialData data = new MaterialData(Material.DIAMOND_BLOCK);
        FallingBlock block = world.spawnFallingBlock(location, data);
        block.setGravity(false);
        block.setMetadata("show", new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), id));
        block.setHurtEntities(false);
        block.setDropItem(false);
        return block;
    }
}
