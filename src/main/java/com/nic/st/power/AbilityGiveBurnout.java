package com.nic.st.power;

import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConstant;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Nictogen on 2019-05-27.
 */
public class AbilityGiveBurnout extends AbilityConstant
{
	public AbilityGiveBurnout(EntityLivingBase entity)
	{
		super(entity);
	}

	@Override public void updateTick()
	{
		PotionBurnout.giveBurnout(entity);
	}


}
