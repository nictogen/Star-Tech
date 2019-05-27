package com.nic.st.items;

import com.nic.st.StarTech;
import com.nic.st.entity.EntityBullet;
import com.nic.st.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Nictogen on 4/1/18.
 */
public class ItemPrintedGun extends Item
{
	public ItemPrintedGun()
	{
		setRegistryName(StarTech.MODID, "printed_gun");
		setTranslationKey("printed_gun");
		setMaxStackSize(1);
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
			nbt.setIntArray("ammo_indexes", new int[]{});
			nbt.setInteger("ammo_voxels", 0);
			stack.setTagCompound(nbt);
		}
		return stack.getTagCompound();
	}

	public static NBTTagCompound createGunData(byte[] voxels, ItemStack stack, int[][] colors, VoxelUses[] uses)
	{
		NBTTagCompound nbt = getGunData(stack);

		GunStats stats = new GunStats(voxels, uses);
		HashSet<Integer> ammoIndexesSet = new HashSet<>();
		for (int i = 0; i < uses.length; i++)
			if (uses[i] == VoxelUses.AMMO)
				ammoIndexesSet.add(i + 1);
		int[] ammoIndexes = new int[ammoIndexesSet.size()];
		int i = 0;
		for (Integer integer : ammoIndexesSet)
			ammoIndexes[i++] = integer;
		nbt.setByteArray("voxels", voxels);
		nbt.setInteger("max_ammo", (int) (stats.ammo));
		nbt.setInteger("ammo", (int) (stats.ammo));
		nbt.setInteger("fire_freq", (int) stats.fireRate);
		nbt.setInteger("fireCount", (int) stats.fireRate);
		nbt.setDouble("damage", stats.damage);
		nbt.setIntArray("ammo_indexes", ammoIndexes);
		nbt.setInteger("ammo_voxels", stats.ammoVoxels);

		for (i = 0; i < colors.length && i < uses.length; i++)
		{
			nbt.setIntArray("color" + i, colors[i]);
			if (uses[i] == VoxelUses.DAMAGE)
			{
				nbt.setIntArray("damage_color", colors[i]);
			}
			else if (uses[i] == VoxelUses.FIRE_RATE)
			{
				nbt.setIntArray("fire_rate_color", colors[i]);
			}
		}

		if (nbt.getIntArray("fire_rate_color").length == 0)
			nbt.setIntArray("fire_rate_color", nbt.getIntArray("damage_color"));

		return nbt;
	}

	@Override public double getDurabilityForDisplay(ItemStack stack)
	{
		NBTTagCompound gunData = getGunData(stack);
		return 1.0 - ((double) gunData.getInteger("ammo") / (double) gunData.getInteger("max_ammo"));
	}

	@Override public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override public boolean isDamaged(ItemStack stack)
	{
		return true;
	}

	public static class GunStats
	{
		public int totalVoxels = 1, structureVoxels, damageVoxels, ammoVoxels, fireRateVoxels;

		public double damage, ammo, fireRate;

		public GunStats(byte[] voxels, VoxelUses[] uses)
		{

			for (byte voxel : voxels)
			{
				if (voxel > 0)
				{
					totalVoxels++;
					switch (uses[voxel - 1])
					{
					case STRUCTURE:
						structureVoxels++;
						break;
					case DAMAGE:
						damageVoxels++;
						break;
					case AMMO:
						ammoVoxels++;
						break;
					case FIRE_RATE:
						fireRateVoxels++;
						break;
					}
				}
			}

			damage = 75 * (((double) damageVoxels + 1.0) / (double) totalVoxels);
			ammo = (((double) ammoVoxels + 1.0) / (double) totalVoxels) * 200;
			fireRate = 5.0 / (((double) fireRateVoxels + 1.0) / (double) totalVoxels);

		}

		public boolean isValid()
		{
			return structureVoxels >= totalVoxels / 4;
		}

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

		if (gunData.getInteger("ammo") <= 0 && !reload(player, itemstack))
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		if (gunData.getInteger("fireCount") < gunData.getInteger("fire_freq"))
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);

		gunData.setInteger("fireCount", 0);
		gunData.setInteger("ammo", gunData.getInteger("ammo") - 1);
		if (!worldIn.isRemote)
		{
			EnumHandSide side = (player.getPrimaryHand() == EnumHandSide.RIGHT) ?
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;

			Vec3d eyes = player.getPositionEyes(0.0f);
			Vec3d rotVec = Utils.getVectorForRotation(player.rotationPitch, (side == EnumHandSide.RIGHT) ? player.rotationYaw + 90 : player.rotationYaw - 90);
			Vec3d offset = eyes.add(rotVec.scale(0.3)).subtract(0, 0.1, 0);
			EntityBullet entityBullet = new EntityBullet(worldIn, player);
			entityBullet.damage = gunData.getDouble("damage");

			entityBullet.setLocationAndAngles(offset.x, offset.y, offset.z, entityBullet.rotationYaw, entityBullet.rotationPitch);
			entityBullet.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityBullet);

			int[] color1 = gunData.getIntArray("damage_color");
			int[] color2 = gunData.getIntArray("fire_rate_color");
			if (color1.length > 2)
				entityBullet.getDataManager().set(EntityBullet.COLOR1, new BlockPos(color1[0], color1[1], color1[2]));
			if (color2.length > 2)
				entityBullet.getDataManager().set(EntityBullet.COLOR2, new BlockPos(color2[0], color2[1], color2[2]));

			worldIn.playSound(null, player.posX, player.posY, player.posZ, StarTech.Sounds.shoot, SoundCategory.PLAYERS, 1.0f, new Random().nextFloat());
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	private boolean reload(EntityPlayer player, ItemStack stack)
	{
		NBTTagCompound gunData = getGunData(stack);

		for (ItemStack itemStack : player.inventory.mainInventory)
		{
			if (itemStack.getItem() == Items.REDSTONE)
			{
				if (gunData.getInteger("ammo") < gunData.getInteger("max_ammo"))
				{
					gunData.setInteger("ammo", Math.min(gunData.getInteger("ammo" + 20), gunData.getInteger("max_ammo")));
				}
				else
					return true;
			}
			if (itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
			{
				if (gunData.getInteger("ammo") < gunData.getInteger("max_ammo"))
				{
					gunData.setInteger("ammo", Math.min(gunData.getInteger("ammo" + 180), gunData.getInteger("max_ammo")));
				}
				else
					return true;
			}
			if (itemStack.getItem() == Items.GUNPOWDER)
			{
				if (gunData.getInteger("ammo") < gunData.getInteger("max_ammo"))
				{
					gunData.setInteger("ammo", Math.min(gunData.getInteger("ammo" + 40), gunData.getInteger("max_ammo")));
				}
				else
					return true;
			}
			if (itemStack.getItem() == Items.GLOWSTONE_DUST)
			{
				if (gunData.getInteger("ammo") < gunData.getInteger("max_ammo"))
				{
					gunData.setInteger("ammo", Math.min(gunData.getInteger("ammo" + 40), gunData.getInteger("max_ammo")));
				}
				else
					return true;
			}
			if (itemStack.getItem() == Item.getItemFromBlock(Blocks.GLOWSTONE))
			{
				if (gunData.getInteger("ammo") < gunData.getInteger("max_ammo"))
				{
					gunData.setInteger("ammo", Math.min(gunData.getInteger("ammo" + 160), gunData.getInteger("max_ammo")));
				}
				else
					return true;
			}
		}

		return gunData.getInteger("ammo") > 0;
	}

	public enum VoxelUses
	{
		STRUCTURE("Structure"),
		DAMAGE("Damage"),
		AMMO("Ammo"),
		FIRE_RATE("Fire Rate");
		public String name;

		VoxelUses(String name)
		{
			this.name = name;
		}
	}
}
