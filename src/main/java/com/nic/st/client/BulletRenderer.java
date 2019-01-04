package com.nic.st.client;

import com.nic.st.entity.EntityBullet;
import com.nic.st.util.ClientUtils;
import lucraft.mods.lucraftcore.util.helper.LCRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

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
		EntityPlayer player = Minecraft.getMinecraft().player;
		double pX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
		double pY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
		double pZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

		AxisAlignedBB voxel = new AxisAlignedBB(-0.025, -0.025, -0.025, 0.025, 0.025, 0.025);

		LCRenderHelper.setLightmapTextureCoords(240, 240);
		GlStateManager.pushMatrix();
		GlStateManager.translate(-player.posX, -player.posY, -player.posZ);
		GlStateManager.translate(pX, pY, pZ);
		GlStateManager.rotate(entity.rotationYaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(entity.rotationPitch, -1.0f, 0.0f, 0.0f);
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();


//		for(float dZ = 0.0f; dZ > -5f; dZ--)
//		{

		float startingAngle = ((entity.ticksExisted + partialTicks)*30) % 360;

		for(int i = 1; i > -1; i--)
		{
			float dZ = 0f;
			for (int angle = (int) startingAngle + 180*i; angle > startingAngle + 180*i - 720; angle -= 30)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0f, 0.0f, dZ);
				GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
				GlStateManager.translate(0.0f, -dZ*0.1, 0.0f);
				GlStateManager.rotate(angle, 0.0f, 0.0f, -1.0f);
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				ClientUtils.addTexturedBoxVertices(bufferbuilder, voxel, 0.8f, 0.6f*i, 0.0f, 0.5f);
				tessellator.draw();
				GlStateManager.popMatrix();
				dZ -= 0.05f;
			}
		}

//		}
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
		LCRenderHelper.restoreLightmapTextureCoords();

//		Particle p = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), pX, pY, pZ, 0, 0.0, 0);
//		if (p != null)
//		{
//			p.setRBGColorF(1.0f, 0.0f, 0.0f);
//			p.multipleParticleScaleBy(0.2f);
//		}
//
//		Random r = new Random();
//		for (int i = 0; i < 4; i++)
//		{
//			p = Minecraft.getMinecraft().effectRenderer
//					.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), pX + r.nextGaussian() * 0.025, pY + r.nextGaussian() * 0.025,
//							pZ + r.nextGaussian() * 0.025, 0, 0.0, 0);
//			if (p != null)
//			{
//				p.setRBGColorF(1.0f, r.nextFloat() * 0.5f + 0.3f, 0.0f);
//				p.multipleParticleScaleBy(0.2f);
//			}
//		}


		//Star Wars stun-ish
//		for(float dZ = 0.0f; dZ > -5f; dZ--)
//		{
//			for (int angle = 0; angle < 360; angle += 15)
//			{
//				GlStateManager.pushMatrix();
//				GlStateManager.translate(0.0f, 0.0f, dZ);
//				GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
//				float radius = (dZ % -2f == 0) ? 4 : 2;
//				GlStateManager.translate(0.0f, radius*0.2, 0.0f);
//				GlStateManager.rotate(angle, 0.0f, 0.0f, -1.0f);
//				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//				ClientUtils.addTexturedBoxVertices(bufferbuilder, voxel, 0.67f, 0.84f, 0.9f, 1.0f);
//				tessellator.draw();
//				GlStateManager.popMatrix();
//			}
//		}
	}

	public static class Overlay extends Particle
	{
		public Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_)
		{
			super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
			this.particleMaxAge = 4;
		}

		/**
		 * Renders the particle
		 */
		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
		{
			float f4 = 1F * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0F) * 0.25F * (float)Math.PI);
			this.setAlphaF(0.6F - ((float)this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F);
			float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
			float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
			float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
			int i = this.getBrightnessForRender(partialTicks);
			int j = i >> 16 & 65535;
			int k = i & 65535;
			buffer.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex(0.5D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex(0.5D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex(0.25D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex(0.25D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		}
	}

}


