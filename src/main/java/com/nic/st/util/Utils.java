package com.nic.st.util;

import com.nic.st.blocks.BlockBlueprintCreator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created by Nictogen on 4/1/18.
 */
public class Utils
{
	public static void addTexturedBoxVertices(BufferBuilder buffer, AxisAlignedBB bb, float red, float green, float blue, float alpha)
	{
		addTexturedBoxVertices(buffer, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
	}

	public static void placeVoxels(World w, BlockPos pos, EntityPlayer player)
	{
		ArrayList<BlockBlueprintCreator.TileEntityBlueprintCreator> creators = new ArrayList<>();

		if (w.getTileEntity(pos.down()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			creators.add((BlockBlueprintCreator.TileEntityBlueprintCreator) w.getTileEntity(pos.down()));
		}

		Vec3d hitVec = player.getPositionEyes(0.0f);
		Vec3d lookPos = player.getLook(0.0f);
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (BlockBlueprintCreator.TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getEmptyVoxel(BlockBlueprintCreator.HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels,
					creator.getPos());
			if (voxel != -1)
				creator.voxels[voxel] = 1;

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
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (BlockBlueprintCreator.TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getVoxel(BlockBlueprintCreator.HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels,
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
		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			if (voxels[i] != 0)
			{
				AxisAlignedBB current = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
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

	private static void addTexturedBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red,
			float green, float blue, float alpha)
	{
		buffer.pos(minX, minY, minZ).tex(0, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).tex(0, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).tex(0, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, minZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).tex(0, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, minZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).tex(0, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).tex(0, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).tex(1, 1).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).tex(1, 0).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).tex(0, 0).color(red, green, blue, alpha).endVertex();

	}

	private static int getEmptyVoxel(AxisAlignedBB bb, Vec3d origin, Vec3d end, byte[] voxels, BlockPos pos)
	{
		if (Utils.calculateFarthestIntercept(bb, origin, end) == null)
			return -1;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);

		RayTraceResult closestVoxel = null;

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
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

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
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
}
