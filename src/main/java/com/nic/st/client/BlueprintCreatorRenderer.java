package com.nic.st.client;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
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
		Random r = new Random(123123213L);
		Minecraft mc = Minecraft.getMinecraft();
		Vec3d hitVec = mc.player.getPositionEyes(partialTicks);
		Vec3d lookPos = mc.player.getLook(partialTicks);
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);
		int lookVoxel = Utils
				.getVoxel(BlockBlueprintCreator.HOLO_BOX.offset(te.getPos()), mc.player.getPositionEyes(partialTicks), hitVec, te.voxels, te.getPos());

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		RenderGlobal.drawSelectionBoundingBox(BlockBlueprintCreator.HOLO_BOX.offset(te.getPos()), 1.0f, 1.0f, 1.0f, 1.0f);

		GlStateManager.enableTexture2D();
		bindTexture(TEXTURE);
		GlStateManager.disableCull();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		for (int i = 0, vX = 0, vY = 0, vZ = 0; i < te.voxels.length; i++, vX = i % 8, vY = (i % 64) / 8, vZ = i / 64)
		{
			float shade = ((float) r.nextInt(32)) * 0.001953125f;
			if (te.voxels[i] != 0)
			{
				Utils.addTexturedBoxVertices(bufferbuilder,
						voxel.offset(te.getPos().getX() + vX * 0.0625 + 0.25, te.getPos().getY() + vY * 0.0625 + 1, te.getPos().getZ() + vZ * 0.0625), 0.3f,
						0.3f, 0.8f - shade,
						1.0f);
			}
		}
		tessellator.draw();

		if (lookVoxel != -1)
		{
			GlStateManager.disableTexture2D();
			RenderGlobal.drawSelectionBoundingBox(
					voxel.offset(te.getPos().getX() + (lookVoxel % 8) * 0.0625 + 0.25, te.getPos().getY() + (lookVoxel % 64 / 8) * 0.0625 + 1,
							te.getPos().getZ() + (lookVoxel / 64) * 0.0625)
							.grow(0.001), 1.0f, 1.0f, 1.0f, 1.0f);

			GlStateManager.enableTexture2D();
		}

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
