package com.nic.st.blocks;

import com.nic.st.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlockHologram extends Block
{
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

	@Override public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return false;
	}
}
