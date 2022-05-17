package net.mehvahdjukaar.furniture_thingy;


import net.mehvahdjukaar.furniture_thingy.init.ModRegistry;
import net.mehvahdjukaar.furniture_thingy.init.ModSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MehVahdJukaar
 */
@Mod(Furniture.MOD_ID)
public class Furniture {
    public static final String MOD_ID = "furniture_thingy";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public Furniture() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModSetup::init);

        MinecraftForge.EVENT_BUS.register(this);

        ModRegistry.init(bus);
    }

}
