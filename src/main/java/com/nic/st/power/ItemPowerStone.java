package com.nic.st.power;

import com.nic.st.StarTech;
import com.nic.st.entity.EntityItemIndestructibleST;
import lucraft.mods.lucraftcore.infinity.EntityItemIndestructible;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.supplier.IAbilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 7/27/18.
 */
public class ItemPowerStone extends ItemInfinityStone implements IAbilityProvider
{
	public ItemPowerStone()
	{
		setRegistryName(StarTech.MODID, "power_stone");
		setTranslationKey("power_stone");
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

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	public void onEntityItemIndestructibleUpdate(EntityItemIndestructible entityItem)
	{
		if (!entityItem.getItem().hasTagCompound())
			entityItem.getItem().setTagCompound(new NBTTagCompound());
		entityItem.getItem().getTagCompound().setTag("lc_item_abilities", new NBTTagCompound());
	}

	@Nullable
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityItemIndestructibleST item = new EntityItemIndestructibleST(world, location.posX, location.posY, location.posZ, itemstack);
		item.setEntitySize(entityHeight, entityWidth);
		item.motionX = location.motionX;
		item.motionY = location.motionY;
		item.motionZ = location.motionZ;
		return item;
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return oldStack.getItem() != newStack.getItem();
	}

	@Override public Ability.AbilityMap addStoneAbilities(EntityLivingBase entity, Ability.AbilityMap abilities, Ability.EnumAbilityContext context)
	{
		abilities.put("power_blast", new AbilityPowerBlast(entity));
		abilities.put("power_impower", new AbilityPowerImpower(entity));
		abilities.put("power_rocket_burst", new AbilityRocketBurst(entity));
		return super.addStoneAbilities(entity, abilities, context);
	}

	@Override public Ability.AbilityMap addDefaultAbilities(EntityLivingBase entity, Ability.AbilityMap abilities, Ability.EnumAbilityContext context)
	{
		abilities.put("power_tendrils", new AbilityTendrils(entity));
		abilities.put("power_cyclone", new AbilityPowerCyclone(entity));
		return abilities;
	}
}
