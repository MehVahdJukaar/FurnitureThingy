package net.mehvahdjukaar.furniture_thingy.common.entity;

import net.mehvahdjukaar.furniture_thingy.common.block.ChairBlock;
import net.mehvahdjukaar.furniture_thingy.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;

public class ChairEntity extends Entity implements IEntityAdditionalSpawnData {

    private float chairHeight;
    private BlockState state = Blocks.AIR.defaultBlockState();

    public ChairEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
        this.setInvisible(true);
        this.chairHeight = 0;
    }

    public ChairEntity(Level worldIn, BlockPos pos, BlockState state) {
        super(ModRegistry.CHAIR_ENTITY.get(), worldIn);
        this.noPhysics = true;
        this.setInvisible(true);
        this.setPos((double) pos.getX() + 0.5D, (double) pos.getY() + 0.6D, (double) pos.getZ() + 0.5D);
        this.state = state;
        if (state.getBlock() instanceof ChairBlock) {
            updateChair();
        }
    }

    private void updateChair(){
        ChairBlock chair = ((ChairBlock)state.getBlock());
        this.chairHeight = chair.getChairHeight(state);
        this.setRot(chair.getChairRot(state),0);
    }

    @Override
    public void tick() {

        super.tick();
        List<Entity> passengers = this.getPassengers();
        boolean dead = passengers.isEmpty();
        BlockPos pos = this.blockPosition();
        this.state = this.level.getBlockState(pos);

        if(!dead) {
            if (state.getBlock() instanceof ChairBlock) {
                updateChair();
            } else {
                PistonMovingBlockEntity piston = null;
                boolean didOffset = false;
                BlockEntity tile = this.level.getBlockEntity(pos);
                if (tile instanceof PistonMovingBlockEntity pis && pis.getMovedState().getBlock() instanceof ChairBlock) {
                    piston = pis;
                } else {
                    for (Direction d : Direction.values()) {
                        BlockPos offPos = pos.relative(d);
                        tile = this.level.getBlockEntity(offPos);
                        if (tile instanceof PistonMovingBlockEntity pi && pi.getMovedState().getBlock() instanceof ChairBlock) {
                            piston = pi;
                            break;
                        }
                    }
                }

                if (piston != null) {
                    Direction dir = piston.getMovementDirection();
                    this.move(MoverType.PISTON, new Vec3((double) ((float) dir.getStepX()) * 0.33D, (double) ((float) dir.getStepY()) * 0.33D, (double) ((float) dir.getStepZ()) * 0.33D));
                    didOffset = true;
                }

                dead = !didOffset;
            }
        }

        if (dead && !this.level.isClientSide) {
            this.discard();
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return chairHeight;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        for(Direction dir : Direction.orderedByNearest(this)) {
            Vec3 safeVec = DismountHelper.findSafeDismountLocation(entity.getType(), this.level, this.blockPosition().relative(dir), false);
            if(safeVec != null) {
                return safeVec.add(0, 0.25, 0);
            }
        }
        return super.getDismountLocationForPassenger(entity);
    }

    @Override
    protected void addPassenger(Entity entity) {
        super.addPassenger(entity);
        entity.setYRot(this.getYRot());
    }

    @Override
    public void positionRider(Entity entity) {
        super.positionRider(entity);
        this.clampYaw(entity);
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampYaw(entity);
    }

    private void clampYaw(Entity passenger) {
        passenger.setYBodyRot(this.getYRot());
        float wrappedYaw = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
        float clampedYaw = Mth.clamp(wrappedYaw, -120.0F, 120.0F);
        passenger.yRotO += clampedYaw - wrappedYaw;
        passenger.setYRot(passenger.getYRot() + clampedYaw - wrappedYaw);
        passenger.setYHeadRot(passenger.getYRot());
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() < 1;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.chairHeight = compound.getFloat("chairHeight");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("chairHeight", this.chairHeight);
    }

    @Nonnull
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.chairHeight);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.chairHeight = additionalData.readFloat();
    }
}
