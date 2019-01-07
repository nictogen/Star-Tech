package com.nic.st.entity;

import com.nic.st.power.ItemPowerStone;
import lucraft.mods.lucraftcore.infinity.EntityItemIndestructible;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Nictogen on 1/6/19.
 */
public class EntityItemIndestructibleST extends EntityItemIndestructible
{
	public EntityItemIndestructibleST(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	public EntityItemIndestructibleST(World worldIn, double x, double y, double z, ItemStack stack, float height, float width) {
		super(worldIn, x, y, z, stack, height, width);
	}

	public EntityItemIndestructibleST(World worldIn) {
		super(worldIn);
	}

	@Override public void onEntityUpdate()
	{
		if(getItem().getItem() instanceof ItemPowerStone)
			((ItemPowerStone) getItem().getItem()).onEntityItemIndestructibleUpdate(this);
		super.onEntityUpdate();
	}
}
