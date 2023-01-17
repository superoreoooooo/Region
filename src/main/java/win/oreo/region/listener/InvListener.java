package win.oreo.region.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import win.oreo.inventory.Inventory.oreoInventory;
import win.oreo.inventory.Inventory.oreoItem;
import win.oreo.inventory.util.oreoInventoryUtil;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;
import win.oreo.region.region.permission.RegionPermission;
import win.oreo.region.region.permission.RegionPermissionUtil;

public class InvListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        for (oreoInventory inventory : oreoInventoryUtil.oreoInventories) {
            if (inventory.getInventory().equals(e.getClickedInventory())) {
                e.setCancelled(true);
                for (oreoItem item : inventory.getInventoryMap().values()) {
                    if (item.getItemStack().equals(e.getCurrentItem())) {
                        Player player = (Player) e.getWhoClicked();
                        switch (item.getItemType()) {
                            case DEAL -> {
                                player.sendMessage("deal");
                            }
                            case BUTTON -> {
                                switch (item.getAction()) {
                                    case CLOSE -> e.getWhoClicked().closeInventory();
                                    case VOID -> {
                                        switch (e.getClickedInventory().getName()) {
                                            case "권한설정" -> {
                                                boolean bool;
                                                RegionPermission regionPermission = RegionPermissionUtil.getRegionPermission(player);
                                                if (regionPermission == null) return;
                                                switch (item.getItemStack().getType()) {
                                                    case DIAMOND_PICKAXE -> {
                                                        bool = !regionPermission.isAccess();
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            region.getRegionPermission().setAccess(bool);
                                                        }
                                                        player.sendMessage("region permission:access set to " + bool);
                                                    }
                                                    case TNT -> {
                                                        bool = !regionPermission.isExplode();
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            region.getRegionPermission().setExplode(bool);
                                                        }
                                                        player.sendMessage("region permission:explode set to " + bool);
                                                    }
                                                    case DIAMOND_SWORD -> {
                                                        bool = !regionPermission.isPvp();
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            region.getRegionPermission().setPvp(bool);
                                                        }
                                                        player.sendMessage("region permission:access set to " + bool);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (inventory.getInventory().equals(e.getInventory())) {
                e.setCancelled(true);
            }
        }
    }
}
