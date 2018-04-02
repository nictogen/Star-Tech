package com.nic.st.client.bakedmodels;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created by Nictogen on 4/1/18.
 */
public class PrintedGunOverrideList extends ItemOverrideList
{
	public PrintedGunOverrideList()
	{
		super(new ArrayList<>());
	}

	@Override public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
	{
		return super.handleItemState(originalModel, stack, world, entity);
	}
}
