package win.oreo.region.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.inventory.Inventory.oreoInventory;
import win.oreo.inventory.Inventory.oreoItem;
import win.oreo.inventory.listener.oreoInventoryClickEvent;
import win.oreo.region.Main;
import win.oreo.region.region.permission.RegionPermission;
import win.oreo.region.region.permission.RegionPermissionUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.EventPriority.LOWEST;

public class InventoryListener implements Listener {

    List<Player> coolDown = new ArrayList<>();

    @EventHandler (priority = LOWEST, ignoreCancelled = true)
    public void onClickItem(oreoInventoryClickEvent e) {
        oreoInventory inventory = e.getInventory();
        Player player = e.getPlayer();
        oreoItem item = e.getClickedItem();

        if (coolDown.contains(player)) return;
        delay(player);

        if (item == null) return;

        switch (item.getItemType()) {
            case BUTTON -> {
                switch (item.getAction()) {
                    case CLOSE -> player.closeInventory();
                    case VOID -> {
                        switch (inventory.getInventoryName()) {
                            case "권한설정" -> {
                                permission(player, item);
                            }
                            case "테스트" -> player.sendMessage("테스트");
                        }
                    }
                }
            }
        }
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 5);
    }

    /**
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
                                        switch (inventory.getInventoryName()){
                                            case "권한설정" -> {
                                                permission(player, item);
                                            }
                                            case "테스트" -> {
                                                player.sendMessage("test!");
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
    **/

    public void permission(Player player, oreoItem item) {
        boolean bool;
        RegionPermission regionPermission = RegionPermissionUtil.getRegionPermission(player);
        if (regionPermission == null) return;
        switch (item.getItemStack().getType()) {
            case DIAMOND_PICKAXE -> {
                bool = !regionPermission.isAccess();
                RegionPermissionUtil.getRegionPermission(player).setAccess(bool);
                player.sendMessage("모든 플레이어의 지역 출입을 " + bool + "로 변경되었습니다.");
            }
            case TNT -> {
                bool = !regionPermission.isExplode();
                RegionPermissionUtil.getRegionPermission(player).setExplode(bool);
                player.sendMessage("폭발 방지가 " + bool + "로 변경되었습니다.");
            }
            case DIAMOND_SWORD -> {
                bool = !regionPermission.isPvp();
                RegionPermissionUtil.getRegionPermission(player).setPvp(bool);
                player.sendMessage("PVP가 " + bool + "로 변경되었습니다");
            }
        }
    }
}
