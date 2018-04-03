package com.nic.st.items;

import com.nic.st.StarTech;
import com.nic.st.entity.EntityBullet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Nictogen on 4/1/18.
 */
public class ItemPrintedGun extends Item
{
	public ItemPrintedGun()
	{
		setRegistryName(StarTech.MODID, "printed_gun");
		setUnlocalizedName("printed_gun");
	}

	public static NBTTagCompound getGunData(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByteArray("voxels", new byte[1024]);
			nbt.setInteger("fire_freq", -1);
			nbt.setInteger("fireCount", 0);
			nbt.setInteger("max_ammo", -1);
			nbt.setInteger("ammo", -1);
			nbt.setDouble("damage", -1.0);
			stack.setTagCompound(nbt);
		}
		return stack.getTagCompound();
	}

	public static NBTTagCompound createGunData(byte[] voxels, ItemStack stack)
	{
		NBTTagCompound nbt = getGunData(stack);

		int yellow = 0;
		double darkGrey = 0;
		double lightGrey = 0;
		int blue = 0;

		for (byte voxel : voxels)
		{
			switch (voxel)
			{
			case 0:
				continue;
			case 1:
				yellow++;
				continue;
			case 2:
				darkGrey++;
				continue;
			case 3:
				lightGrey++;
				continue;
			case 4:
				blue++;
			}
		}

		if (yellow > 20)
			yellow = 20;
		if (blue > 20)
			blue = 20;

		nbt.setByteArray("voxels", voxels);
		nbt.setInteger("fire_freq", 40 - yellow * 2);
		nbt.setInteger("max_ammo", blue * 250);
		nbt.setInteger("ammo", blue * 250);
		nbt.setDouble("damage", darkGrey / lightGrey);
		return nbt;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
	{
		EnumHand oppositeHand = EnumHand.MAIN_HAND == handIn ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		if (player.getHeldItem(oppositeHand).getItem() instanceof ItemPrintedGun)
		{
			shoot(worldIn, player, oppositeHand);
		}
		return shoot(worldIn, player, handIn);
	}

	@Override public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		NBTTagCompound gunData = getGunData(stack);
		if (gunData.getInteger("fireCount") < gunData.getInteger("fire_freq"))
		{
			gunData.setInteger("fireCount", gunData.getInteger("fireCount") + 1);
		}
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return oldStack.getItem() != newStack.getItem();
	}

	private ActionResult<ItemStack> shoot(World worldIn, EntityPlayer player, EnumHand handIn)
	{
		ItemStack itemstack = player.getHeldItem(handIn);

		NBTTagCompound gunData = getGunData(itemstack);

		if (gunData.getInteger("ammo") <= 0)
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		if (gunData.getInteger("fireCount") < gunData.getInteger("fire_freq"))
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);

		//		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		gunData.setInteger("fireCount", 0);
		gunData.setInteger("ammo", gunData.getInteger("ammo") - (int) (gunData.getDouble("damage") * 20.0));
		if (!worldIn.isRemote)
		{
			EnumHandSide side = (player.getPrimaryHand() == EnumHandSide.RIGHT) ?
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;

			Vec3d eyes = player.getPositionEyes(0.0f);
			Vec3d rotVec = getVectorForRotation(player.rotationPitch, (side == EnumHandSide.RIGHT) ? player.rotationYaw + 90 : player.rotationYaw - 90);
			Vec3d offset = eyes.add(rotVec.scale(0.3)).subtract(0, 0.1, 0);
			EntityBullet entityBullet = new EntityBullet(worldIn, player);
			entityBullet.damage = gunData.getDouble("damage");
			entityBullet.setLocationAndAngles(offset.x, offset.y, offset.z, entityBullet.rotationYaw, entityBullet.rotationPitch);
			entityBullet.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityBullet);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	private Vec3d getVectorForRotation(float pitch, float yaw)
	{
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
	}
}
