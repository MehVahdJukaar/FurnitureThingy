package net.mehvahdjukaar.furniture_thingy.init;

import net.mehvahdjukaar.furniture_thingy.Furniture;
import net.mehvahdjukaar.furniture_thingy.common.block.CustomBlock;
import net.mehvahdjukaar.furniture_thingy.common.block.SofaBlock;
import net.mehvahdjukaar.furniture_thingy.common.entity.ChairEntity;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Furniture.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Furniture.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Furniture.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Furniture.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Furniture.MOD_ID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
    }

    private static RegistryObject<Item> regItem(String name, Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }

    protected static RegistryObject<Item> regBlockItem(RegistryObject<Block> blockSup) {
        return regItem(blockSup.getId().getPath(), () -> new BlockItem(blockSup.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_DECORATIONS)));
    }

    protected static RegistryObject<Block> regWithItem(String name, Supplier<Block> blockSup) {
        var b = BLOCKS.register(name, blockSup);
        regBlockItem(b);
        return b;
    }

    private static final Set<Supplier<Block>> TILE_BLOCKS = new HashSet<>();

    public static RegistryObject<Block> regTileBlock(String name, Supplier<Block> blockSup) {
        var b = regWithItem(name, blockSup);
        TILE_BLOCKS.add(b);
        return b;
    }

    private static Block[] getAllTileBlocks() {
        return TILE_BLOCKS.stream().map(Supplier::get).toArray(Block[]::new);
    }


    public static final RegistryObject<Block> WHITE_SOFA = regTileBlock("white_sofa", () ->
            new SofaBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.WOOL)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .strength(1)));


    public static final RegistryObject<EntityType<ChairEntity>> CHAIR_ENTITY = ENTITIES.register("sofa",
            () -> EntityType.Builder.<ChairEntity>of(ChairEntity::new, MobCategory.MISC)
                    .sized(0.375F, 0.5F)
                    .updateInterval(-1)
                    .clientTrackingRange(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("sofa"));

    //register a custom block. copy-paste this line changing its parameters
    public static final RegistryObject<Block> NEW_BLOCK = regWithItem("new_block_name",
            () -> new CustomBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.WOOL)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .strength(1), 6, 8));


}
