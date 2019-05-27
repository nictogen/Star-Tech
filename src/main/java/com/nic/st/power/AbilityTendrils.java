package com.nic.st.power;

import com.nic.st.ClientProxy;
import com.nic.st.StarTech;
import com.nic.st.network.MessageMovePlayer;
import com.nic.st.util.Utils;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityHeld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Nictogen on 11/3/18.
 */
public class AbilityTendrils extends AbilityHeld
{
	public AbilityTendrils(EntityLivingBase player)
	{
		super(player);
	}

	@Override public void updateTick()
	{
		double progress = this.ticks > 50.0 ? 50.0 : (double) this.ticks;
		RayTraceResult result = Utils.rayTrace(entity, progress/10);
		if (result.entityHit instanceof EntityLivingBase)
		{
			PotionBurnout.giveBurnout((EntityLivingBase) result.entityHit);
			result.entityHit.motionX = 0;
			result.entityHit.motionZ = 0;
			double motionY = result.entityHit.onGround ? 0.4 : (result.entityHit.motionY > 0) ? result.entityHit.motionY : 0;
			result.entityHit.fallDistance = 0;
			result.entityHit.motionY = motionY;
			if (result.entityHit instanceof EntityPlayerMP)
				StarTech.simpleNetworkWrapper.sendTo(new MessageMovePlayer(0, motionY, 0), (EntityPlayerMP) result.entityHit);
		}

	}

	@SideOnly(Side.CLIENT)
	@Override public void drawIcon(Minecraft mc, Gui gui, int x, int y)
	{
		mc.renderEngine.bindTexture(ClientProxy.SUPERPOWER_ICONS);
		gui.drawTexturedModalRect(x, y, 16, 0, 16, 16);
	}
}
