package win.oreo.region.region.permission;

import java.util.List;

public class RegionPermission {
    private boolean access;
    private boolean explode;
    private boolean pvp;
    private List<String> accessPlayers;

    public RegionPermission(boolean access, boolean explode, boolean pvp, List<String> accessPlayers) {
        this.access = access;
        this.explode = explode;
        this.pvp = pvp;
        this.accessPlayers = accessPlayers;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public boolean isExplode() {
        return explode;
    }

    public void setExplode(boolean explode) {
        this.explode = explode;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public List<String> getAccessPlayers() {
        return accessPlayers;
    }

    public void setAccessPlayers(List<String> accessPlayers) {
        this.accessPlayers = accessPlayers;
    }
}
