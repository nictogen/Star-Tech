package com.nic.st;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.blocks.BlockPrinter;
import com.nic.st.client.BlueprintCreatorRenderer;
import com.nic.st.client.BulletRenderer;
import com.nic.st.client.PrintedGunModel;
import com.nic.st.client.PrinterRenderer;
import com.nic.st.client.gui.GuiEditCreatorButton;
import com.nic.st.entity.EntityBullet;
import com.nic.st.entity.EntityItemIndestructibleST;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.power.AbilityPowerCyclone;
import com.nic.st.power.EntityPowerRocket;
import com.nic.st.power.PowerRocketRenderer;
import com.nic.st.util.ClientUtils;
import com.nic.st.util.LimbManipulationUtil;
import lucraft.mods.lucraftcore.infinity.render.ItemRendererInfinityStone;
import lucraft.mods.lucraftcore.infinity.render.RenderEntityInfinityStone;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
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
		RenderingRegistry.registerEntityRenderingHandler(EntityPowerRocket.class, PowerRocketRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityItemIndestructibleST.class, RenderEntityInfinityStone::new);
		OBJLoader.INSTANCE.addDomain(StarTech.MODID);
		MinecraftForge.EVENT_BUS.register(new AbilityPowerCyclone.ClientHandler());
	}

	@Override public void init(FMLInitializationEvent event)
	{
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PrintedGunModel.PrintedGunColorizer(), StarTech.Items.printedGun);
	}

	@Override public void postInit(FMLPostInitializationEvent event)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(BlockBlueprintCreator.TileEntityBlueprintCreator.class, new BlueprintCreatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockPrinter.TileEntityPrinter.class, new PrinterRenderer());
	}

	@Override public void onLaserImpact(World world, double x, double y, double z, Color c)
	{
		BulletRenderer.Overlay particlefirework$overlay = new BulletRenderer.Overlay(world, x, y, z);
		particlefirework$overlay.setRBGColorF(((float) c.getRed())/255f, ((float) c.getGreen())/255f, ((float) c.getBlue())/255f);
		Minecraft.getMinecraft().effectRenderer.addEffect(particlefirework$overlay);

		for (int i = 0; i < 5; i++)
		{
			Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), x + world.rand.nextFloat() * 0.1 * (world.rand.nextBoolean() ? 1 : -1), y + world.rand.nextFloat() * 0.2, z + world.rand.nextFloat() * 0.1 * (world.rand.nextBoolean() ? 1 : -1), world.rand.nextGaussian()*0.05, 0.05, world.rand.nextGaussian()*0.05);
			Particle p = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), x + world.rand.nextFloat() * 0.1 * (world.rand.nextBoolean() ? 1 : -1), y + world.rand.nextFloat() * 0.2, z + world.rand.nextFloat() * 0.1 * (world.rand.nextBoolean() ? 1 : -1), world.rand.nextGaussian()*0.05, 0.1, world.rand.nextGaussian()*0.05);

			if (p != null)
			{
				p.setRBGColorF(1.0f,  1.0f, 1.0f);
				p.multipleParticleScaleBy(0.2f);
			}
		}

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
		StarTech.Items.powerStone.setTileEntityItemStackRenderer(new ItemRendererInfinityStone(new Color(148, 0, 211), new Color(186, 85, 211)));
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

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event)
	{
		event.getMap().registerSprite(ClientUtils.VOXEL_TEXTURE_FOR_ATLAS);
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockBlueprintCreator)
		{
			BlockBlueprintCreator.TileEntityBlueprintCreator te = (BlockBlueprintCreator.TileEntityBlueprintCreator) event.getEntity().world
					.getTileEntity(event.getPos());
			AxisAlignedBB buttonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.85, 0.1).offset(te.getPos());
			Vec3d hitVec = event.getEntityPlayer().getPositionEyes(0.0f);
			Vec3d lookPos = event.getEntityPlayer().getLook(0.0f);
			hitVec = hitVec.add(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

			boolean b = true;
			for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
			{
				if (buttonBox.offset(0.75 - i*0.1, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiEditCreatorButton(te.getPos(), 3));
					b = true;
				}
			}
			if(!b) return;
			event.setCanceled(true);
			te.markDirty();
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
		}

	}
}
