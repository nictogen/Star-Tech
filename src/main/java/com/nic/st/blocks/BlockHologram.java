package com.nic.st.blocks;

import com.nic.st.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlockHologram extends Block
{
	public static final AxisAlignedBB HOLO_BOX = new AxisAlignedBB(0, 1.5, 0.25, 1, 2, 0.75);

	public BlockHologram()
	{
		super(Material.IRON);
		setRegistryName("star-tech:hologram");
		setUnlocalizedName("hologram");
		setBlockUnbreakable();
	}

	@Override public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if (world.isRemote)
			Utils.breakVoxels(world, pos, player);
		return false;
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if (!worldIn.isRemote)
		{
			if (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockBlueprintCreator)
				return;
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}


	@Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return HOLO_BOX.offset(0, -1, 0);
	}

	@Nullable @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return HOLO_BOX.offset(0, -1, 0);
	}

	@Override public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return false;
	}
}
