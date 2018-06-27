package com.nic.st;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.blocks.BlockPrinter;
import com.nic.st.client.BlueprintCreatorRenderer;
import com.nic.st.client.BulletRenderer;
import com.nic.st.client.PrintedGunModel;
import com.nic.st.client.PrinterRenderer;
import com.nic.st.entity.EntityBullet;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.LimbManipulationUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nictogen on 4/3/18.
 */
public class ClientProxy extends CommonProxy
{
	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post event)
	{
		@SuppressWarnings("rawtypes") RenderLivingBase renderer = (RenderLivingBase) Minecraft
				.getMinecraft().getRenderManager().getEntityRenderObject(event.getEntityPlayer());
		List<LayerRenderer<AbstractClientPlayer>> layerList = ReflectionHelper
				.getPrivateValue(RenderLivingBase.class, renderer, 4);
		try
		{
			for (LayerRenderer<AbstractClientPlayer> layer : layerList)
			{
				for (Field field : layer.getClass().getDeclaredFields())
				{
					field.setAccessible(true);
					if (field.getType() == ModelBiped.class)
					{
						for (ModelRenderer modelRenderer : ((ModelBiped) field
								.get(layer)).boxList)
						{
							if (modelRenderer instanceof LimbManipulationUtil.CustomModelRenderer)
							{
								((LimbManipulationUtil.CustomModelRenderer) modelRenderer).reset();
							}
						}
					}
					else if (field.getType() == ModelPlayer.class)
					{
						for (ModelRenderer modelRenderer : ((ModelBiped) field
								.get(layer)).boxList)
						{
							if (modelRenderer instanceof LimbManipulationUtil.CustomModelRenderer)
							{
								((LimbManipulationUtil.CustomModelRenderer) modelRenderer).reset();
							}
						}
					}
				}
			}
			for (ModelRenderer modelRenderer : event.getRenderer().getMainModel().boxList)
			{
				if (modelRenderer instanceof LimbManipulationUtil.CustomModelRenderer)
				{
					((LimbManipulationUtil.CustomModelRenderer) modelRenderer).reset();
				}
			}
		}
		catch (IllegalAccessException ignored)
		{
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

	@SubscribeEvent public void registerModels(ModelRegistryEvent ev)
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

	@SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
	{
		EntityPlayer player = event.getEntityPlayer();

		ArrayList<LimbManipulationUtil.Limb> limbs = new ArrayList<>();

		float f = LimbManipulationUtil.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());
		float f1 = LimbManipulationUtil.interpolateRotation(player.prevRotationYawHead, player.rotationYawHead, event.getPartialRenderTick());
		float f2 = f1 - f;

		if (player.getHeldItemMainhand().getItem() instanceof ItemPrintedGun)
		{
			limbs.add(
					event.getEntityPlayer().getPrimaryHand() == EnumHandSide.RIGHT ? LimbManipulationUtil.Limb.RIGHT_ARM : LimbManipulationUtil.Limb.LEFT_ARM);
		}
		if (player.getHeldItemOffhand().getItem() instanceof ItemPrintedGun)
		{
			limbs.add(
					event.getEntityPlayer().getPrimaryHand() != EnumHandSide.RIGHT ? LimbManipulationUtil.Limb.RIGHT_ARM : LimbManipulationUtil.Limb.LEFT_ARM);

		}

		for (LimbManipulationUtil.Limb limb : limbs)
		{
			LimbManipulationUtil.getLimbManipulator(event.getRenderer(), limb).setAngles(event.getEntityPlayer().rotationPitch - 90, f2, 0);
		}

	}

	@SubscribeEvent
	public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event)
	{
		if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos())
				.getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
		}
	}
}
