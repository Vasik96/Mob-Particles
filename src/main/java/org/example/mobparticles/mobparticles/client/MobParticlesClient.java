package org.example.mobparticles.mobparticles.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.Map;

public class MobParticlesClient implements ClientModInitializer {
    private final Map<Entity, Boolean> particlesSpawned = new HashMap<>();

    @Override
    public void onInitializeClient() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient) {
                return ActionResult.PASS;
            }

            // Spawn particles when the player hits an entity, without any cooldown
            spawnParticles(entity);

            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                for (Entity entity : client.world.getEntities()) {
                    if (entity instanceof LivingEntity livingEntity) {
                        if (livingEntity.hurtTime > 0 && !particlesSpawned.getOrDefault(entity, false)) {
                            // Spawn particles for any hurt entity
                            spawnParticles(entity);
                            particlesSpawned.put(entity, true);
                        } else if (livingEntity.hurtTime == 0) {
                            particlesSpawned.put(entity, false);
                        }
                    }
                }
            }
        });
    }

    private void spawnParticles(Entity entity) {
        World world = entity.getEntityWorld();
        Box box = entity.getBoundingBox();
        double x = box.minX + (box.maxX - box.minX) / 2;
        double y = box.minY + (box.maxY - box.minY) / 2;
        double z = box.minZ + (box.maxZ - box.minZ) / 2;
        for (int i = 0; i < 28; i++) {
            double particleX = x + (Math.random() - 0.5) * 0.5; // Random value between -0.25 and 0.25
            double particleY = y + (Math.random() - 0.5) * 0.5 + 0.35; // Random value between -0.25 and 0.25
            double particleZ = z + (Math.random() - 0.5) * 0.5; // Random value between -0.25 and 0.25
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState()), particleX, particleY, particleZ, 0, 0, 0);
        }
    }
}
