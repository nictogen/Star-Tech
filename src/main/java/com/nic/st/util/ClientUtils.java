package com.nic.st.util;

import com.nic.st.StarTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by Nictogen on 4/3/18.
 */
public class ClientUtils
{
	private static final ResourceLocation VOXEL_TEXTURE = new ResourceLocation(StarTech.MODID, "textures/voxel.png");

	public static void addTexturedBoxVertices(BufferBuilder buffer, AxisAlignedBB bb, float red, float green, float blue, float alpha)
	{
		addTexturedBoxVertices(buffer, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
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

	public static void bindVoxelTexture()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(VOXEL_TEXTURE);
	}
}
