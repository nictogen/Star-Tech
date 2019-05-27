package com.nic.st.abilities;

import com.nic.st.ClientProxy;
import com.nic.st.StarTech;
import com.nic.st.network.MessageMovePlayer;
import com.nic.st.util.ClientUtils;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityHeld;
import lucraft.mods.lucraftcore.superpowers.abilities.data.AbilityData;
import lucraft.mods.lucraftcore.superpowers.abilities.data.AbilityDataFloat;
import lucraft.mods.lucraftcore.superpowers.abilities.data.AbilityDataInteger;
import lucraft.mods.lucraftcore.superpowers.abilities.supplier.EnumSync;
import lucraft.mods.lucraftcore.superpowers.render.RenderSuperpowerLayerEvent;
import lucraft.mods.lucraftcore.util.helper.LCEntityHelper;
import lucraft.mods.lucraftcore.util.helper.LCRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by Nictogen on 1/24/19.
 */
public class AbilityConcussiveBlast extends AbilityHeld
{
	public static final AbilityData<Float> DAMAGE = new AbilityDataFloat("damage").disableSaving().setSyncType(EnumSync.SELF).enableSetting("damage", "The damage done by the attack");
	public static final AbilityData<Integer> DURATION = new AbilityDataInteger("duration").disableSaving().setSyncType(EnumSync.SELF).enableSetting("duration", "How long the blast is fired for.");

	public AbilityConcussiveBlast(EntityLivingBase player)
	{
		super(player);
	}

	@Override
	public void registerData()
	{
		super.registerData();
		this.dataManager.register(DAMAGE, 1F);
		this.dataManager.register(DURATION, 5);
	}

	@Override
	public void onUpdate() {
		if (isUnlocked()) {
			if (isEnabled()) {
				if (ticks == 0)
					firstTick();
				ticks++;
				updateTick();

			} else {
				if (ticks != 0) {
					lastTick();
					ticks = 0;
				}

				if (hasCooldown()) {
					if (getCooldown() > 0)
						this.setCooldown(getCooldown() - 1);
				}
			}
		} else if (ticks != 0) {
			lastTick();
			ticks = 0;
		}

		if (this.dataManager.sync != null) {
			this.sync = this.sync.add(this.dataManager.sync);
			this.dataManager.sync = EnumSync.NONE;
		}
	}

	@Override public void onKeyPressed()
	{
		if (!this.isCoolingdown())
			super.onKeyPressed();
	}

	@Override public void onKeyReleased()
	{
	}

	@Override public void updateTick()
	{
		for (Entity entity1 : entity.world
				.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(Math.min(ticks, 4)), input -> LCEntityHelper.isInFrontOfEntity(entity, input)))
		{
			float speed = 3;
			entity1.motionY += -Math.sin((double)(entity.rotationPitch * (float)Math.PI / 180.0F)) * speed;
			entity1.motionX += -Math.sin((double) (entity.rotationYaw * (float) Math.PI / 180.0F)) * speed;
			entity1.motionZ += Math.cos((double) (entity.rotationYaw * (float) Math.PI / 180.0F)) * speed;
			if(entity1 instanceof EntityPlayerMP){
				StarTech.simpleNetworkWrapper.sendTo(new MessageMovePlayer(-Math.sin((double) (entity.rotationYaw * (float) Math.PI / 180.0F)) * speed, -Math.sin((double)(entity.rotationPitch * (float)Math.PI / 180.0F)) * speed, Math.cos((double) (entity.rotationYaw * (float) Math.PI / 180.0F)) * speed), (EntityPlayerMP) entity1);
			}

			entity1.attackEntityFrom(DamageSource.causeMobDamage(entity), this.dataManager.get(DAMAGE));

		}
		if (this.ticks == this.dataManager.get(DURATION))
		{
			this.setEnabled(false);
			this.setCooldown(this.getMaxCooldown());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override public void drawIcon(Minecraft mc, Gui gui, int x, int y)
	{
		mc.renderEngine.bindTexture(ClientProxy.SUPERPOWER_ICONS);
		gui.drawTexturedModalRect(x, y, 5 * 16, 0, 16, 16);
	}

	@Mod.EventBusSubscriber(Side.CLIENT)
	public static class Renderer
	{
		@SubscribeEvent
		public static void onRenderWorldLast(RenderWorldLastEvent event)
		{
			if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)
				return;

			EntityPlayer player = Minecraft.getMinecraft().player;
			AxisAlignedBB voxel = new AxisAlignedBB(-0.025, -0.025, -0.025, 0.025, 0.025, 0.025);

			for (AbilityConcussiveBlast ab : Ability.getAbilitiesFromClass(Ability.getAbilities(player), AbilityConcussiveBlast.class))
			{
				if (ab != null && ab.isUnlocked() && ab.isEnabled())
				{
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

					GlStateManager.translate(0, player.getEyeHeight() + 0.25, 0);
					GlStateManager.rotate(-player.rotationYaw, 0, 1, 0);
					GlStateManager.rotate(player.rotationPitch, 1, 0, 0);
					GlStateManager.translate(ab.context == EnumAbilityContext.OFF_HAND ? 0.25 : -0.25, 0, 0.75);


					for (int i = 0; i < 4 && i < ab.ticks; i++)
					{
						float offset = ((float) i) + ((ab.ticks % 4 + Minecraft.getMinecraft().getRenderPartialTicks()) / 5f);
						//*0.5f
						for (int angle = 0; angle < 360; angle += 8)
						{
							GlStateManager.pushMatrix();
							GlStateManager.translate(0.0f, 0.0f, offset);
							GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
							float radius = 1 + offset * 1.25f;
							GlStateManager.translate(0.0f, radius * 0.2, 0.0f);
							GlStateManager.rotate(angle, 0.0f, 0.0f, -1.0f);
							bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
							ClientUtils.addTexturedBoxVertices(bufferbuilder, voxel, 1.0f, 1.0f, 1.0f, 0.5f);
							tessellator.draw();
							GlStateManager.popMatrix();
						}
					}

					GlStateManager.disableAlpha();
					GlStateManager.disableBlend();
					GlStateManager.enableCull();
					GlStateManager.enableLighting();
					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
					LCRenderHelper.restoreLightmapTextureCoords();
				}
			}
		}

		@SubscribeEvent
		public static void onRenderLayer(RenderSuperpowerLayerEvent e)
		{
			EntityPlayer player = e.getPlayer();
			AxisAlignedBB voxel = new AxisAlignedBB(-0.025, -0.025, -0.025, 0.025, 0.025, 0.025);

			for (AbilityConcussiveBlast ab : Ability.getAbilitiesFromClass(Ability.getAbilities(player), AbilityConcussiveBlast.class))
			{
				if (ab != null && ab.isUnlocked() && ab.isEnabled())
				{
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
					if(ab.context == EnumAbilityContext.OFF_HAND)
					{
						if(e.getPlayer().getPrimaryHand() == EnumHandSide.LEFT)
							e.getRenderPlayer().getMainModel().bipedRightArm.postRender(e.getScale());
						else
							e.getRenderPlayer().getMainModel().bipedLeftArm.postRender(e.getScale());
					} else {
						if(e.getPlayer().getPrimaryHand() == EnumHandSide.RIGHT)
							e.getRenderPlayer().getMainModel().bipedRightArm.postRender(e.getScale());
						else
							e.getRenderPlayer().getMainModel().bipedLeftArm.postRender(e.getScale());
					}
					GlStateManager.translate(0, 0.55, -1.2);

					for (int i = 0; i < 4 && i < ab.ticks; i++)
					{
						float offset = ((float) i) + ((ab.ticks % 4 + e.getPartialTicks()) / 5f);
						for (int angle = 0; angle < 360; angle += 8)
						{
							GlStateManager.pushMatrix();
							GlStateManager.translate(0.0f, 0.0f, -offset);
							GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
							float radius = 1 + offset*0.5f;
							GlStateManager.translate(0.0f, radius * 0.2, 0.0f);
							GlStateManager.rotate(angle, 0.0f, 0.0f, -1.0f);
							bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
							ClientUtils.addTexturedBoxVertices(bufferbuilder, voxel, 1.0f, 1.0f, 1.0f, 0.5f);
							tessellator.draw();
							GlStateManager.popMatrix();
						}
					}

					GlStateManager.disableAlpha();
					GlStateManager.disableBlend();
					GlStateManager.enableCull();
					GlStateManager.enableLighting();
					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
					LCRenderHelper.restoreLightmapTextureCoords();
				}
			}
		}

	}
}
