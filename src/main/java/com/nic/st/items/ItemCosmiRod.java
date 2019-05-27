package com.nic.st.items;

import com.nic.st.abilities.AbilityConcussiveBlast;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Created by Nictogen on 2019-05-27.
 */
public class ItemCosmiRod extends ItemInfinityGauntlet
{
	public ItemCosmiRod()
	{
		super("cosmi_rod");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		EnumHand opposite = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		if (!playerIn.getHeldItem(handIn).hasTagCompound())
			playerIn.getHeldItem(handIn).setTagCompound(new NBTTagCompound());
		ItemStack stack = playerIn.getHeldItem(handIn);
		InventoryInfinityGauntlet inv = new InventoryInfinityGauntlet(stack);
		if (playerIn.getHeldItem(opposite).getItem() instanceof ItemInfinityStone)
		{
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack s = inv.getStackInSlot(i);

				if (!s.isEmpty() && s.getItem() instanceof ItemInfinityStone)
					return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
			}
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack s = inv.getStackInSlot(i);

				if (s.isEmpty())
				{
					inv.setInventorySlotContents(i, playerIn.getHeldItem(opposite));
					playerIn.setHeldItem(opposite, ItemStack.EMPTY);
					return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
				}
			}
		}
		else if (playerIn.getHeldItem(opposite).isEmpty() && playerIn.isSneaking())
		{

			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack s = inv.getStackInSlot(i);

				if (!s.isEmpty() && s.getItem() instanceof ItemInfinityStone)
				{
					playerIn.setHeldItem(opposite, s);
					inv.setInventorySlotContents(i, ItemStack.EMPTY);
					return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
				}
			}
		}

		return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	@Override
	public Ability.AbilityMap addDefaultAbilities(EntityLivingBase player, Ability.AbilityMap abilities, Ability.EnumAbilityContext context)
	{
		abilities.put("concussive_blast", new AbilityConcussiveBlast(player).setMaxCooldown(100).setDataValue(AbilityConcussiveBlast.DURATION, 7)
				.setDataValue(AbilityConcussiveBlast.DAMAGE, 5f));
		return super.addDefaultAbilities(player, abilities, context);
	}
}
