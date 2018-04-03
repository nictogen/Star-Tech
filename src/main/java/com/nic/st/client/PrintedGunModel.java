package com.nic.st.client;

import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.ClientUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nictogen on 4/1/18.
 */
public class PrintedGunModel implements IBakedModel
{
	public static ItemStack stack = null;
	private static final PrintedGunOverrideList list = new PrintedGunOverrideList();

	public PrintedGunModel()
	{
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		tessellator.draw();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.rotate(180f, 1f, 0f, 0f);
		GlStateManager.translate(-0.25f, -0.1f, 0.9f);

		render();
		GlStateManager.popMatrix();
		stack = null;
		builder.begin(7, DefaultVertexFormats.ITEM);
		return new ArrayList<>();
	}

	@Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	{
		return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
	}

	private void render()
	{
		if (stack != null)
		{
			byte[] voxels = ItemPrintedGun.getGunData(stack).getByteArray("voxels");
			Random r = new Random(123123213L);
			AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
			Minecraft.getMinecraft().renderEngine.bindTexture(BlueprintCreatorRenderer.TEXTURE);

			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

			int ammo = (int) Math.round(((double) ItemPrintedGun.getGunData(stack).getInteger("ammo")) / 250.0);

			for (int i = voxels.length - 1, vX = 0, vY = 0, vZ = 0; i >= 0; i--, vX = i % 8, vY = (i % 64) / 8, vZ = i / 64)
			{
				float shade = ((float) r.nextInt(32)) * 0.001953125f;
				if (voxels[i] != 0)
				{
					Color color = (voxels[i] == 1) ?
							new Color(1.0f - shade, 0.85f - shade, 0.0f) :
							(voxels[i] == 2) ?
									new Color(0.5f - shade, 0.5f - shade, 0.5f - shade) :
									(voxels[i] == 3) ?
											new Color(0.3f - shade, 0.3f - shade, 0.3f - shade) :
											(ammo-- > 0) ?
													new Color(0.2f - shade, 0.4f - shade, 1.0f - shade) : new Color(0.3f - shade, 0.3f - shade, 0.5f - shade);
					ClientUtils.addTexturedBoxVertices(bufferbuilder,
							voxel.offset(vX * 0.0625, vY * 0.0625, -vZ * 0.0625), ((float) color.getRed()) / 255f,
							((float) color.getGreen()) / 255f, ((float) color.getBlue()) / 255f,
							1.0f);
				}
			}
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}
	}

	@Override public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override public boolean isGui3d()
	{
		return false;
	}

	@Override public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override public TextureAtlasSprite getParticleTexture()
	{
		return null;
	}

	@Override public ItemOverrideList getOverrides()
	{
		return list;
	}

	public static class PrintedGunOverrideList extends ItemOverrideList
	{
		public PrintedGunOverrideList()
		{
			super(new ArrayList<>());
		}

		@Override public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stackIn, @Nullable World world, @Nullable EntityLivingBase entity)
		{
			stack = stackIn;
			return originalModel;
		}
	}

	public static class PrintedGunModelLoader implements ICustomModelLoader
	{
		@Override public boolean accepts(ResourceLocation modelLocation)
		{
			return modelLocation.getResourcePath().contains("printed_gun");
		}

		@Override public IModel loadModel(ResourceLocation modelLocation)
		{
			return (state, format, bakedTextureGetter) -> new PrintedGunModel();
		}

		@Override public void onResourceManagerReload(IResourceManager resourceManager)
		{
		}
	}

}
