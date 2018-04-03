package com.nic.st.blocks;

import com.nic.st.StarTech;
import com.nic.st.items.ItemBlueprint;
import com.nic.st.items.ItemPrintedGun;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class BlockPrinter extends Block
{
	public BlockPrinter()
	{
		super(Material.IRON);
		setRegistryName(StarTech.MODID, "printer");
		setUnlocalizedName("printer");
	}

	@Deprecated
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ)
	{
		if (playerIn.getHeldItemMainhand().getItem() instanceof ItemBlueprint)
		{
			ItemStack stack = playerIn.getHeldItemMainhand();
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
			te.blueprint = stack;
			te.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		else if (playerIn.getHeldItemMainhand().getItem() instanceof ItemAir && playerIn.isSneaking())
		{
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			if (te.blueprint.isEmpty())
				return false;

			playerIn.setHeldItem(EnumHand.MAIN_HAND, te.blueprint);
			te.blueprint = ItemStack.EMPTY;
			te.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		else if (playerIn.getHeldItemMainhand().getItem() instanceof ItemAir)
		{
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			if (te.gun.isEmpty() || te.ticks != 0)
				return false;

			playerIn.setHeldItem(EnumHand.MAIN_HAND, te.gun);
			te.gun = ItemStack.EMPTY;

			if (new Random().nextInt(4) == 0)
			{
				playerIn.addItemStackToInventory(te.blueprint);
				worldIn.setBlockToAir(pos);
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.0f, false);
			}
			else
			{
				te.markDirty();
				worldIn.notifyBlockUpdate(pos, state, state, 3);
			}
			return true;
		}
		return false;
	}

	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override @Nullable
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityPrinter();
	}

	public static class TileEntityPrinter extends TileEntity implements ITickable
	{

		public int ticks = 0;
		public ItemStack blueprint = ItemStack.EMPTY;
		public ItemStack gun = ItemStack.EMPTY;

		@Override public void update()
		{
			if (ticks == 0 && gun.isEmpty() && !blueprint.isEmpty() && getWorld().isBlockPowered(getPos()))
			{
				ticks++;
				gun = new ItemStack(StarTech.Items.printedGun, 1);
				ItemPrintedGun.createGunData(blueprint.getTagCompound().getByteArray("voxels"), gun);
				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			else if (ticks > 0 && getWorld().isBlockPowered(getPos()) && blueprint.getItem() instanceof ItemBlueprint)
				ticks++;
			else if (ticks != 0 && (!getWorld().isBlockPowered(getPos()) || blueprint.isEmpty()))
			{
				gun = ItemStack.EMPTY;
				ticks = 0;

				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			if (!blueprint.isEmpty() && ticks > blueprint.getTagCompound().getInteger("total") * 200)
			{
				ticks = 0;
			}
		}

		@Override public void readFromNBT(NBTTagCompound compound)
		{
			super.readFromNBT(compound);
			blueprint = new ItemStack(compound.getCompoundTag("blueprint"));
			gun = new ItemStack(compound.getCompoundTag("gun"));
			ticks = compound.getInteger("ticks");
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			NBTTagCompound nbt = super.writeToNBT(compound);
			nbt.setTag("blueprint", blueprint.writeToNBT(new NBTTagCompound()));
			nbt.setTag("gun", gun.writeToNBT(new NBTTagCompound()));
			nbt.setInteger("ticks", ticks);
			return nbt;
		}

		@Nullable @Override public SPacketUpdateTileEntity getUpdatePacket()
		{
			return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
		}

		@Override public NBTTagCompound getUpdateTag()
		{
			return writeToNBT(new NBTTagCompound());
		}

		@Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
		{
			readFromNBT(pkt.getNbtCompound());
		}
	}
}
