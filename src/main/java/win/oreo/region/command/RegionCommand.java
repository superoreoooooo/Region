package win.oreo.region.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.region.region.Region;
import win.oreo.region.region.RegionUtil;

import java.util.HashSet;
import java.util.Set;

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
                        player.sendMessage("regions count : " + RegionUtil.getPlayerRegions(player).size());
                    }
                    case "buy" -> {
                        if (args.length == 2) {
                            RegionUtil.playerCountMap.put(player, Integer.parseInt(args[1]));
                            player.sendMessage("buy complete!");
                        }
                    }
                    case "sell" -> {
                        if (args.length == 2) {
                            RegionUtil.playerCountMap.put(player, RegionUtil.playerCountMap.get(player) - (Integer.parseInt(args[1])));
                            player.sendMessage("sell complete!");
                        }
                    }
                    case "set" -> {
                        editorSet.add(player);
                        player.sendMessage("edit mode on!");
                    }
                    case "remove" -> {
                        for (Region region : RegionUtil.getPlayerRegions(player)) {
                            RegionUtil.removeRegion(player, region);
                        }
                        player.sendMessage("remove complete!");
                    }
                    default -> {
                        player.sendMessage("error");
                    }
                }
            }
        }
        return false;
    }
}
