package com.nic.st.blocks;

import com.nic.st.StarTech;
import com.nic.st.items.ItemPrintedGun;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;

import static com.nic.st.blocks.BlockHologram.HOLO_BOX;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlockBlueprintCreator extends Block
{

	public BlockBlueprintCreator()
	{
		super(Material.IRON);
		setRegistryName(StarTech.MODID, "blueprint_creator");
		setTranslationKey("blueprint_creator");
		setHardness(2.0f).setResistance(10.0f);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	public boolean hasTileEntity(IBlockState state)
	{
		return true;
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

	@Override @Nullable
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityBlueprintCreator();
	}

	public static class TileEntityBlueprintCreator extends TileEntity implements ITickable
	{
		public static final int VOXEL_TYPES = 7;
		public byte[] voxels = new byte[1024];
		public Color[] colors;
		public ItemPrintedGun.VoxelUses[] uses;
		public int buttonDown = 0;
		public boolean useCachedModel = false;

		public TileEntityBlueprintCreator()
		{
			uses = new ItemPrintedGun.VoxelUses[VOXEL_TYPES];
			colors = new Color[VOXEL_TYPES];
			for (int i = 0; i < VOXEL_TYPES; i++)
			{
				uses[i] = ItemPrintedGun.VoxelUses.STRUCTURE;
				colors[i] = new Color(1.0f, 1.0f, 1.0f, 1.0f);
			}

		}

		@Override public void update()
		{
			if (getWorld().isAirBlock(getPos().up()))
			{
				getWorld().setBlockState(pos.up(), StarTech.Blocks.hologram.getDefaultState());
			}
		}

		@Override public void readFromNBT(NBTTagCompound compound)
		{
			super.readFromNBT(compound);
			voxels = compound.getByteArray("voxels");
			buttonDown = compound.getInteger("buttonDown");
			for (int i = 0; i < VOXEL_TYPES; i++)
			{
				int[] rgba = compound.getIntArray("color" + i);
				if (rgba.length > 3)
					colors[i] = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
			}
			for (int i = 0; i < VOXEL_TYPES; i++)
			{
				uses[i] = ItemPrintedGun.VoxelUses.values()[compound.getInteger("voxelUse" + i)];
			}
			useCachedModel = false;
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			NBTTagCompound nbt = super.writeToNBT(compound);
			nbt.setByteArray("voxels", voxels);
			nbt.setInteger("buttonDown", buttonDown);
			buttonDown = compound.getInteger("buttonDown");
			for (int i = 0; i < VOXEL_TYPES; i++)
			{
				compound.setIntArray("color" + i, new int[] { colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), colors[i].getAlpha() });
			}
			for (int i = 0; i < VOXEL_TYPES; i++)
			{
				compound.setInteger("voxelUse" + i, uses[i].ordinal());
			}
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
