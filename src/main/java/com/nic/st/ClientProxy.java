package com.nic.st;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockPrinter;
import com.nic.st.client.BlueprintCreatorRenderer;
import com.nic.st.client.BulletRenderer;
import com.nic.st.client.PrintedGunModel;
import com.nic.st.client.PrinterRenderer;
import com.nic.st.entity.EntityBullet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;

/**
 * Created by Nictogen on 4/3/18.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@SubscribeEvent public static void registerModels(ModelRegistryEvent ev)
	{
		for (Field f : StarTech.Items.class.getDeclaredFields())
		{
			try
			{
				Item item = (Item) f.get(null);
				ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			}
			catch (IllegalAccessException | ClassCastException e)
			{
				throw new RuntimeException("Incorrect field in item sub-class", e);
			}
		}

		for (Field f : StarTech.Blocks.class.getDeclaredFields())
		{
			try
			{
				Block block = (Block) f.get(null);
				Item item = Item.getItemFromBlock(block);
				ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName() + (f.isAnnotationPresent(StarTech.OBJ.class) ? ".obj" : ""),
						"inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			}
			catch (IllegalAccessException | ClassCastException e)
			{
				throw new RuntimeException("Incorrect field in item sub-class", e);
			}
		}
	}

	@Override public void preInit(FMLPreInitializationEvent event)
	{
		ModelLoaderRegistry.registerLoader(new PrintedGunModel.PrintedGunModelLoader());
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, BulletRenderer::new);
		OBJLoader.INSTANCE.addDomain(StarTech.MODID);
	}

	@Override public void postInit(FMLPostInitializationEvent event)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(BlockBlueprintCreator.TileEntityBlueprintCreator.class, new BlueprintCreatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockPrinter.TileEntityPrinter.class, new PrinterRenderer());
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(StarTech.Blocks.blueprintCreator), 0,
				BlockBlueprintCreator.TileEntityBlueprintCreator.class);
	}

	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event)
	{
		//		event.getModelRegistry().putObject(new ModelResourceLocation(StarTech.Items.printedGun.getRegistryName(), "inventory"), new PrintedGunModel());
	}
}
