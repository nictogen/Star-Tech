package com.nic.st;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.client.bakedmodels.PrintedGunModel;
import com.nic.st.client.bakedmodels.PrintedGunModelLoader;
import com.nic.st.client.tesr.BlueprintCreatorRenderer;
import com.nic.st.items.ItemPrintedGun;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Field;

@Mod(modid = StarTech.MODID, name = StarTech.NAME, version = StarTech.VERSION)
@Mod.EventBusSubscriber
public class StarTech
{
	public static final String MODID = "star-tech";
	public static final String NAME = "Star Tech, Man! The Legendary Mod?";
	public static final String VERSION = "1.0";

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockBlueprintCreator());
		event.getRegistry().register(new BlockHologram());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) throws IllegalAccessException
	{
		event.getRegistry().register(new ItemPrintedGun());

		for (Field field : Blocks.class.getDeclaredFields())
		{
			Block block = (Block) field.get(null);
			Item itemBlock = new ItemBlock(block).setRegistryName(block.getRegistryName()).setUnlocalizedName(block.getUnlocalizedName());
			event.getRegistry().register(itemBlock);
		}
	}

	@SubscribeEvent public static void registerModels(ModelRegistryEvent ev)
	{
		for (Field f : Items.class.getDeclaredFields())
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

		for (Field f : Blocks.class.getDeclaredFields())
		{
			try
			{
				Block block = (Block) f.get(null);
				Item item = Item.getItemFromBlock(block);
				ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			}
			catch (IllegalAccessException | ClassCastException e)
			{
				throw new RuntimeException("Incorrect field in item sub-class", e);
			}
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModelLoaderRegistry.registerLoader(new PrintedGunModelLoader());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GameRegistry.registerTileEntity(BlockBlueprintCreator.TileEntityBlueprintCreator.class, "star-tech:blueprint_creator");
	}

	@EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		if (event.getSide().isClient())
		{
			ClientRegistry.bindTileEntitySpecialRenderer(BlockBlueprintCreator.TileEntityBlueprintCreator.class, new BlueprintCreatorRenderer());
		}
	}

	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event)
	{
		event.getModelRegistry().putObject(new ModelResourceLocation(Items.printedGun.getRegistryName(), "inventory"), new PrintedGunModel());
	}

	@GameRegistry.ObjectHolder(MODID)
	public static class Blocks
	{
		@GameRegistry.ObjectHolder("blueprint_creator")
		public static final Block blueprintCreator = null;

		public static final Block hologram = null;
	}

	@GameRegistry.ObjectHolder(MODID)
	public static class Items
	{
		@GameRegistry.ObjectHolder("printed_gun")
		public static final Item printedGun = null;

		@GameRegistry.ObjectHolder("blueprint_creator")
		public static final Item blueprintCreator = null;
	}

}
