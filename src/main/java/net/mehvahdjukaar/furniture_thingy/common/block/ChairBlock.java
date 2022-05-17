package net.mehvahdjukaar.furniture_thingy.common.block;

import net.mehvahdjukaar.furniture_thingy.common.entity.ChairEntity;
import net.mehvahdjukaar.selene.blocks.WaterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class ChairBlock extends WaterBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected final float chairHeight;

    public ChairBlock(Properties properties, float chairHeight) {
        super(properties);
        this.chairHeight = (chairHeight - 13) / 16f;
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockState(pos.above()).isAir() && player.getVehicle() == null) {

            if (!worldIn.isClientSide) {
                ChairEntity entity;
                var chairs = worldIn.getEntitiesOfClass(ChairEntity.class, new AABB(pos));
                if (chairs.isEmpty()) {
                    entity = new ChairEntity(worldIn, pos, state);
                    worldIn.addFreshEntity(entity);
                } else {
                    entity = chairs.get(0);
                }
                if (entity.getPassengers().size() < 1) {
                    player.startRiding(entity);
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.FAIL;
            } else return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    public float getChairHeight(BlockState state) {
        return chairHeight;
    }

    public float getChairRot(BlockState state) {
        return state.getValue(ChairBlock.FACING).toYRot();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }


}

