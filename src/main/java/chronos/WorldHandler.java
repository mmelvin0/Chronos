package chronos;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class WorldHandler {

    static private WorldHandler instance = null;

    static private Map<World, IWorldData> worlds = new HashMap<World, IWorldData>();

    static public WorldHandler getInstance() {
        if (instance == null) {
            instance = new WorldHandler();
        }
        return instance;
    }

    static public double getScale(World world) {
        return worlds.get(world).getScale();
    }

    static public void setScale(World world, double scale) {
        worlds.get(world).setScale(scale);
        FMLLog.log(Info.MODID, Level.INFO, String.format("Set %s time scale to %s", world.provider.getDimensionName(), scale));
    }

    static public long tick(World world) {
        IWorldData wd = worlds.get(world);
        long currentTime = world.getWorldTime();
        double newTime = wd.getTime();
        if (currentTime != (long)newTime) {
            newTime = (double)currentTime;
        }
        newTime += wd.getScale();
        wd.setTime(newTime);
        return (long)newTime;
    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        World world = event.world;
        worlds.put(world, world.isRemote ? WorldDataClient.get(world) : WorldDataServer.get(world));
    }

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        worlds.remove(event.world);
    }

}
