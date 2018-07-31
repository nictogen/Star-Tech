package com.nic.st.client;

import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.ClientUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

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
		if (stack != null)
		{
			return ClientUtils.createQuads(ItemPrintedGun.getGunData(stack).getByteArray("voxels"));
			//			int ammo = (int) Math.round(((double) ItemPrintedGun.getGunData(stack).getInteger("ammo")) / 250.0); TODO
		}

		return new ArrayList<>();
	}

	@Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	{
		return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
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
