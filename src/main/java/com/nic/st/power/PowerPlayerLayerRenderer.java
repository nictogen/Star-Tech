package com.nic.st.power;

import com.nic.st.util.ClientUtils;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.util.events.RenderModelEvent;
import lucraft.mods.lucraftcore.util.helper.LCRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * Created by Nictogen on 4/25/18.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
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
		renderCracking(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

			for (Ability ability : Ability.getAbilities(entitylivingbaseIn))
			{
				if(ability instanceof AbilityTendrils && ability.isEnabled())
					renderBeams(entitylivingbaseIn, (ability.context == Ability.EnumAbilityContext.OFF_HAND) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, renderPlayer, ability.getTicks());
			}

	}

	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event){
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) return;
		EntityPlayer player = Minecraft.getMinecraft().player;

		for (Ability ability : Ability.getAbilities(player))
			{
				if(ability instanceof AbilityTendrils && ability.isEnabled())
				{
					EnumHandSide side = (player.getPrimaryHand() == EnumHandSide.RIGHT && !(ability.context == Ability.EnumAbilityContext.OFF_HAND)) ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
					GlStateManager.pushMatrix();
					GlStateManager.translate(side == EnumHandSide.RIGHT ? 0.9 : -0.8, -0.9, -1);
					renderBeams(player, (ability.context == Ability.EnumAbilityContext.OFF_HAND) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, null, ability.getTicks());
					GlStateManager.popMatrix();
				}
			}

	}

	private void renderCracking(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch, float scale)
	{
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		if (PotionBurnout.ClientHandler.glowingTexture != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			this.model.setModelAttributes(this.renderPlayer.getMainModel());
			this.model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			LCRenderHelper.setLightmapTextureCoords(240, 240);
			ResourceLocation texLoc = manager.getDynamicTextureLocation("glowing_skin", PotionBurnout.ClientHandler.glowingTexture);
			this.renderPlayer.bindTexture(texLoc);
			this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			manager.deleteTexture(texLoc);
			LCRenderHelper.restoreLightmapTextureCoords();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}

		if (PotionBurnout.ClientHandler.extendedTexture != null)
		{
			GlStateManager.pushMatrix();
			this.biggerModel.setModelAttributes(this.renderPlayer.getMainModel());
			this.biggerModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			ResourceLocation texLoc = manager.getDynamicTextureLocation("power_skin", PotionBurnout.ClientHandler.extendedTexture);
			this.renderPlayer.bindTexture(texLoc);
			this.biggerModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			manager.deleteTexture(texLoc);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void onSetRotationAngles(RenderModelEvent.SetRotationAngels event)
	{
		if (event.type != RenderModelEvent.ModelSetRotationAnglesEventType.PRE || !(event.getEntity() instanceof AbstractClientPlayer))
			return;
		Render r = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(event.getEntity());
		if (!(r instanceof RenderLivingBase) || event.model != ((RenderLivingBase) r).getMainModel())
			return;

		if(PotionBurnout.ClientHandler.replacementTexture != null){
			ResourceLocation texLoc = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("replacement_skin", PotionBurnout.ClientHandler.replacementTexture);
			Minecraft.getMinecraft().renderEngine.bindTexture(texLoc);
		}
	}

	private static void renderBeams(EntityLivingBase entityLivingBase, EnumHand hand, @Nullable RenderPlayer renderPlayer, float progress)
	{
		EnumHandSide side = (entityLivingBase.getPrimaryHand() == EnumHandSide.RIGHT) ?
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);

		LCRenderHelper.setLightmapTextureCoords(240, 240);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		if(renderPlayer != null)
		renderPlayer.getMainModel().postRenderArm(0.0625F, side);

		GlStateManager.translate(-0.1, 0.5, 0.5);
		Random r = new Random(entityLivingBase.getEntityId());
		for (int n = 0; n < 8; n++)
		{
			GlStateManager.pushMatrix();
			float inc = r.nextFloat() / 20;
			float xAmp = r.nextInt(15) + 2;
			float xPer = xAmp * (r.nextInt(3) + 1);
			float yAmp = r.nextInt(15) + 2;
			float yPer = yAmp * (r.nextInt(3) + 1);
			GlStateManager.translate(xAmp * Math.sin(progress / xPer) * -0.0625, yAmp * Math.sin(progress / yPer) * -0.0625, 0);
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			for (int e = 0; e < 2; e++)
			{
				double s = e == 0 ? 0 + inc : 0.075F + inc;
				Color color = new Color(150, r.nextInt(50), 150);
				for (double i = 0; i < 60 && i < progress; i += 1 + e * 2)
				{
					Color c = e == 0 ? new Color(255, 255, 255) : new Color(150, color.getGreen(), 150);
					ClientUtils.addTexturedBoxVertices(bufferbuilder,
							voxel.offset(xAmp * Math.sin((i + progress) / xPer) * 0.0625,
									yAmp * Math.sin((i + progress) / yPer) * 0.0625,
									(10 + i) * -0.0625).grow(s),
							((float) c.getRed()) / 255f,
							((float) c.getGreen()) / 255f,
							((float) c.getBlue()) / 255f,
							e == 0 ? 1.0f : 0.15f);
				}
			}
			tessellator.draw();

			GlStateManager.popMatrix();
		}
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		LCRenderHelper.restoreLightmapTextureCoords();
	}

	@Override public boolean shouldCombineTextures()
	{
		return false;
	}
}
