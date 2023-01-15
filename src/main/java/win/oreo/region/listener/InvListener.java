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
                                                switch (item.getItemStack().getType()) {
                                                    case DIAMOND_PICKAXE -> {
                                                        boolean bool = false;
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            bool = !region.getRegionPermission().isAccess();
                                                            region.getRegionPermission().setAccess(!region.getRegionPermission().isAccess());
                                                        }
                                                        player.sendMessage("region permission:access set to " + bool);
                                                    }
                                                    case TNT -> {
                                                        boolean bool = false;
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            bool = !region.getRegionPermission().isExplode();
                                                            region.getRegionPermission().setExplode(!region.getRegionPermission().isExplode());
                                                        }
                                                        player.sendMessage("region permission:explode set to " + bool);
                                                    }
                                                    case DIAMOND_SWORD -> {
                                                        boolean bool = false;
                                                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                                                            bool = !region.getRegionPermission().isExplode();
                                                            region.getRegionPermission().setPvp(!region.getRegionPermission().isPvp());
                                                        }
                                                        player.sendMessage("region permission:pvp set to "  + bool);
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
