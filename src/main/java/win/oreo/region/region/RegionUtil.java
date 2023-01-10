package win.oreo.region.region;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.Main;
import win.oreo.region.util.Area;

import java.util.*;

public class RegionUtil {
    public static HashMap<OfflinePlayer, Set<Region>> playerRegionMap = new HashMap<>();
    public static Set<Region> regionSet = new HashSet<>();
    public static HashMap<OfflinePlayer, Integer> playerCountMap = new HashMap<>();

    /**
     * initializes region system.
     */
    public void initialize() {
        Main plugin = JavaPlugin.getPlugin(Main.class);
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
            addRegionToPlayer(player, region);
        }
        for (String name : plugin.ymlManager.getConfig().getConfigurationSection("player.").getKeys(false)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            int count = plugin.ymlManager.getConfig().getInt("player." + name + ".count");

            playerCountMap.put(player, count);
        }
    }

    public void save() {
        Main plugin = JavaPlugin.getPlugin(Main.class);
        for (Region region : regionSet) {
            plugin.ymlManager.getConfig().set("region." + region.getId().toString() + ".x1", region.getX1());
            plugin.ymlManager.getConfig().set("region." + region.getId().toString() + ".x2", region.getX2());
            plugin.ymlManager.getConfig().set("region." + region.getId().toString() + ".z1", region.getZ1());
            plugin.ymlManager.getConfig().set("region." + region.getId().toString() + ".z2", region.getZ2());
            plugin.ymlManager.getConfig().set("region." + region.getId().toString() + ".owner", region.getOwner());
        }
        for (OfflinePlayer offlinePlayer : playerCountMap.keySet()) {
            plugin.ymlManager.getConfig().set("player." + offlinePlayer.getName() + ".count", playerCountMap.get(offlinePlayer));
            Bukkit.getConsoleSender().sendMessage("player : " + offlinePlayer.getName() + " count : " + playerCountMap.get(offlinePlayer));
        }
        plugin.ymlManager.saveConfig();
        Bukkit.getConsoleSender().sendMessage("save complete!");
    }

    /**
     * adds region.
     * @param player player to add region
     * @param region region to add
     */
    public static void addRegionToPlayer(OfflinePlayer player, Region region) {
        if (playerRegionMap.containsKey(player)) {
            playerRegionMap.get(player).add(region);
        } else {
            Set<Region> regions = new HashSet<>();
            regions.add(region);
            playerRegionMap.put(player, regions);
        }
    }

    /**
     * gets region. (by UUID)
     * @param regionID a region's ID
     * @return region with ID
     */
    public static Region getRegionByID(UUID regionID) {
        for (Region region : regionSet) {
            if (region.getId().equals(regionID)) return region;
        }
        return null;
    }

    /**
     * creates region (String)
     * @param x1 1st position's BlockX
     * @param x2 2nd position's BlockX
     * @param z1 1st position's BlockZ
     * @param z2 2nd position's BlockZ
     * @param owner owner of the region (String)
     * @return
     */
    public static void createRegion(int x1, int x2, int z1, int z2, String owner) {
        UUID newID = UUID.randomUUID();
        int[] ints = setNormal(x1, x2, z1, z2);
        Region region = new Region(newID, ints[0], ints[1], ints[2], ints[3], owner);
        regionSet.add(region);
        addRegionToPlayer(Bukkit.getOfflinePlayer(owner), region);
    }

    /**
     * creates region (Player)
     * @param x1 1st position's BlockX
     * @param x2 2nd position's BlockX
     * @param z1 1st position's BlockZ
     * @param z2 2nd position's BlockZ
     * @param player owner of the region (Player)
     * @return region
     */
    public static void createRegion(int x1, int x2, int z1, int z2, Player player) {
        createRegion(x1, x2, z1, z2, player.getName());
    }

    /**
     *
     * @param area area
     * @param player owner of the region (Player)
     * @return region
     */
    public static void createRegion(Area area, Player player) {
        createRegion(area.getX1(), area.getX2(), area.getZ1(), area.getZ2(), player.getName());
    }

    /**
     * change positions to ascending order
     * @param x1 1st position's BlockX
     * @param x2 2nd position's BlockX
     * @param z1 1st position's BlockZ
     * @param z2 2nd position's BlockZ
     * @return arr of {x1, x2, z1, z2} !!(x1 < x2, z1 < z2)!!
     */
    public static int[] setNormal(int x1, int x2, int z1, int z2) {
        int[] ints;
        if (x1 > x2) {
            if (z1 > z2) {
                ints = new int[]{x2, x1, z2, z1};
            } else {
                ints = new int[]{x2, x1, z1, z2};
            }
        } else {
            if (z1 > z2) {
                ints = new int[]{x1, x2, z2, z1};
            } else {
                ints = new int[]{x1, x2, z1, z2};
            }
        }
        return ints;
    }

    /**
     * removes region
     * @param player player that owns the region
     * @param region region to remove
     */
    public static void removeRegion(OfflinePlayer player, Region region) {
        playerRegionMap.get(player).remove(region);
        regionSet.remove(region);
    }

    /**
     * removes region
     * @param region region to remove
     */
    public static void removeRegion(Region region) {
        removeRegion(Bukkit.getOfflinePlayer(getOwner(region.getId())), region);
    }

    /**
     * gets set of regions that owned by offlinePlayer
     * @param offlinePlayer offlinePlayer to get Regions set
     * @return set of regions that owned by offlinePlayer
     */
    public static Set<Region> getPlayerRegions(OfflinePlayer offlinePlayer) {
        return playerRegionMap.get(offlinePlayer);
    }

    /**
     * gets size of the region
     * @param region region to get size
     * @return size of region
     */
    public static int getSize(Region region) {
        return (region.getX2() - region.getX1() + 1) * (region.getZ2() - region.getZ1() + 1);
    }

    /**
     *
     * @param x1 x1
     * @param x2 x2
     * @param z1 z1
     * @param z2 z2
     * @return size of area
     */
    public static int getSize(int x1, int x2, int z1, int z2) {
        int[] ints = setNormal(x1, x2, z1, z2);
        return (ints[1] - ints[0] + 1) * (ints[3] - ints[2] + 1);
    }

    /**
     *
     * @param area area
     * @return size of area
     */
    public static int getSize(Area area) {
        int[] ints = setNormal(area.getX1(), area.getX2(), area.getZ1(), area.getZ2());
        return (ints[1] - ints[0] + 1) * (ints[3] - ints[2] + 1);
    }

    /**
     * gets owner of the region
     * @param id Region's Id
     * @return Region's owner
     */
    public static String getOwner(UUID id) {
        if (getRegionByID(id) != null) return getRegionByID(id).getOwner();
        else return null;
    }

    /**
     * check if regions overlap
     * @param x1,z1 1st position
     * @param x2,z2 2nd position
     * @return true : overlapping / false : no overlapping
     */
    public static boolean checkOverlapping(int x1, int x2, int z1, int z2) {
        int[] ints = setNormal(x1, x2, z1, z2);
        for (Region region : regionSet) {
            for (int cx = region.getX1(); cx <= region.getX2(); cx++) {
                for (int cz = region.getZ1(); cz <= region.getZ2(); cz++) {
                    if (((cx == ints[0]) && (cz == ints[2])) || ((cx == ints[1]) && (cz == ints[3]))) {
                        Bukkit.broadcastMessage("중복 : " + region.getId() + " cx " + cx + " cz " + cz);
                        return true;
                    }
                }
            }
            for (int x = ints[0]; x <= ints[1]; x++) {
                for (int z = ints[2]; z <= ints[3]; z++) {
                    if (((x == region.getX1()) && (z == region.getZ1())) || ((x == region.getX2()) && (z == region.getZ2()))) {
                        Bukkit.broadcastMessage("중복 : " + region.getId() + " x " + x + " z " + z);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param area area
     * @return true : no overlapping / false : overlapping
     */
    public static boolean checkOverlapping(Area area) {
        return checkOverlapping(area.getX1(), area.getX2(), area.getZ1(), area.getZ2());
    }
}
