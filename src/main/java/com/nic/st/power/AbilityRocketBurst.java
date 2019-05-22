package com.nic.st.power;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityAction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;

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
}
