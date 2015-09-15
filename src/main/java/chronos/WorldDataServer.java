package chronos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class WorldDataServer extends WorldSavedData implements IWorldData {

    private double scale = 1;

    private double time = 0;

    static public WorldDataServer get(World world) {
        WorldSavedData wsd = world.perWorldStorage.loadData(WorldDataServer.class, Info.MODID);
        WorldDataServer wd;
        boolean create = wsd == null || !(wsd instanceof WorldDataServer);
        if (create) {
            wd = new WorldDataServer();
            world.perWorldStorage.setData(Info.MODID, wd);
        } else {
            wd = (WorldDataServer)wsd;
        }
        wd.time = (double)(world.getWorldTime());
        if (create) {
            wd.setDirty(true);
        }
        return wd;
    }

    public WorldDataServer() {
        super(Info.MODID);
    }

    public WorldDataServer(String id) {
        super(id);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double s) {
        if (s < 0) {
            s = 0;
        }
        if (s != scale) {
            scale = s;
            this.setDirty(true);
        }
    }

    public double getTime() {
        return time;
    }

    public void setTime(double t) {
        time = t;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.setScale(compound.getDouble("scale"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setDouble("scale", this.getScale());
    }

}
