package com.nic.st.client.bakedmodels;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Nictogen on 4/1/18.
 */
public class PrintedGunModel implements IBakedModel
{
	private static final PrintedGunOverrideList list = new PrintedGunOverrideList();

	private List<BakedQuad> quads;

	public PrintedGunModel(List<BakedQuad> quads)
	{
		this.quads = quads;
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		return quads;
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
}
