package com.nic.st.items;

import com.nic.st.StarTech;
import net.minecraft.item.Item;

/**
 * Created by Nictogen on 4/3/18.
 */
public class ItemBlueprint extends Item
{
	public ItemBlueprint()
	{
		setRegistryName(StarTech.MODID, "blueprint");
		setUnlocalizedName("blueprint");
		setMaxStackSize(1);
	}

}
