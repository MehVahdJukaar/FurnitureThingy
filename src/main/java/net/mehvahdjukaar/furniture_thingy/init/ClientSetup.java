package net.mehvahdjukaar.furniture_thingy.init;

import net.mehvahdjukaar.furniture_thingy.Furniture;
import net.mehvahdjukaar.furniture_thingy.client.renderers.ChairEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Furniture.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.CHAIR_ENTITY.get(), ChairEntityRenderer::new);

    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {

        // ItemBlockRenderTypes.setRenderLayer(ModRegistry.BLACK_CHAIR.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {

    }

}
