package win.oreo.region.region.permission;

import org.bukkit.OfflinePlayer;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;

public class RegionPermissionUtil {
    public static RegionPermission getRegionPermission(OfflinePlayer player) {
        for (Region region : RegionUtil.getPlayerRegions(player)) {
            return region.getRegionPermission();
        }
        return null;
    }
}
