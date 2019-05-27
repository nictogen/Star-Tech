package com.nic.st.client;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.util.ClientUtils;
import com.nic.st.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlueprintCreatorRenderer extends TileEntitySpecialRenderer<BlockBlueprintCreator.TileEntityBlueprintCreator>
{
	private HashMap<BlockBlueprintCreator.TileEntityBlueprintCreator, List<BakedQuad>> quadCache = new HashMap<>();

	@Override
	public void render(BlockBlueprintCreator.TileEntityBlueprintCreator te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		Tessellator tessellator = Tessellator.getInstance();

		AxisAlignedBB creatorBox = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 1, 0.9).offset(te.getPos());
		AxisAlignedBB buttonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.85, 0.1);
		AxisAlignedBB pushedButtonBox = new AxisAlignedBB(0.0, 0.75, 0.025, 0.1, 0.85, 0.1);
		AxisAlignedBB holobox = BlockHologram.HOLO_BOX.offset(te.getPos());
		Minecraft mc = Minecraft.getMinecraft();

		Vec3d hitVec = mc.player.getPositionEyes(partialTicks);
		Vec3d lookPos = mc.player.getLook(partialTicks);
		hitVec = hitVec.add(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);
		int lookVoxel = Utils.getVoxel(BlockHologram.HOLO_BOX.offset(te.getPos()), mc.player.getPositionEyes(partialTicks), hitVec, te.voxels, te.getPos());

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
		GlStateManager.disableLighting();

		GlStateManager.disableCull();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		ClientUtils.bindVoxelTexture();

		GlStateManager.pushMatrix();
		GlStateManager.translate(te.getPos().getX() + 0.75, te.getPos().getY() + 0.2, te.getPos().getZ() - 0.3);
		GlStateManager.rotate(20f, 1, 0, 0);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
		{
			ClientUtils.addTexturedBoxVertices(bufferbuilder, (te.buttonDown == i ? pushedButtonBox : buttonBox).offset(-0.1*i, 0, 0), (float) te.colors[i].getRed() / 255f,
					(float) te.colors[i].getGreen() / 255f, (float) te.colors[i].getBlue() / 255f, (float) te.colors[i].getAlpha() / 255f);
		}

		tessellator.draw();
		GlStateManager.popMatrix();

		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		//Voxels
		GlStateManager.pushMatrix();
		GlStateManager.translate(te.getPos().getX(), te.getPos().getY() + 1.5f, te.getPos().getZ() + 0.25);
		if (!quadCache.containsKey(te) || !te.useCachedModel)
		{
			quadCache.put(te, ClientUtils.createQuads(te.voxels, 1024, new int[]{}));
			te.useCachedModel = true;
		}

		for (BakedQuad bakedQuad : quadCache.get(te))
		{
			int[] data = bakedQuad.getVertexData();
			bufferbuilder.addVertexData(data);
			NBTTagCompound colorCompound = new NBTTagCompound();
			for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
			{
				colorCompound.setIntArray("color" + i,
						new int[] { te.colors[i].getRed(), te.colors[i].getGreen(), te.colors[i].getBlue(), te.colors[i].getAlpha() });
			}
			ItemStack gun = new ItemStack(StarTech.Items.printedGun);
			gun.setTagCompound(colorCompound);
			int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(gun, bakedQuad.getTintIndex());

			float cb = color & 0xFF;
			float cg = (color >>> 8) & 0xFF;
			float cr = (color >>> 16) & 0xFF;
			float ca = (color >>> 24) & 0xFF;
			VertexFormat format = DefaultVertexFormats.POSITION_TEX_COLOR;
			int size = format.getIntegerSize();
			int offset = format.getColorOffset() / 4; // assumes that color is aligned
			for (int i = 0; i < 4; i++)
			{
				int vc = data[offset + size * i];
				float vcr = vc & 0xFF;
				float vcg = (vc >>> 8) & 0xFF;
				float vcb = (vc >>> 16) & 0xFF;
				float vca = (vc >>> 24) & 0xFF;
				int ncr = Math.min(0xFF, (int) (cr * vcr / 0xFF));
				int ncg = Math.min(0xFF, (int) (cg * vcg / 0xFF));
				int ncb = Math.min(0xFF, (int) (cb * vcb / 0xFF));
				int nca = Math.min(0xFF, (int) (ca * vca / 0xFF));
				bufferbuilder.putColorRGBA(bufferbuilder.getColorIndex(4 - i), ncr, ncg, ncb, nca);
			}
		}
		tessellator.draw();
		GlStateManager.popMatrix();

		if (lookVoxel != -1)
		{
			GlStateManager.disableTexture2D();
			RenderGlobal.drawSelectionBoundingBox(
					voxel.offset(te.getPos().getX() + (lookVoxel / 64) * 0.0625, te.getPos().getY() + (lookVoxel % 64 / 8) * 0.0625 + 1.5,
							te.getPos().getZ() + (lookVoxel % 8) * 0.0625 + 0.25)
							.grow(0.001), 1.0f, 1.0f, 1.0f, 1.0f);

			GlStateManager.enableTexture2D();
		}

		//Projection

		GlStateManager.pushAttrib();
		GlStateManager.disableTexture2D();

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

		RenderGlobal.drawSelectionBoundingBox(holobox, 0.0f, 0.0f, 0.75f, 0.75f);

		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(creatorBox.minX, creatorBox.maxY, creatorBox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.minX, holobox.minY, holobox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(creatorBox.maxX, creatorBox.maxY, creatorBox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.maxX, holobox.minY, holobox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		tessellator.draw();

		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(creatorBox.minX, creatorBox.maxY, creatorBox.minZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.minX, holobox.minY, holobox.minZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(creatorBox.maxX, creatorBox.maxY, creatorBox.minZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.maxX, holobox.minY, holobox.minZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}
