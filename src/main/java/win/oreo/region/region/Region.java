package win.oreo.region.region;

import java.util.UUID;

public class Region {
    private UUID id;
    private int x1;
    private int x2;
    private int z1;
    private int z2;
    private String owner;

    public Region(UUID id, int x1, int x2, int z1, int z2, String owner) {
        this.id = id;
        this.x1 = x1;
        this.x2 = x2;
        this.z1 = z1;
        this.z2 = z2;
        this.owner = owner;
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
}
