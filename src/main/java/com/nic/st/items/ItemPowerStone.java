package com.nic.st.items;

import com.nic.st.StarTech;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nictogen on 7/27/18.
 */
public class ItemPowerStone extends ItemInfinityStone
{
	public ItemPowerStone()
	{
		setRegistryName(StarTech.MODID, "power_stone");
		setUnlocalizedName("power_stone");
		setMaxStackSize(1);
	}

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

	public static int getPowerStoneDuration(EntityPlayer player)
	{
		ItemStack stack = player.getHeldItemMainhand();
		int progress = 0;
		if (stack.getItem() instanceof ItemPowerStone)
		{
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			progress = stack.getTagCompound().getInteger("power_duration");
		}

		stack = player.getHeldItemOffhand();
		if (stack.getItem() instanceof ItemPowerStone)
		{
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			int a = stack.getTagCompound().getInteger("power_duration");
			progress = a > progress ? a : progress;
		}
		return progress;
	}

	public static int getPowerStoneUseDuration(EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() instanceof ItemPowerStone)
		{
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			return stack.getTagCompound().getInteger("power_use") == 0 ? 0 : player.ticksExisted - stack.getTagCompound().getInteger("power_use");
		}
		return 0;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if (stack.getTagCompound().getInteger("power_use") == 0)
			stack.getTagCompound().setInteger("power_use", player.ticksExisted);
		else
			stack.getTagCompound().setInteger("power_use", 0);

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if (isSelected)
		{
			stack.getTagCompound().setInteger("power_duration", stack.getTagCompound().getInteger("power_duration") + 1);
		}
		else
		{
			stack.getTagCompound().setInteger("power_duration", 0);
			stack.getTagCompound().setInteger("power_use", 0);
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.NONE;
	}

	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}

	@Override public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		//		stack.getTagCompound().setInteger("power_use", 0);
		//		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}

	@Override public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		if (!entityItem.getItem().hasTagCompound())
			entityItem.getItem().setTagCompound(new NBTTagCompound());
		entityItem.getItem().getTagCompound().setInteger("power_duration", 0);
		entityItem.getItem().getTagCompound().setInteger("power_use", 0);
		return false;
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return oldStack.getItem() != newStack.getItem();
	}
}
