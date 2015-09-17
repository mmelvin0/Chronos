package chronos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Info.MODID, name = Info.MODID, version = Info.VERSION)
public class Chronos {

    @SidedProxy(clientSide = "chronos.ClientProxy", serverSide = "chronos.Proxy")
    static public Proxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onPreInit(FMLPreInitializationEvent event) {
        NetworkHandler.getInstance().register();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onInit(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(PlayerHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(WorldHandler.getInstance());
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new TimescaleCommand());
    }

}
