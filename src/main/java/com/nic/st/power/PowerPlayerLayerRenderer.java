package com.nic.st.power;

import lucraft.mods.lucraftcore.util.helper.LCRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Nictogen on 4/25/18.
 */
public class PowerPlayerLayerRenderer implements LayerRenderer<EntityPlayer>
{
	private ModelPlayer model = new ModelPlayer(0.01f, false);
	private ModelPlayer biggerModel = new ModelPlayer(0.5f, false);
	private RenderPlayer renderPlayer;

	public PowerPlayerLayerRenderer(RenderPlayer renderPlayer)
	{
		this.renderPlayer = renderPlayer;
	}

	@Override
	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch, float scale)
	{
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		int progress = (entitylivingbaseIn.ticksExisted % 100) * 2;
		if (PowerClientRenderer.glowingTexture != null)
		{
			GlStateManager.pushMatrix();
			LCRenderHelper.setLightmapTextureCoords(240, 240);
			this.model.setModelAttributes(this.renderPlayer.getMainModel());
			this.model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			ResourceLocation texLoc = manager.getDynamicTextureLocation("glowing_skin", PowerClientRenderer.glowingTexture);
			this.renderPlayer.bindTexture(texLoc);
			this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			manager.deleteTexture(texLoc);
			LCRenderHelper.restoreLightmapTextureCoords();
			GlStateManager.popMatrix();
		}

		if (PowerClientRenderer.extendedTexture != null)
		{
			GlStateManager.pushMatrix();
			this.biggerModel.setModelAttributes(this.renderPlayer.getMainModel());
			this.biggerModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			ResourceLocation texLoc = manager.getDynamicTextureLocation("power_skin", PowerClientRenderer.extendedTexture);
			this.renderPlayer.bindTexture(texLoc);
			this.biggerModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			manager.deleteTexture(texLoc);
			GlStateManager.popMatrix();
		}

		//		Tessellator tessellator = Tessellator.getInstance();
		//		BufferBuilder vertexbuffer = tessellator.getBuffer();
		//		RenderHelper.disableStandardItemLighting();
		//		float f = ((float) progress + partialTicks) / 200.0F;
		//		float f1 = f / 2F;
		////		if (f > 0.8F)
		////		{
		////			f1 = (f - 0.8F) / 0.2F;
		////		}
		//
		//		Random random = new Random(432L);
		//		GlStateManager.disableTexture2D();
		//		GlStateManager.shadeModel(7425);
		//		GlStateManager.enableBlend();
		//		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		//		GlStateManager.disableAlpha();
		//		GlStateManager.enableCull();
		//		GlStateManager.depthMask(false);
		//		GlStateManager.pushMatrix();
		//		int i = 0;
		//		GlStateManager.translate(0, 0.25, 0);
		//		GlStateManager.color(255.0F, 0.0F, 255.0F);
		//
		//		float scale1;
		//		while ((float) i < (f + f * f) / 2.0F * 60.0F)
		//		{
		//			float x = random.nextFloat()*0.2f;
		//			float y = random.nextFloat()*0.2f;
		//			float z = random.nextFloat()*0.2f;
		//			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
		//			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
		//			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
		//			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
		//			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
		////			GlStateManager.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
		////			scale1 = random.nextFloat() * 5.0F + 5.0F + f1 * 10.0F;
		////			float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
		//			scale1 = (random.nextFloat() + f1 * 2.0F)/5;
		//			float f3 = (random.nextFloat() + f1)/5;
		//			GlStateManager.translate(x, y, z);
		//			vertexbuffer.begin(6, DefaultVertexFormats.POSITION);
		//			vertexbuffer.pos(0.0D, 0.0D, 0.0D).endVertex();
		//			vertexbuffer.pos(-0.866D * (double) f3, (double) scale1, (double) (-0.5F * f3)).endVertex();
		//			vertexbuffer.pos(0.866D * (double) f3, (double) scale1, (double) (-0.5F * f3)).endVertex();
		//			vertexbuffer.pos(0.0D, (double) scale1, (double) (1.0F * f3)).endVertex();
		//			vertexbuffer.pos(-0.866D * (double) f3, (double) scale1, (double) (-0.5F * f3)).endVertex();
		//			tessellator.draw();
		//			GlStateManager.translate(-x, -y, -z);
		//			++i;
		//		}
		//
		//		GlStateManager.popMatrix();
		//		GlStateManager.disableCull();
		//		GlStateManager.shadeModel(7424);
		//		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//		GlStateManager.enableTexture2D();
		//		GlStateManager.enableAlpha();
		//		RenderHelper.enableStandardItemLighting();
		//		GlStateManager.pushMatrix();
		//		scale1 = (float) progress / (float) 30;
		//		GL11.glScalef(scale1, scale1, scale1);
		//		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		//		GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.5F);
		//		GlStateManager.enableAlpha();
		//		GL11.glCallList(ClientProxy.sphereIdOutside);
		//		GL11.glCallList(ClientProxy.sphereIdInside);
		//		GL11.glPopMatrix();
		//		GlStateManager.disableBlend();
		//		GlStateManager.disableAlpha();
		//		GlStateManager.depthMask(true);

	}

	@Override public boolean shouldCombineTextures()
	{
		return false;
	}
}
