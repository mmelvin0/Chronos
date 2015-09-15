package chronos;

import net.minecraft.world.World;

public class WorldDataClient implements IWorldData {

    private double scale = 1;

    private double time = 0;

    static public WorldDataClient get(World world) {
        WorldDataClient data = new WorldDataClient();
        data.time = world.getWorldTime();
        return data;
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
        }
    }

    public double getTime() {
        return time;
    }

    public void setTime(double t) {
        time = t;
    }

}
