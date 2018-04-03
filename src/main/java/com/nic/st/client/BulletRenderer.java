package com.nic.st.client;

import com.nic.st.entity.EntityBullet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.util.EnumParticleTypes;

import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class BulletRenderer extends RenderSnowball<EntityBullet>
{

	public BulletRenderer(RenderManager manager)
	{
		super(manager, Items.AIR, Minecraft.getMinecraft().getRenderItem());
	}

	@Override public void doRender(EntityBullet entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		double pX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
		double pY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
		double pZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

		Particle p = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), pX, pY, pZ, 0, 0.1, 0);
		if (p != null)
		{
			p.setRBGColorF(1.0f, 0.0f, 0.0f);
			p.multipleParticleScaleBy(0.2f);
		}

		Random r = new Random();
		for (int i = 0; i < 4; i++)
		{
			p = Minecraft.getMinecraft().effectRenderer
					.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), pX + r.nextGaussian() * 0.025, pY + r.nextGaussian() * 0.025,
							pZ + r.nextGaussian() * 0.025, 0, 0.1, 0);
			if (p != null)
			{
				p.setRBGColorF(1.0f, r.nextFloat() * 0.5f + 0.3f, 0.0f);
				p.multipleParticleScaleBy(0.2f);
			}
		}
	}

}
