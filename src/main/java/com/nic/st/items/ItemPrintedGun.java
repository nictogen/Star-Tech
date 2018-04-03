package com.nic.st.items;

import com.nic.st.StarTech;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Nictogen on 4/1/18.
 */
public class ItemPrintedGun extends Item
{
	public ItemPrintedGun()
	{
		setRegistryName(StarTech.MODID, "printed_gun");
	}

	public static NBTTagCompound getGunData(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByteArray("voxels", new byte[1024]);
			stack.setTagCompound(nbt);
		}
		return stack.getTagCompound();
	}

}
