package com.nic.st.client.bakedmodels;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.ArrayList;

/**
 * Created by Nictogen on 4/1/18.
 */
public class PrintedGunModelLoader implements ICustomModelLoader
{
	@Override public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.getResourceDomain().contains("printed_gun");
	}

	@Override public IModel loadModel(ResourceLocation modelLocation)
	{
		return (state, format, bakedTextureGetter) -> new PrintedGunModel(new ArrayList<>());
	}

	@Override public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}
}
