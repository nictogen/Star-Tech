package com.nic.st.util;

import com.google.common.primitives.Ints;
import com.nic.st.StarTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class ClientUtils
{
	private static final ResourceLocation VOXEL_TEXTURE = new ResourceLocation(StarTech.MODID, "textures/voxel.png");
	public static final ResourceLocation VOXEL_TEXTURE_FOR_ATLAS = new ResourceLocation(StarTech.MODID, "voxel");

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

	public static List<BakedQuad> createQuads(byte[] voxels, int ammo, int[] ammoNumbers)
	{
		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		ArrayList<BakedQuad> quads = new ArrayList<>();
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(VOXEL_TEXTURE_FOR_ATLAS.toString());
		int size = 8;
		Random r = new Random(123123213L);
		for (int i = voxels.length - 1, vX = 0, vY = 0, vZ = 0; i >= 0; i--, vZ = i % size, vY = (i % (size * size)) / size, vX = i / (size * size))
		{
			int tintIndex = voxels[i];
			for (int ammoNumber : ammoNumbers)
				if(ammoNumber == voxels[i] && ammo-- < 0)
					tintIndex *= -1;

//			int tintIndex = voxels[i] != 4 ? voxels[i] : (ammo-- > 0) ? 4 : 5;
			float randDarkness = r.nextFloat() / 10;
			if (voxels[i] != 0)
			{
				for (EnumFacing facing : EnumFacing.values())
				{
					if (getByteInDirection(facing, i, voxels, size, size, size) == 0)
					{
						float brightness = getFaceBrightness(facing) - randDarkness;
						AxisAlignedBB bb = voxel.offset(vX * 0.0625, vY * 0.0625, vZ * 0.0625);
						addQuad(quads, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, new Color(brightness, brightness, brightness), tintIndex, facing,
								sprite);
					}
				}
			}
		}
		return quads;
	}

	private static float getFaceBrightness(EnumFacing facing)
	{
		switch (facing)
		{
		case DOWN:
			return 0.5F;
		case UP:
			return 1.0F;
		case NORTH:
		case SOUTH:
			return 0.8F;
		case WEST:
		case EAST:
			return 0.6F;
		default:
			return 1.0F;
		}
	}

	//x = east, z = south TODO
	private static byte getByteInDirection(EnumFacing facing, int i, byte[] voxels, int x, int y, int z)
	{
		return 0;
		//		int vX = i % 8, vY = (i % 64) / 8, vZ = i / 64;
		//		switch (facing)
		//		{
		//		case DOWN:
		//			if (vY <= 0)
		//				return 0;
		//			return voxels[i - 8];
		//		case UP:
		//			if (vY >= y - 1)
		//				return 0;
		//			return voxels[i + 8];
		//		case NORTH:
		//			if (vZ <= 0)
		//				return 0;
		//			return voxels[i - 64];
		//		case SOUTH:
		//			if (vZ >= z - 1)
		//				return 0;
		//			return voxels[i + 64];
		//		case WEST:
		//			if (vX <= 0)
		//				return 0;
		//			return voxels[i + 1];
		//		case EAST:
		//			if (vX >= x - 1)
		//				return 0;
		//			return voxels[i - 1];
		//		default:
		//			return 0;
		//		}
	}

	public static void addQuad(List<BakedQuad> quads, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color,
			int tintIndex, EnumFacing facing, TextureAtlasSprite sprite)
	{

		switch (facing)
		{
		case DOWN:
			quads.add(createBakedQuad(
					new double[] { minX, minY, minZ }, 0, 0,
					new double[] { maxX, minY, minZ }, 16, 0,
					new double[] { maxX, minY, maxZ }, 16, 16,
					new double[] { minX, minY, maxZ }, 0, 16,
					color, tintIndex, EnumFacing.DOWN, sprite));
			break;
		case UP:
			quads.add(createBakedQuad(
					new double[] { minX, maxY, minZ }, 0, 0,
					new double[] { maxX, maxY, minZ }, 16, 0,
					new double[] { maxX, maxY, maxZ }, 16, 16,
					new double[] { minX, maxY, maxZ }, 0, 16,
					color, tintIndex, EnumFacing.UP, sprite));
			break;
		case NORTH:
			quads.add(createBakedQuad(
					new double[] { minX, maxY, minZ }, 0, 0,
					new double[] { maxX, maxY, minZ }, 16, 0,
					new double[] { maxX, minY, minZ }, 16, 16,
					new double[] { minX, minY, minZ }, 0, 16,
					color, tintIndex, EnumFacing.NORTH, sprite));
			break;
		case SOUTH:
			quads.add(createBakedQuad(
					new double[] { maxX, maxY, maxZ }, 0, 0,
					new double[] { minX, maxY, maxZ }, 16, 0,
					new double[] { minX, minY, maxZ }, 16, 16,
					new double[] { maxX, minY, maxZ }, 0, 16,
					color, tintIndex, EnumFacing.SOUTH, sprite));
			break;
		case EAST:
			quads.add(createBakedQuad(
					new double[] { maxX, maxY, maxZ }, 0, 0,
					new double[] { maxX, maxY, minZ }, 16, 0,
					new double[] { maxX, minY, minZ }, 16, 16,
					new double[] { maxX, minY, maxZ }, 0, 16,
					color, tintIndex, EnumFacing.EAST, sprite));
			break;
		case WEST:
			quads.add(createBakedQuad(
					new double[] { minX, maxY, maxZ }, 0, 0,
					new double[] { minX, maxY, minZ }, 16, 0,
					new double[] { minX, minY, minZ }, 16, 16,
					new double[] { minX, minY, maxZ }, 0, 16,
					color, tintIndex, EnumFacing.WEST, sprite));
			break;
		}
	}

	private static BakedQuad createBakedQuad(
			double[] p1, float u1, float v1,
			double[] p2, float u2, float v2,
			double[] p3, float u3, float v3,
			double[] p4, float u4, float v4,
			Color color, int tintIndex, EnumFacing side, TextureAtlasSprite sprite)
	{
		return new BakedQuad(Ints.concat(vertexToInts(p1[0], p1[1], p1[2], u1, v1, color, sprite),
				vertexToInts(p2[0], p2[1], p2[2], u2, v2, color, sprite),
				vertexToInts(p3[0], p3[1], p3[2], u3, v3, color, sprite),
				vertexToInts(p4[0], p4[1], p4[2], u4, v4, color, sprite)), tintIndex, side, sprite, false, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	private static int[] vertexToInts(double x, double y, double z, float u, float v, Color color, TextureAtlasSprite sprite)
	{
		return new int[] {
				Float.floatToRawIntBits((float) x),
				Float.floatToRawIntBits((float) y),
				Float.floatToRawIntBits((float) z),
				Float.floatToRawIntBits(sprite.getInterpolatedU(u)),
				Float.floatToRawIntBits(sprite.getInterpolatedV(v)),
				ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ?
						(color.getAlpha() & 0x0ff) << 24 | (color.getBlue() & 0x0ff) << 16 | (color.getGreen() & 0x0ff) << 8 | (color.getRed() & 0x0ff) :
						(color.getRed() & 0x0ff) << 24 | (color.getGreen() & 0x0ff) << 16 | (color.getBlue() & 0x0ff) << 8 | (color.getAlpha() & 0x0ff)
		};
	}

}
