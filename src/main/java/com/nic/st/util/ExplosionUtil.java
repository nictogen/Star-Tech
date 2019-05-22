package com.nic.st.util;

import com.google.common.collect.Sets;
import com.nic.st.StarTech;
import com.nic.st.network.MessageMovePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Nictogen on 1/18/19.
 */
public class ExplosionUtil
{

	public static final float SIZE = 7.5f;

	public static void doExplosionA(World world, @Nullable EntityLivingBase exploder, double x, double y, double z, List<BlockPos> affectedBlockPos)
	{
		Explosion explosion = new Explosion(world, exploder, x, y, z, SIZE, true, true);

		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		int i = 16;

		for (int j = 0; j < 16; ++j)
		{
			for (int k = 0; k < 16; ++k)
			{
				for (int l = 0; l < 16; ++l)
				{
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
					{
						double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
						double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
						double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 = d0 / d3;
						d1 = d1 / d3;
						d2 = d2 / d3;
						float f = SIZE * (0.7F + world.rand.nextFloat() * 0.6F);
						double d4 = x;
						double d6 = y;
						double d8 = z;

						for (; f > 0.0F; f -= 0.22500001F)
						{
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							IBlockState iblockstate = world.getBlockState(blockpos);

							if (iblockstate.getMaterial() != Material.AIR)
							{
								f -= 1F;
							}

							//TODO ftb utilities checking
							if (f > 0.0F && exploder.canExplosionDestroyBlock(explosion, world, blockpos, iblockstate, f))
							{
								set.add(blockpos);
							}

							d4 += d0 * 0.30000001192092896D;
							d6 += d1 * 0.30000001192092896D;
							d8 += d2 * 0.30000001192092896D;
						}
					}
				}
			}
		}

		affectedBlockPos.addAll(set);
		List<Entity> list = world
				.getEntitiesWithinAABBExcludingEntity(exploder, new AxisAlignedBB(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0).grow(SIZE/5));

		for (int k2 = 0; k2 < list.size(); ++k2)
		{
			Entity entity = list.get(k2);

			entity.motionY += SIZE/5;
			entity.motionX += -Math.sin((double)(exploder.rotationYaw * (float)Math.PI / 180.0F)) * SIZE;
			entity.motionZ += Math.cos((double)(exploder.rotationYaw * (float)Math.PI / 180.0F)) * SIZE;

			if(entity instanceof EntityPlayerMP){
				StarTech.simpleNetworkWrapper.sendTo(new MessageMovePlayer(-Math.sin((double)(exploder.rotationYaw * (float)Math.PI / 180.0F)) * SIZE, SIZE/5, Math.cos((double)(exploder.rotationYaw * (float)Math.PI / 180.0F)) * SIZE), (EntityPlayerMP) entity);
			}

			entity.attackEntityFrom(DamageSource.causeExplosionDamage(exploder),SIZE*2);
		}


		world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F,
				(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

		for (BlockPos blockpos : affectedBlockPos)
		{
			IBlockState iblockstate = world.getBlockState(blockpos);
			Block block = iblockstate.getBlock();

			if (iblockstate.getMaterial() != Material.AIR && iblockstate.getBlock() != Blocks.BEDROCK) //TODO bedrock config
			{
				block.dropBlockAsItemWithChance(world, blockpos, world.getBlockState(blockpos), 1.0F / SIZE, 0);
				block.onBlockExploded(world, blockpos, explosion);
			}
		}

		//		if (causesFire)
		//		{
		//			for (BlockPos blockpos1 : affectedBlockPositions)
		//			{
		//				if (world.getBlockState(blockpos1).getMaterial() == Material.AIR && world.getBlockState(blockpos1.down()).isFullBlock()
		//						&& random.nextInt(3) == 0)
		//				{
		//					world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
		//				}
		//			} TODO purple flames
		//		}
	}
}
