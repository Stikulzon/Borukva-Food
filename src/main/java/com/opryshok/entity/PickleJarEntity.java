package com.opryshok.entity;

import com.opryshok.item.ModItems;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class PickleJarEntity extends ProjectileEntity implements PolymerEntity {
    public ItemStack itemStack = ModItems.PICKLE_JAR.getDefaultStack();
    public PickleJarEntity(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ITEM_DISPLAY;
    }

    public void tick() {
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        super.tick();

        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));

        if (this.isTouchingWater()) {
            this.setVelocity(this.getVelocity().multiply(0.8));
        }
        if (this.isOnGround()){
            Break();
        }
        if (this.isOnFire()) {
            this.discard();
        }
        List<Entity> entities = this.getEntityWorld().getOtherEntities(this, this.getBoundingBox().expand(0.5), entity -> entity instanceof MobEntity);
        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                int damage = entity instanceof PhantomEntity ? 100 : 0;
                entity.damage(serverWorld, this.getDamageSources().thrown(this, this.getOwner()), damage);
            }
            Break();
        }
    }
    public void Break(){
        var pos = this.getBlockPos();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        this.getEntityWorld().playSound(null, pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL);
        ItemScatterer.spawn(this.getEntityWorld(), x, y, z, new ItemStack(ModItems.PICKLE, 3));
        this.discard();
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }
    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.SCALE, new Vector3f(0.6f)));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.BILLBOARD, (byte) DisplayEntity.BillboardMode.CENTER.ordinal()));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM, this.itemStack));
            //data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM_DISPLAY, ModelTransformation.NONE.fixed().hashCode()));
        }
    }
    public static void spawn(Vec3d vector, Position pos, World world, ItemStack stack, PlayerEntity owner) {
        var entity = create(vector, pos, world, stack);
        entity.setOwner(owner);
        world.spawnEntity(entity);
    }

    public static PickleJarEntity create(Vec3d vector, Position pos, World world, ItemStack stack) {
        var entity = new PickleJarEntity(ModEntities.CUCUMBER_JAR, world);
        entity.itemStack = stack;
        entity.setPosition(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
        entity.setVelocity(vector);
        return entity;
    }
    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(this, entityTrackerEntry);
    }
}
