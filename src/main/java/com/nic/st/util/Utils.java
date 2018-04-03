package com.nic.st.util;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 4/1/18.
 */
public class Utils
{
	public static RayTraceResult calculateFurthestIntercept(AxisAlignedBB bb, Vec3d origin, Vec3d end)
	{
		Vec3d vec3d = collideWithXPlane(bb, bb.minX, origin, end);
		EnumFacing enumfacing = EnumFacing.WEST;
		Vec3d vec3d1 = collideWithXPlane(bb, bb.maxX, origin, end);

		if (vec3d1 != null && isFarthest(origin, vec3d, vec3d1))
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.EAST;
		}

		vec3d1 = collideWithYPlane(bb, bb.minY, origin, end);

		if (vec3d1 != null && isFarthest(origin, vec3d, vec3d1))
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.DOWN;
		}

		vec3d1 = collideWithYPlane(bb, bb.maxY, origin, end);

		if (vec3d1 != null && isFarthest(origin, vec3d, vec3d1))
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.UP;
		}

		vec3d1 = collideWithZPlane(bb, bb.minZ, origin, end);

		if (vec3d1 != null && isFarthest(origin, vec3d, vec3d1))
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.NORTH;
		}

		vec3d1 = collideWithZPlane(bb, bb.maxZ, origin, end);

		if (vec3d1 != null && isFarthest(origin, vec3d, vec3d1))
		{
			vec3d = vec3d1;
			enumfacing = EnumFacing.SOUTH;
		}

		return vec3d == null ? null : new RayTraceResult(vec3d, enumfacing);
	}

	private static Vec3d collideWithXPlane(AxisAlignedBB bb, double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
	{
		Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
		return vec3d != null && intersectsWithYZ(bb, vec3d) ? vec3d : null;
	}

	private static Vec3d collideWithYPlane(AxisAlignedBB bb, double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
	{
		Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
		return vec3d != null && intersectsWithXZ(bb, vec3d) ? vec3d : null;
	}

	private static Vec3d collideWithZPlane(AxisAlignedBB bb, double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
	{
		Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
		return vec3d != null && intersectsWithXY(bb, vec3d) ? vec3d : null;
	}

	@VisibleForTesting
	private static boolean intersectsWithYZ(AxisAlignedBB bb, Vec3d vec)
	{
		return vec.y >= bb.minY && vec.y <= bb.maxY && vec.z >= bb.minZ && vec.z <= bb.maxZ;
	}

	@VisibleForTesting
	private static boolean intersectsWithXZ(AxisAlignedBB bb, Vec3d vec)
	{
		return vec.x >= bb.minX && vec.x <= bb.maxX && vec.z >= bb.minZ && vec.z <= bb.maxZ;
	}

	@VisibleForTesting
	private static boolean intersectsWithXY(AxisAlignedBB bb, Vec3d vec)
	{
		return vec.x >= bb.minX && vec.x <= bb.maxX && vec.y >= bb.minY && vec.y <= bb.maxY;
	}

	public static boolean isFarthest(Vec3d origin, @Nullable Vec3d furthest, Vec3d checking)
	{
		return furthest == null || origin.squareDistanceTo(checking) > origin.squareDistanceTo(furthest);
	}

	@VisibleForTesting
	public static boolean isClosest(Vec3d origin, @Nullable Vec3d furthest, Vec3d checking)
	{
		return furthest == null || origin.squareDistanceTo(checking) < origin.squareDistanceTo(furthest);
	}

	public static void renderTexturedBox(AxisAlignedBB bb, float red, float green, float blue, float alpha)
	{
		renderTexturedBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
	}

	public static void renderTexturedBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue,
			float alpha)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		addTexturedBoxVertices(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
		tessellator.draw();
	}

	public static void addTexturedBoxVertices(BufferBuilder buffer, AxisAlignedBB bb, float red, float green, float blue, float alpha)
	{
		addTexturedBoxVertices(buffer, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
	}

	public static void addTexturedBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red,
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
}
