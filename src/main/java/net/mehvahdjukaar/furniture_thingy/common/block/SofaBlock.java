package net.mehvahdjukaar.furniture_thingy.common.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class SofaBlock extends ChairBlock {

    public static final EnumProperty<Attachment> RIGHT = EnumProperty.create("right", Attachment.class);
    public static final EnumProperty<Attachment> LEFT = EnumProperty.create("left", Attachment.class);

    public final ImmutableMap<BlockState, VoxelShape> SHAPES;

    VoxelShape s = Block.box(1, 1, 1, 15, 15, 15);

    public SofaBlock(Properties properties) {
        super(properties, 7);
        this.registerDefaultState(this.defaultBlockState().setValue(RIGHT, Attachment.ARM_REST).setValue(LEFT, Attachment.ARM_REST));
        SHAPES = null;//Map.of();
    }

    @Override
    public float getChairRot(BlockState state) {
        float r = state.getValue(ChairBlock.FACING).toYRot();
        if (state.getValue(LEFT) == Attachment.CORNER) r += 45;
        if (state.getValue(RIGHT) == Attachment.CORNER) r -= 45;
        return r;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return s;
    }

    // @Override
    // public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
    //     return SHAPES.get(state);
    // }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos.MutableBlockPos neighborPos = context.getClickedPos().mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            neighborPos.setWithOffset(pos, direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            state = this.updateShape(state, direction, neighbor, level, pos, neighborPos);
        }
        return state;
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = direction.getAxis();
        if (axis == Direction.Axis.Y) return state;
        Direction myDir = state.getValue(FACING);
        if (direction == myDir) {
            if (neighbor.getBlock() instanceof SofaBlock) {
                Direction facingDir = neighbor.getValue(FACING);
                if (myDir.getAxis() == facingDir.getClockWise().getAxis()) {
                    boolean r = myDir.getCounterClockWise() == facingDir;
                    var side = getSideProp(r);
                    state = state.setValue(side, Attachment.CORNER);
                    var opposite = getSideProp(!r);
                    if (state.getValue(opposite) == Attachment.ARM_REST)
                        state = state.setValue(opposite, Attachment.NONE);
                }
            } else {
                boolean needsUpdate = false;
                if (state.getValue(LEFT) == Attachment.CORNER) {
                    state = state.setValue(RIGHT, Attachment.ARM_REST);
                    state = state.setValue(LEFT, Attachment.ARM_REST);
                    needsUpdate = true;
                }
                if (state.getValue(RIGHT) == Attachment.CORNER) {
                    state = state.setValue(RIGHT, Attachment.NONE);
                    state = state.setValue(LEFT, Attachment.ARM_REST);
                    needsUpdate = true;
                }
                if (needsUpdate) {
                    Direction d = direction.getCounterClockWise();
                    BlockPos p = pos.relative(d);
                    state = this.updateShape(state, d, level.getBlockState(p), level, pos, p);
                    d = direction.getClockWise();
                    p = pos.relative(d);
                    state = this.updateShape(state, d, level.getBlockState(p), level, pos, p);
                }
                return state;
            }
        } else if (axis == myDir.getClockWise().getAxis()) {
            var side = getSideProp(myDir.getClockWise() == direction);
            //corner cant connect
            if (state.getValue(side) != Attachment.CORNER) {
                if (neighbor.getBlock() instanceof SofaBlock) {
                    Direction facingDir = state.getValue(FACING);
                    if (facingDir == myDir || facingDir == direction.getOpposite()) {
                        return state.setValue(side, Attachment.NONE);
                    }
                } else if (canHaveArmRest(state,side)) {
                    return state.setValue(side, Attachment.ARM_REST);
                }
            }
        }
        return state;
    }

    private boolean canHaveArmRest(BlockState state, EnumProperty<Attachment> side){
        return state.getValue(getOpposite(side)) != Attachment.CORNER;
    }

    private EnumProperty<Attachment> getOpposite(EnumProperty<Attachment> side) {
        return side == LEFT ? RIGHT : LEFT;
    }

    public EnumProperty<Attachment> getSideProp(boolean right) {
        return right ? RIGHT : LEFT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RIGHT, LEFT);
    }

    public enum Attachment implements StringRepresentable {

        NONE("none"),
        ARM_REST("arm_rest"),
        CORNER("corner");

        private final String id;

        Attachment(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}