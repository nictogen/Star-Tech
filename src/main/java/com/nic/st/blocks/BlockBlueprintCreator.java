package com.nic.st.blocks;

import com.nic.st.StarTech;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlockBlueprintCreator extends Block implements ITileEntityProvider
{
	public static final AxisAlignedBB HOLO_BOX = new AxisAlignedBB(0.25, 1, 0, 0.75, 1.5, 1);

	public BlockBlueprintCreator()
	{
		super(Material.IRON);
		setRegistryName(StarTech.MODID, "blueprint_creator");
		setUnlocalizedName("blueprint_creator");
	}

	@Deprecated
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override public boolean hasTileEntity()
	{
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override @Nullable
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityBlueprintCreator();
	}

	@Nullable @Override public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return createTileEntity(worldIn, getDefaultState());
	}

	public static class TileEntityBlueprintCreator extends TileEntity implements ITickable
	{
		public byte[] voxels = new byte[1024];

		public TileEntityBlueprintCreator()
		{
		}

		@Override public void update()
		{
			if (getWorld().isAirBlock(getPos().up()))
			{
				getWorld().setBlockState(pos, StarTech.Blocks.hologram.getDefaultState());
			}
		}

		@Override public void readFromNBT(NBTTagCompound compound)
		{
			super.readFromNBT(compound);
			voxels = compound.getByteArray("voxels");
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			NBTTagCompound nbt = super.writeToNBT(compound);
			nbt.setByteArray("voxels", voxels);
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

		@Override public AxisAlignedBB getRenderBoundingBox()
		{
			return super.getRenderBoundingBox().union(HOLO_BOX);
		}
	}

}
