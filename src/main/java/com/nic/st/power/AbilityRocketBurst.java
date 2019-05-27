package com.nic.st.power;

import com.nic.st.ClientProxy;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Nictogen on 1/22/19.
 */
public class AbilityRocketBurst extends AbilityAction
{
	public AbilityRocketBurst(EntityLivingBase player)
	{
		super(player);
	}

	@Override public boolean action()
	{
		for (int i = 0; i < 5; i++)
		{
			AbilityPowerCyclone.shootRocket(this.context == EnumAbilityContext.OFF_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, entity);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override public void drawIcon(Minecraft mc, Gui gui, int x, int y)
	{
		mc.renderEngine.bindTexture(ClientProxy.SUPERPOWER_ICONS);
		gui.drawTexturedModalRect(x, y, 0, 0, 16, 16);
	}
}
