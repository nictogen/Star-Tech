package com.nic.st.client;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nictogen on 4/1/18.
 */
public class BlueprintCreatorRenderer extends TileEntitySpecialRenderer<BlockBlueprintCreator.TileEntityBlueprintCreator>
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(StarTech.MODID, "textures/voxel.png");

	@Override
	public void render(BlockBlueprintCreator.TileEntityBlueprintCreator te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		Tessellator tessellator = Tessellator.getInstance();
		AxisAlignedBB creatorBox = new AxisAlignedBB(0, 0, 0, 1, 0.75, 1).offset(te.getPos());
		AxisAlignedBB buttonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.85, 0.1).offset(te.getPos());
		AxisAlignedBB pushedButtonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.8, 0.1).offset(te.getPos());
		AxisAlignedBB holobox = BlockHologram.HOLO_BOX.offset(te.getPos());
		Minecraft mc = Minecraft.getMinecraft();
		Vec3d hitVec = mc.player.getPositionEyes(partialTicks);
		Vec3d lookPos = mc.player.getLook(partialTicks);
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);
		int lookVoxel = Utils
				.getVoxel(BlockHologram.HOLO_BOX.offset(te.getPos()), mc.player.getPositionEyes(partialTicks), hitVec, te.voxels, te.getPos());

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

		GlStateManager.disableLighting();

		GlStateManager.disableCull();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bindTexture(TEXTURE);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		//Actual Block
		Utils.addTexturedBoxVertices(bufferbuilder, creatorBox, 0.8f, 0.8f, 0.8f, 1.0f);
		Utils.addTexturedBoxVertices(bufferbuilder, (te.buttonDown == 0 ? pushedButtonBox : buttonBox).offset(0.8, 0, 0.75), 1.0f, 0.85f, 0.0f, 1.0f);
		Utils.addTexturedBoxVertices(bufferbuilder, (te.buttonDown == 1 ? pushedButtonBox : buttonBox).offset(0.8, 0, 0.55), 0.5f, 0.5f, 0.5f, 1.0f);
		Utils.addTexturedBoxVertices(bufferbuilder, (te.buttonDown == 2 ? pushedButtonBox : buttonBox).offset(0.8, 0, 0.35), 0.3f, 0.3f, 0.3f, 1.0f);
		Utils.addTexturedBoxVertices(bufferbuilder, (te.buttonDown == 3 ? pushedButtonBox : buttonBox).offset(0.8, 0, 0.15), 0.2f, 0.4f, 1.0f, 1.0f);

		//Voxels
		Random r = new Random(123123213L);
		for (int i = 0, vX = 0, vY = 0, vZ = 0; i < te.voxels.length; i++, vX = i % 8, vY = (i % 64) / 8, vZ = i / 64)
		{
			float shade = ((float) r.nextInt(32)) * 0.001953125f;
			if (te.voxels[i] != 0)
			{
				Color color = (te.voxels[i] == 1) ?
						new Color(1.0f - shade, 0.85f - shade, 0.0f) :
						(te.voxels[i] == 2) ?
								new Color(0.5f - shade, 0.5f - shade, 0.5f - shade) :
								(te.voxels[i] == 3) ? new Color(0.3f - shade, 0.3f - shade, 0.3f - shade) : new Color(0.2f - shade, 0.4f - shade, 1.0f - shade);
				Utils.addTexturedBoxVertices(bufferbuilder,
						voxel.offset(te.getPos().getX() + vX * 0.0625 + 0.25, te.getPos().getY() + vY * 0.0625 + 1.5, te.getPos().getZ() + vZ * 0.0625),
						((float) color.getRed()) / 255f,
						((float) color.getGreen()) / 255f, ((float) color.getBlue()) / 255f,
						1.0f);
			}
		}
		tessellator.draw();

		if (lookVoxel != -1)
		{
			GlStateManager.disableTexture2D();
			RenderGlobal.drawSelectionBoundingBox(
					voxel.offset(te.getPos().getX() + (lookVoxel % 8) * 0.0625 + 0.25, te.getPos().getY() + (lookVoxel % 64 / 8) * 0.0625 + 1.5,
							te.getPos().getZ() + (lookVoxel / 64) * 0.0625)
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
		bufferbuilder.pos(creatorBox.getCenter().x, creatorBox.getCenter().y + 0.5, creatorBox.getCenter().z).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.minX, holobox.minY, holobox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.maxX, holobox.minY, holobox.maxZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		tessellator.draw();
		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(creatorBox.getCenter().x, creatorBox.getCenter().y + 0.5, creatorBox.getCenter().z).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
		bufferbuilder.pos(holobox.minX, holobox.minY, holobox.minZ).color(0.0f, 0.0f, 0.75f, 0.75f).endVertex();
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
