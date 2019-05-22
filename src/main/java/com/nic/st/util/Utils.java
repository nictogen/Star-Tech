package com.nic.st.util;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nictogen on 4/1/18.
 */
public class Utils
{

	public static void placeVoxels(World w, BlockPos pos, EntityPlayer player)
	{
		ArrayList<BlockBlueprintCreator.TileEntityBlueprintCreator> creators = new ArrayList<>();

		if (w.getTileEntity(pos.down()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			creators.add((BlockBlueprintCreator.TileEntityBlueprintCreator) w.getTileEntity(pos.down()));
		}

		Vec3d hitVec = player.getPositionEyes(0.0f);
		Vec3d lookPos = player.getLook(0.0f);
		hitVec = hitVec.add(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (BlockBlueprintCreator.TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getEmptyVoxel(BlockHologram.HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels,
					creator.getPos());
			if (voxel != -1)
				creator.voxels[voxel] = (byte) (creator.buttonDown + 1);

			creator.markDirty();
			IBlockState state = creator.getWorld().getBlockState(creator.getPos());
			creator.getWorld().notifyBlockUpdate(creator.getPos(), state, state, 3);
		}
	}

	public static void breakVoxels(World w, BlockPos pos, EntityPlayer player)
	{
		ArrayList<BlockBlueprintCreator.TileEntityBlueprintCreator> creators = new ArrayList<>();

		if (w.getTileEntity(pos.down()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			creators.add((BlockBlueprintCreator.TileEntityBlueprintCreator) w.getTileEntity(pos.down()));
		}
		if (w.getTileEntity(pos.down().north()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			creators.add((BlockBlueprintCreator.TileEntityBlueprintCreator) w.getTileEntity(pos.down().north()));
		}
		if (w.getTileEntity(pos.down().south()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			creators.add((BlockBlueprintCreator.TileEntityBlueprintCreator) w.getTileEntity(pos.down().south()));
		}

		Vec3d hitVec = player.getPositionEyes(0.0f);
		Vec3d lookPos = player.getLook(0.0f);
		hitVec = hitVec.add(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (BlockBlueprintCreator.TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getVoxel(BlockHologram.HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels,
					creator.getPos());
			if (voxel != -1)
				creator.voxels[voxel] = 0;

			creator.markDirty();
			IBlockState state = creator.getWorld().getBlockState(creator.getPos());
			creator.getWorld().notifyBlockUpdate(creator.getPos(), state, state, 3);
		}
	}

	public static int getVoxel(AxisAlignedBB bb, Vec3d origin, Vec3d end, byte[] voxels, BlockPos pos)
	{
		RayTraceResult result = Utils.calculateFarthestIntercept(bb, origin, end);

		if (result == null)
			return -1;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		Vec3d closest = null;
		int closestIndex = -1;
		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i / 64, y = (i % 64) / 8, z = i % 8)
		{
			if (voxels[i] != 0)
			{
				AxisAlignedBB current = voxel.offset(pos.getX() + x * 0.0625, pos.getY() + 1.5 + y * 0.0625, pos.getZ() + z * 0.0625 + 0.25);
				RayTraceResult currentResult = current.calculateIntercept(origin, end);
				if (currentResult != null)
				{
					if (Utils.compare(origin, closest, currentResult.hitVec) <= 0)
					{
						closest = currentResult.hitVec;
						closestIndex = i;
					}
				}
			}
		}

		return closestIndex;
	}

	private static RayTraceResult calculateFarthestIntercept(AxisAlignedBB bb, Vec3d origin, Vec3d end)
	{
		Vec3d vec3d = collideWithXPlane(bb, bb.minX, origin, end);
		EnumFacing enumfacing = EnumFacing.WEST;
		Vec3d vec3d1 = collideWithXPlane(bb, bb.maxX, origin, end);

		if (vec3d1 != null && compare(origin, vec3d, vec3d1) >= 0)
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.EAST;
		}

		vec3d1 = collideWithYPlane(bb, bb.minY, origin, end);

		if (vec3d1 != null && compare(origin, vec3d, vec3d1) >= 0)
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.DOWN;
		}

		vec3d1 = collideWithYPlane(bb, bb.maxY, origin, end);

		if (vec3d1 != null && compare(origin, vec3d, vec3d1) >= 0)
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.UP;
		}

		vec3d1 = collideWithZPlane(bb, bb.minZ, origin, end);

		if (vec3d1 != null && compare(origin, vec3d, vec3d1) >= 0)
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.NORTH;
		}

		vec3d1 = collideWithZPlane(bb, bb.maxZ, origin, end);

		if (vec3d1 != null && compare(origin, vec3d, vec3d1) >= 0)
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.SOUTH;
		}

		return vec3d == null ? null : new RayTraceResult(vec3d, enumfacing);
	}

	private static Vec3d collideWithXPlane(AxisAlignedBB bb, double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
	{
		Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
		return vec3d != null && vec3d.y >= bb.minY && vec3d.y <= bb.maxY && vec3d.z >= bb.minZ && vec3d.z <= bb.maxZ ? vec3d : null;
	}

	private static Vec3d collideWithYPlane(AxisAlignedBB bb, double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
	{
		Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
		return vec3d != null && vec3d.x >= bb.minX && vec3d.x <= bb.maxX && vec3d.z >= bb.minZ && vec3d.z <= bb.maxZ ? vec3d : null;
	}

	private static Vec3d collideWithZPlane(AxisAlignedBB bb, double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
	{
		Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
		return vec3d != null && vec3d.x >= bb.minX && vec3d.x <= bb.maxX && vec3d.y >= bb.minY && vec3d.y <= bb.maxY ? vec3d : null;
	}

	/**
	 * @return 0 if the current is null, -1 if the vec to check is closer than the current, or 1 if the check is farther away than the current
	 */
	private static int compare(Vec3d origin, @Nullable Vec3d current, Vec3d checking)
	{
		return current == null ? 0 : origin.squareDistanceTo(checking) > origin.squareDistanceTo(current) ? 1 : -1;
	}

	private static int getEmptyVoxel(AxisAlignedBB bb, Vec3d origin, Vec3d end, byte[] voxels, BlockPos pos)
	{
		if (Utils.calculateFarthestIntercept(bb, origin, end) == null)
			return -1;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);

		RayTraceResult closestVoxel = null;

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i / 64, y = (i % 64) / 8, z = i % 8)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625, pos.getY() + 1.5 + y * 0.0625, pos.getZ() + z * 0.0625 + 0.25);
			if (voxels[i] != 0)
			{
				RayTraceResult cvResult = voxelPos.calculateIntercept(origin, end);
				if (cvResult != null)
				{
					if (Utils.compare(origin, closestVoxel == null ? null : closestVoxel.hitVec, cvResult.hitVec) <= 0)
					{
						closestVoxel = cvResult;
					}
				}
			}
		}

		RayTraceResult farthestEmptyVoxel = null;
		int farthestEmptyIndex = -1;

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i / 64, y = (i % 64) / 8, z = i % 8)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625, pos.getY() + 1.5 + y * 0.0625, pos.getZ() + z * 0.0625 + 0.25);
			if (voxels[i] == 0)
			{
				RayTraceResult fvResult = voxelPos.calculateIntercept(origin, end);
				if (fvResult != null)
				{
					if (Utils.compare(origin, closestVoxel == null ? null : closestVoxel.hitVec, fvResult.hitVec) <= 0)
					{
						if (Utils.compare(origin, farthestEmptyVoxel == null ? null : farthestEmptyVoxel.hitVec, fvResult.hitVec) >= 0)
						{
							farthestEmptyVoxel = fvResult;
							farthestEmptyIndex = i;
						}
					}
				}
			}
		}

		return farthestEmptyIndex;
	}

	public static Vec3d getVectorForRotation(float pitch, float yaw)
	{
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
	}

	/**
	 * Returns a random integer between -1 and 1
	 */
	public static int randomInt3(Random random)
	{
		int r = random.nextInt(3);
		if (r == 2)
			r = -1;
		return r;
	}

	public static int p(World w)
	{
		int p = w.rand.nextInt(1);
		if (p == 0)
			p = -1;
		return p;
	}

	public static double d(World w, int divisor)
	{
		double d = 0.0;
		while (d == 0)
			d = w.rand.nextGaussian() / divisor;
		return d;
	}

	public static RayTraceResult rayTrace(EntityLivingBase player, double distance) {
		Vec3d lookVec = player.getLookVec();
		for (int i = 0; i < distance * 2; i++) {
			float scale = i / 2F;
			Vec3d pos = player.getPositionVector().add(0, player.getEyeHeight(), 0).add(lookVec.scale(scale));

			if (player.world.isBlockFullCube(new BlockPos(pos)) && !player.world.isAirBlock(new BlockPos(pos))) {
				return new RayTraceResult(pos, null);
			} else {
				Vec3d pos1 = pos.add(0.25F, 0.25F, 0.25F);
				Vec3d pos2 = pos.add(-0.25F, -0.25F, -0.25F);
				for (Entity entity : player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z))) {
					return new RayTraceResult(entity);
				}
			}
		}
		return new RayTraceResult(player.getPositionVector().add(lookVec.scale(distance)), null);
	}
}
