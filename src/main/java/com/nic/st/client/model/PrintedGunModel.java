package com.nic.st.client.model;

import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.ClientUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nictogen on 4/1/18.
 */
public class PrintedGunModel implements IBakedModel
{
	private static final PrintedGunOverrideList list = new PrintedGunOverrideList();
	private static HashMap<ItemStack, PrintedGunModel> cache = new HashMap<>();
	private NBTTagCompound compound;
	private List<BakedQuad> quads;

	public PrintedGunModel()
	{
		quads = new ArrayList<>();
		compound = new NBTTagCompound();
	}

	public PrintedGunModel(NBTTagCompound compound)
	{
		this.compound = compound;
		this.quads = ClientUtils.createQuads(compound.getByteArray("voxels"), (int) Math.round(((double) compound.getInteger("ammo")) / 250.0));
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		GlStateManager.disableLighting(); //This is really shitty to do but for some reason it's dark as hell without it? TODO figure it out
		GlStateManager.disableCull(); //Same thing ^
		return quads;
	}

	@Override public boolean isAmbientOcclusion()
	{
		return true;
	}

	@Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	{
		if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(0.25f, 0.35f, -0.3f), new Quat4f(0, 1, 0, 1), null, null).getMatrix());
		}
		if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(-0.25f, 0.35f, -0.3f), new Quat4f(0, 1, 0, -1), null, null).getMatrix());
		}
		if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(-0.25f, 0.35f, -0.3f), new Quat4f(0, 1, 0, -1), null, null).getMatrix());
		}
		if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(0.25f, 0.35f, -0.3f), new Quat4f(0, 1, 0, 1), null, null).getMatrix());
		}
		if (cameraTransformType == ItemCameraTransforms.TransformType.GUI)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(0.2f, 0.35f, -0.3f), new Quat4f(0.0f, 0.0f, -0.5f, 1.0f), null, null).getMatrix());
		}
		if (cameraTransformType == ItemCameraTransforms.TransformType.GROUND)
		{
			return Pair.of(this, new TRSRTransformation(new Vector3f(0.0f, 0.35f, 0.0f), new Quat4f(0, 0, 0, 1), null, null).getMatrix());
		}
		return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
	}

	public NBTTagCompound getCompound()
	{
		return compound;
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
			PrintedGunModel model = cache.get(stackIn);
			if (model == null || !ItemPrintedGun.getGunData(stackIn).equals(model.getCompound()))
			{
				cache.remove(stackIn);
				model = new PrintedGunModel(ItemPrintedGun.getGunData(stackIn));
				cache.put(stackIn, model);
				return model;
			}
			else
				return model;
		}
	}

	public static class PrintedGunModelLoader implements ICustomModelLoader
	{
		@Override public boolean accepts(ResourceLocation modelLocation)
		{
			return modelLocation.getPath().contains("printed_gun");
		}

		@Override public IModel loadModel(ResourceLocation modelLocation)
		{
			return (state, format, bakedTextureGetter) -> new PrintedGunModel();
		}

		@Override public void onResourceManagerReload(IResourceManager resourceManager)
		{
		}
	}

	public static class PrintedGunColorizer implements IItemColor
	{

		@Override public int colorMultiplier(ItemStack stack, int tintIndex)
		{
			int[] color = stack.hasTagCompound() ? stack.getTagCompound().getIntArray("color" + (tintIndex - 1)) : null;
			return (color == null || color.length == 0 ? new Color(0, 0, 0) : new Color(color[0], color[1], color[2], color[3])).getRGB();
		}
	}

}
