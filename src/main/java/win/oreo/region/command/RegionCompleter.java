package win.oreo.region.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class RegionCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("보유");
            completions.add("구매");
            completions.add("판매");
            completions.add("설정");
            completions.add("설정해제");
            completions.add("확인");
            completions.add("권한추가");
            completions.add("권한해제");
            completions.add("권한확인");
            completions.add("권한설정");

            return completions;
        }
        return null;
    }
}
