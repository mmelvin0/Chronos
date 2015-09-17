package chronos;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class ClientProxy extends Proxy {

    @Override
    void sync(SyncMessage message) {
        World world = FMLClientHandler.instance().getClient().theWorld;
        int dimension = world.provider.dimensionId;
        if (dimension == message.dimension) {
            WorldHandler.setScale(world, message.scale);
        } else {
            FMLLog.log(Info.MODID, Level.ERROR, String.format("Dimension mismatch during sync (client: %d, server: %d)", dimension, message.dimension));
        }
    }

}
