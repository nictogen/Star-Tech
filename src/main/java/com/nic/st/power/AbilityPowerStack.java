package com.nic.st.power;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityAction;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Nictogen on 1/18/19.
 */
public class AbilityPowerStack extends AbilityAction
{
	public AbilityPowerStack(EntityPlayer player)
	{
		super(player);
	}

	@Override public boolean action()
	{
		return false;
	}


}
