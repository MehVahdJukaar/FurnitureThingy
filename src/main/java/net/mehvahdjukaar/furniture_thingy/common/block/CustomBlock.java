package net.mehvahdjukaar.furniture_thingy.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CustomBlock extends Block {

    private final VoxelShape shape;

    public CustomBlock(Properties properties, int width, int height) {
        super(properties);
        int w2 = width/2;
        int h2 = height/2;
        shape = Block.box(0.5-w2,0,0.5-w2,0.5+w2,height,0.5+w2);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return shape;
    }
}
