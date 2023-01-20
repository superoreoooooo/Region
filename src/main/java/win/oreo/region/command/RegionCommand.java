package win.oreo.region.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.inventory.Inventory.Enums.ButtonAction;
import win.oreo.inventory.Inventory.Enums.ItemType;
import win.oreo.inventory.Inventory.oreoInventory;
import win.oreo.inventory.Inventory.oreoItem;
import win.oreo.inventory.util.oreoInv;
import win.oreo.region.Main;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;
import win.oreo.region.region.permission.RegionPermission;
import win.oreo.region.region.permission.RegionPermissionUtil;

import java.util.*;
import java.util.regex.Pattern;

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
                    case "now", "보유" -> {
                        player.sendMessage(Main.getConfigMessage("messages.regions.now") + RegionUtil.playerCountMap.get(player));
                    }
                    case "buy", "구매" -> {
                        if (args.length == 2) {
                            if (checkInt(args[1])) {
                                RegionUtil.playerCountMap.putIfAbsent(player, 0);
                                RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) + Integer.parseInt(args[1]));
                                player.sendMessage(Main.getConfigMessage("messages.buy.complete"));
                            } else {
                                player.sendMessage(Main.getConfigMessage("messages.error.no-integer"));
                            }
                        }
                    }
                    case "sell", "판매" -> {
                        if (args.length == 2) {
                            if (checkInt(args[1])) {
                                if (RegionUtil.playerCountMap.get(player) >= Integer.parseInt(args[1])) {
                                    RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) - (Integer.parseInt(args[1])));
                                    player.sendMessage(Main.getConfigMessage("messages.sell.complete"));
                                } else {
                                    player.sendMessage(Main.getConfigMessage("messages.error.no-region"));
                                }
                            } else {
                                player.sendMessage(Main.getConfigMessage("messages.error.no-integer"));
                            }
                        }
                    }
                    case "set", "설정" -> {
                        editorSet.add(player);
                        player.sendMessage(Main.getConfigMessage("messages.edit.mode"));
                    }
                    case "clear", "설정해제" -> {
                        RegionUtil.remove(player);
                        player.sendMessage(Main.getConfigMessage("messages.remove.complete"));
                    }
                    case "addpermission", "addperm", "권한추가" -> {
                        String[] strings = new String[1];
                        strings[0] = args[1];
                        if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                            for (Region region : RegionUtil.getPlayerRegions(player)) {
                                List<String> list = region.getRegionPermission().getAccessPlayers();
                                list.add(args[1]);
                                region.getRegionPermission().setAccessPlayers(list);
                            }
                            player.sendMessage(Main.getConfigMessage("messages.permission.add", strings));
                        }

                    }
                    case "removepermission", "removeperm", "권한해제" -> {
                        String[] strings = new String[1];
                        strings[0] = args[1];
                        boolean tf = false;
                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                            List<String> list = region.getRegionPermission().getAccessPlayers();
                            if (list.contains(args[1])) {
                                list.remove(args[1]);
                                region.getRegionPermission().setAccessPlayers(list);
                                tf = true;
                            }
                        }
                        if (tf) {
                            player.sendMessage(Main.getConfigMessage("messages.permission.remove.success", strings));
                        } else {
                            player.sendMessage(Main.getConfigMessage("messages.permission.remove.success", strings));
                        }
                    }
                    case "permission", "perm", "권한확인" -> {
                        RegionPermission permission = RegionPermissionUtil.getRegionPermission(player);
                        List<String> list = permission.getAccessPlayers();
                        for (String playerString : list) {
                            String[] strings = new String[1];
                            strings[0] = playerString;
                            player.sendMessage(Main.getConfigMessage("messages.permission.list", strings));
                        }
                    }
                    case "setpermission", "setperm", "권한설정" -> {
                        permission(player);
                    }
                    case "show", "확인" -> {
                        if (RegionUtil.getPlayerRegions(player) == null) return false;
                        player.sendMessage(Main.getConfigMessage("messages.show.region"));
                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                            show(player, region);
                        }
                    }
                    default -> {
                        player.sendMessage(Main.getConfigMessage("messages.error.wrong-command"));
                    }
                }
            }
        }
        return false;
    }

    public void permission(Player player) {
        oreoInv inv = new oreoInv();
        if (inv.get("권한설정").size() != 0) {
            inv.get("권한설정").forEach(oreoInventory -> player.openInventory(oreoInventory.getInventory()));
        } else {
            boolean access = false;
            boolean explode = false;
            boolean pvp = false;

            RegionPermission regionPermission = RegionPermissionUtil.getRegionPermission(player);

            if (regionPermission == null) return;
            if (regionPermission.isAccess()) access = true;
            if (regionPermission.isExplode()) explode = true;
            if (regionPermission.isPvp()) pvp = true;

            HashMap<Integer, oreoItem> map = new HashMap<>();

            ItemStack accessStack = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta playerPermMeta = accessStack.getItemMeta();
            playerPermMeta.setDisplayName(ChatColor.GOLD + "플레이어 출입");
            List<String> playerPermLore = new ArrayList<>();
            playerPermLore.add("플레이어 출입 : " + access);
            playerPermMeta.setLore(playerPermLore);
            accessStack.setItemMeta(playerPermMeta);

            ItemStack explodeStack = new ItemStack(Material.TNT);
            ItemMeta explodeMeta = explodeStack.getItemMeta();
            explodeMeta.setDisplayName(ChatColor.GOLD + "폭발 방지");
            List<String> explodeLore = new ArrayList<>();
            explodeLore.add("폭발 상태 : " + explode);
            explodeMeta.setLore(explodeLore);
            explodeStack.setItemMeta(explodeMeta);

            ItemStack pvpStack = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta pvpMeta = pvpStack.getItemMeta();
            pvpMeta.setDisplayName(ChatColor.GOLD + "PVP");
            List<String> pvpLore = new ArrayList<>();
            pvpLore.add("플레이어 출입 : " + pvp);
            pvpMeta.setLore(pvpLore);
            pvpStack.setItemMeta(pvpMeta);

            map.put(2, new oreoItem(accessStack, ItemType.BUTTON, 0, ButtonAction.VOID));
            map.put(4, new oreoItem(explodeStack, ItemType.BUTTON, 0, ButtonAction.VOID));
            map.put(6, new oreoItem(pvpStack, ItemType.BUTTON, 0, ButtonAction.VOID));

            oreoInventory inventory = inv.create("권한설정", 9, map);
            player.openInventory(inventory.getInventory());
        }
    }

    public Block spawnBlock(World world, Location location) {
        Block block = world.getBlockAt(location);
        if (block.getType().equals(Material.AIR)) {
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 3);
        }
        return block;
    }

    public static boolean checkInt(String arg) {
        String ck = "^[0-9]*$";
        return Pattern.matches(ck, arg);
    }

    public void show(Player player, Region region) {
        List<Block> blocks = new ArrayList<>();

        int x1 = region.getX1();
        int x2 = region.getX2();
        int z1 = region.getZ1();
        int z2 = region.getZ2();
        String[] strings1 = new String[3];
        String[] strings2 = new String[3];

        strings1[0] = String.valueOf(x1);
        strings1[1] = String.valueOf(z1);
        strings1[2] = region.getId().toString();

        strings2[0] = String.valueOf(x2);
        strings2[1] = String.valueOf(z2);
        strings2[2] = region.getId().toString();

        player.sendMessage(Main.getConfigMessage("messages.show.pos1", strings1));
        player.sendMessage(Main.getConfigMessage("messages.show.pos2", strings2));

        for (int x = x1 + 1; x < x2; x++) {
            int high = 0;
            for (int y = 0; y <= 256; y++) {
                Location loc = new Location(player.getWorld(), x, y, z1);
                if (!loc.getBlock().isEmpty()) high = y;
            }
            high++;
            Location location = new Location(player.getWorld(), x, high, z1);
            blocks.add(spawnBlock(player.getWorld(), location));
        }

        for (int x = x1; x < x2; x++) {
            int high = 0;
            for (int y = 0; y <= 256; y++) {
                Location loc = new Location(player.getWorld(), x, y, z2);
                if (!loc.getBlock().isEmpty()) high = y;
            }
            high++;
            Location location = new Location(player.getWorld(), x, high, z2);
            blocks.add(spawnBlock(player.getWorld(), location));
        }

        for (int z = z1; z < z2; z++) {
            int high = 0;
            for (int y = 0; y <= 256; y++) {
                Location loc = new Location(player.getWorld(), x1, y, z);
                if (!loc.getBlock().isEmpty()) high = y;
            }
            high++;
            Location location = new Location(player.getWorld(), x1, high, z);
            blocks.add(spawnBlock(player.getWorld(), location));
        }

        for (int z = z1; z <= z2; z++) {
            int high = 0;
            for (int y = 0; y <= 256; y++) {
                Location loc = new Location(player.getWorld(), x2, y, z);
                if (!loc.getBlock().isEmpty()) high = y;
            }
            high++;
            Location location = new Location(player.getWorld(), x2, high, z);
            blocks.add(spawnBlock(player.getWorld(), location));
        }

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
            for (Block block : blocks) {
                block.setType(Material.AIR);
            }
        }, 100);
    }
}
