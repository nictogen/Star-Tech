package com.nic.st.items;

import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nictogen on 7/27/18.
 */
public class ItemPowerStone extends ItemInfinityStone
{
	@Override public EnumInfinityStone getType()
	{
		return EnumInfinityStone.POWER;
	}

	@Override public boolean isContainer()
	{
		return false;
	}

	@Override public List getAbilityBarEntries(EntityPlayer player, ItemStack stack)
	{
		return new ArrayList();
	}
}
