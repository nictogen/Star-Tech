package com.nic.st.power;

import com.nic.st.ClientProxy;
import com.nic.st.StarTech;
import com.nic.st.util.ExplosionUtil;
import com.nic.st.util.Utils;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityHeld;
import lucraft.mods.lucraftcore.superpowers.abilities.data.AbilityData;
import lucraft.mods.lucraftcore.superpowers.abilities.data.AbilityDataInteger;
import lucraft.mods.lucraftcore.superpowers.abilities.supplier.EnumSync;
import lucraft.mods.lucraftcore.superpowers.render.RenderSuperpowerLayerEvent;
import lucraft.mods.lucraftcore.util.helper.LCRenderHelper;
import lucraft.mods.lucraftcore.util.helper.PlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nictogen on 1/6/19.
 */
public class AbilityPowerBlast extends AbilityHeld
{

	public static final AbilityData<Integer> DURATION = new AbilityDataInteger("duration").disableSaving().setSyncType(EnumSync.SELF)
			.enableSetting("duration", "How long the beam is fired for.");

	//TODO non-player rendering
	public AbilityPowerBlast(EntityLivingBase player)
	{
		super(player);
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

	@Override public void registerData()
	{
		super.registerData();
		this.dataManager.register(DURATION, 10);
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
		RayTraceResult result = Utils.rayTrace(entity, 100);
		if (!entity.world.isRemote && ticks % 5 == 1)
		{
			ArrayList<BlockPos> affectedBlocks = new ArrayList<>();
			ExplosionUtil.doExplosionA(entity.world, entity, result.hitVec.x, result.hitVec.y, result.hitVec.z, affectedBlocks);
		}
		else
		{
			for (int i = 0; i < 5; i++)
			{
				Vec3d d = result.hitVec.subtract(entity.getLookVec().normalize())
						.add(entity.getRNG().nextGaussian() * 0.5, entity.getRNG().nextGaussian() * 0.5, entity.getRNG().nextGaussian() * 0.5);
				Particle p = Minecraft.getMinecraft().effectRenderer
						.spawnEffectParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), d.x, d.y, d.z, 0, 0.0, 0);
				if (p != null)
				{

					if (entity.getRNG().nextInt(2) != 0)
					{
						p.setRBGColorF((float) Math.max(p.getRedColorF() + 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
								(float) Math.max(p.getBlueColorF() + 0.5, 0.0));
					}
					else
						p.setRBGColorF((float) Math.max(p.getRedColorF() - 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
								(float) Math.max(p.getBlueColorF() - 0.5, 0.0));

					//				p.multipleParticleScaleBy(0.2f);
				}
			}
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
		gui.drawTexturedModalRect(x, y, 2 * 16, 0, 16, 16);
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventBusSubscriber(modid = StarTech.MODID, value = Side.CLIENT)
	public static class Renderer
	{

		@SubscribeEvent
		public static void onRenderWorld(RenderWorldLastEvent e)
		{
			if (Minecraft.getMinecraft().player == null)
				return;

			EntityPlayer player = Minecraft.getMinecraft().player;
			Color c = Color.MAGENTA;

			for (AbilityPowerBlast ab : Ability.getAbilitiesFromClass(Ability.getAbilities(player), AbilityPowerBlast.class))
			{
				if (ab != null && ab.isUnlocked() && ab.isEnabled() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
				{
					GlStateManager.pushAttrib();
					RayTraceResult result = PlayerHelper.rayTrace(player, 100);
					double distance = player.getPositionVector().add(0, player.getEyeHeight() - 0.2, 0).distanceTo(result.hitVec);

					LCRenderHelper.setupRenderLightning();
					GlStateManager.translate(0, player.getEyeHeight(), 0);
					GlStateManager.rotate(-player.rotationYaw, 0, 1, 0);
					GlStateManager.rotate(player.rotationPitch, 1, 0, 0);

					{
						Vec3d start = ab.context == EnumAbilityContext.OFF_HAND ? new Vec3d(0.4F, -0.2, 0.9) : new Vec3d(-0.4F, -0.2, 0.9);
						Vec3d end = start.add(0, 0, distance);
						LCRenderHelper.drawGlowingLine(start, end, 0.5F, c);
					}

					{
						LCRenderHelper.restoreLightmapTextureCoords();
						GlStateManager.enableTexture2D();
						GlStateManager.enableCull();
						GlStateManager.popMatrix();
					}
					GlStateManager.popAttrib();
					return;
				}
			}
		}

		@SubscribeEvent
		public static void onRenderLayer(RenderSuperpowerLayerEvent e)
		{
			if (Minecraft.getMinecraft().player == null)
				return;

			//TODO better third person laser animation
			EntityPlayer player = e.getPlayer();
			Color c = Color.MAGENTA;
			for (AbilityPowerBlast ab : Ability.getAbilitiesFromClass(Ability.getAbilities(player), AbilityPowerBlast.class))
			{
				if (ab != null && ab.isUnlocked() && ab.isEnabled())
				{
					RayTraceResult result = PlayerHelper.rayTrace(player, 100);
					double distance = player.getPositionVector().add(0, player.getEyeHeight(), 0).distanceTo(result.hitVec);

					LCRenderHelper.setupRenderLightning();
					e.getRenderPlayer().getMainModel().bipedHead.postRender(e.getScale());
					{
						Vec3d start =
								ab.context == EnumAbilityContext.OFF_HAND ? new Vec3d(0.3F, 10F * e.getScale(), 1) : new Vec3d(-0.3F, 10F * e.getScale(), 1);
						Vec3d end = start.add(0, 10F * e.getScale(), -distance);
						LCRenderHelper.drawGlowingLine(start, end, 0.5F, c);
					}
					LCRenderHelper.finishRenderLightning();
					return;
				}
			}
		}

	}

}
