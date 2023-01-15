package win.oreo.region.region;

import win.oreo.region.region.permission.RegionPermission;

import java.util.UUID;

public class Region {
    private final UUID id;
    private final int x1;
    private final int x2;
    private final int z1;
    private final int z2;
    private final String owner;
    private RegionPermission regionPermission;

    public Region(UUID id, int x1, int x2, int z1, int z2, String owner, RegionPermission regionPermission) {
        this.id = id;
        this.x1 = x1;
        this.x2 = x2;
        this.z1 = z1;
        this.z2 = z2;
        this.owner = owner;
        this.regionPermission = regionPermission;
    }

    public UUID getId() {
        return id;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getZ1() {
        return z1;
    }

    public int getZ2() {
        return z2;
    }

    public String getOwner() {
        return owner;
    }

    public RegionPermission getRegionPermission() {
        return regionPermission;
    }

    public void setRegionPermission(RegionPermission regionPermission) {
        this.regionPermission = regionPermission;
    }
}
