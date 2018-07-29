package com.nic.st.client;

import com.nic.st.entity.EntityPowerRocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.util.EnumParticleTypes;

import java.util.Random;

/**
 * Created by Nictogen on 7/28/18.
 */
public class PowerRocketRenderer extends RenderSnowball<EntityPowerRocket>
{

	public PowerRocketRenderer(RenderManager manager)
	{
		super(manager, Items.AIR, Minecraft.getMinecraft().getRenderItem());
	}

	@Override public void doRender(EntityPowerRocket entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		double pX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
		double pY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
		double pZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

		Particle p = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), pX, pY, pZ, 0, 0.0, 0);
		if (p != null)
		{
			p.setRBGColorF(1.0f, 0.0f, 1.0f);
			p.multipleParticleScaleBy(0.2f);
			p.setMaxAge(5);
		}

		Random r = new Random();
		for (int i = 0; i < 4; i++)
		{
			p = Minecraft.getMinecraft().effectRenderer
					.spawnEffectParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), pX + r.nextGaussian() * 0.025, pY + r.nextGaussian() * 0.025,
							pZ + r.nextGaussian() * 0.025, 0, 0.0, 0);
			if (p != null)
			{
				p.multipleParticleScaleBy(0.2f);
			}
		}
	}

}
