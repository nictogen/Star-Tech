package com.nic.st.client;

import com.nic.st.blocks.BlockPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

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

		if (!te.gun.isEmpty())
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.45, 0.5);
			GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f);
			GlStateManager.translate(0, 0, 0.5);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.gun, ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
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
			double pos = ((double) te.getWorld().getTotalWorldTime()) % 110.0;
			if (pos > 55)
				pos = 55 - (pos - 55);

			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.pos(-0.22 + pos * 0.015, 0.175, 0.22).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(0.72, 0.5, 0.42).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(0.72, 0.5, 0.58).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
			bufferBuilder.pos(-0.22 + pos * 0.015, 0.175, 0.78).color(0.0f, 0.0f, 1.0f, 0.5f).endVertex();
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
