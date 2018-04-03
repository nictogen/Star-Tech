package com.nic.st.client;

import com.nic.st.blocks.BlockPrinter;
import com.nic.st.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemAir;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class PrinterRenderer extends TileEntitySpecialRenderer<BlockPrinter.TileEntityPrinter>
{

	@Override
	public void render(BlockPrinter.TileEntityPrinter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		if (!(te.blueprint.getItem() instanceof ItemAir))
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.45, 0.4, 0.65);

			//Don't ask about this idfk \/
			GlStateManager.rotate(90f, 0f, 0f, 0f);

			GlStateManager.rotate(-32f, 0f, 0f, 1f);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.blueprint, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
		}

		if (!te.gun.isEmpty())
		{
			GlStateManager.disableLighting();
			GlStateManager.disableCull();

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.45, 0.5);
			GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			Minecraft.getMinecraft().renderEngine.bindTexture(BlueprintCreatorRenderer.TEXTURE);

			AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
			Random r = new Random(123123213L);

			byte[] voxels = te.gun.getTagCompound().getByteArray("voxels");
			int amountToRender = te.ticks / 200;
			for (int i = voxels.length - 1, vX = 0, vY = 0, vZ = 0; i >= 0; i--, vX = i % 8, vY = (i % 64) / 8, vZ = i / 64)
			{
				float shade = ((float) r.nextInt(32)) * 0.001953125f;
				if (voxels[i] != 0 && (amountToRender-- > 0 || te.ticks == 0))
				{
					Color color = (voxels[i] == 1) ?
							new Color(1.0f - shade, 0.85f - shade, 0.0f) :
							(voxels[i] == 2) ?
									new Color(0.5f - shade, 0.5f - shade, 0.5f - shade) :
									(voxels[i] == 3) ?
											new Color(0.3f - shade, 0.3f - shade, 0.3f - shade) :
											new Color(0.2f - shade, 0.4f - shade, 1.0f - shade);
					ClientUtils.addTexturedBoxVertices(bufferbuilder,
							voxel.offset(vX * 0.0625, vY * 0.0625, vZ * 0.0625), ((float) color.getRed()) / 255f,
							((float) color.getGreen()) / 255f, ((float) color.getBlue()) / 255f,
							1.0f);
				}
			}
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}

		GlStateManager.pushAttrib();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		if (te.ticks > 0)
		{
			double pos = ((double) te.getWorld().getTotalWorldTime()) % 80.0;
			if (pos > 40)
				pos = 40 - (pos - 40);

			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.pos(1, 0.65, 0.75).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(0.325 + pos * 0.008, 0.4, 0.6).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(0.325 + pos * 0.008, 0.4, 0.9).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			tessellator.draw();

			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.pos(0, 0.45 + pos * 0.012, 0).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(1, 0.45 + pos * 0.012, 0).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(1, 0.45 + pos * 0.012, 0.5).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(0, 0.45 + pos * 0.012, 0.5).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			tessellator.draw();
		}
		GlStateManager.disableBlend();

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();

		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}
