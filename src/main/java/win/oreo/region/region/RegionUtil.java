package win.oreo.region.region;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.Main;

import java.util.*;

public class RegionUtil {
    private Main plugin;

    public RegionUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public static HashMap<OfflinePlayer, Set<Region>> playerRegionMap = new HashMap<>();
    public static Set<Region> regionSet = new HashSet<>();

    public void initialize() {
        for (String regionID : plugin.ymlManager.getConfig().getConfigurationSection("region.").getKeys(false)) {
            UUID id = UUID.fromString(regionID);
            int x1 = plugin.ymlManager.getConfig().getInt("region." + regionID + ".x1");
            int x2 = plugin.ymlManager.getConfig().getInt("region." + regionID + ".x2");
            int z1 = plugin.ymlManager.getConfig().getInt("region." + regionID + ".z1");
            int z2 = plugin.ymlManager.getConfig().getInt("region." + regionID + ".z2");
            String owner = plugin.ymlManager.getConfig().getString("region." + regionID + ".owner");
            Region region = new Region(id, x1, x2, z1, z2, owner);

            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);

            regionSet.add(region);

            if (playerRegionMap.containsKey(player)) {
                playerRegionMap.get(player).add(region);
            } else {
                Set<Region> regions = new HashSet<>();
                regions.add(region);
                playerRegionMap.put(player, regions);
            }
        }
    }

    public static void addRegion(Player player, Region region) {
        if (playerRegionMap.containsKey(player)) {
            playerRegionMap.get(player).add(region);
        } else {
            Set<Region> regions = new HashSet<>();
            regions.add(region);
            playerRegionMap.put(player, regions);
        }
    }

    public static Region getRegionByID(UUID regionID) {
        for (Region region : regionSet) {
            if (region.getId().equals(regionID)) return region;
        }
        return null;
    }

    public static Region createRegion(int x1, int x2, int z1, int z2, String owner) {
        UUID newID = UUID.randomUUID();
        Region region = new Region(newID, x1, x2, z1, z2, owner);
        regionSet.add(region);
        return region;
    }

    public static Region createRegion(int x1, int x2, int z1, int z2, Player player) {
        return createRegion(x1, x2, z1, z2, player.getName());
    }

    public static void removeRegion(OfflinePlayer player, Region region) {
        regionSet.remove(region);
        playerRegionMap.get(player).remove(region);
    }

    public static Set<Region> getPlayerRegions(OfflinePlayer player) {
        return playerRegionMap.get(player);
    }

    public static int getSize(Region region) {
        return ((Math.abs(region.getX1() - region.getX2()) + 1) * (Math.abs(region.getZ1() - region.getZ2()) + 1));
    }

    public static String getOwner(UUID id) {
        if (getRegionByID(id) != null) return getRegionByID(id).getOwner();
        else return null;
    }
}
