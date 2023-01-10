package win.oreo.region.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.Main;
import win.oreo.region.command.RegionCommand;
import win.oreo.region.command.RegionCompleter;
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
                player.sendMessage("지역을 구매했습니다.");
            } else {
                player.sendMessage("잔액이 부족합니다.");
            }
        } else {
            player.sendMessage("지역이 중복됩니다.");
        }
        map.remove(player);
        RegionCommand.editorSet.remove(player);
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 10);
    }
}
