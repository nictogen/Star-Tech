package com.nic.st.items;

import com.nic.st.StarTech;
import com.nic.st.entity.EntityBullet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

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

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		//		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote)
		{
			EntityBullet entityBullet = new EntityBullet(worldIn, playerIn);
			entityBullet.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityBullet);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}
}
