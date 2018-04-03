package com.nic.st.client;

import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.Utils;
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
		GlStateManager.translate(-0.25f, 0f, 0.9f);

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

	//TODO figure out culling when it's an EntityItem
	private void render()
	{
		if (stack != null)
		{
			byte[] voxels = ItemPrintedGun.getGunData(stack).getByteArray("voxels");
			Random r = new Random(123123213L);
			AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
			Minecraft.getMinecraft().renderEngine.bindTexture(BlueprintCreatorRenderer.TEXTURE);

			GlStateManager.disableLighting();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

			for (int i = 0, vX = 0, vY = 0, vZ = 0; i < voxels.length; i++, vX = i % 8, vY = (i % 64) / 8, vZ = i / 64)
			{
				float shade = ((float) r.nextInt(32)) * 0.001953125f;
				if (voxels[i] != 0)
				{
					Utils.addTexturedBoxVertices(bufferbuilder,
							voxel.offset(vX * 0.0625, vY * 0.0625, -vZ * 0.0625), 0.3f,
							0.3f, 0.8f - shade,
							1.0f);
				}

			}
			tessellator.draw();
			GlStateManager.enableLighting();
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
