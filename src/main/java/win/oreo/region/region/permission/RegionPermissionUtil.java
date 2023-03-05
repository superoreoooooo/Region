package win.oreo.region.region.permission;

import org.bukkit.OfflinePlayer;
import win.oreo.region.region.RegionUtil;

public class RegionPermissionUtil {
    public static RegionPermission getRegionPermission(OfflinePlayer player) {
        return RegionUtil.playerRegionPermissionHashMap.get(player);
    }
}
