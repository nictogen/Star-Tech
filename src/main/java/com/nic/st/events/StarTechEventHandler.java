package com.nic.st.events;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.Utils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Nictogen on 4/2/18.
 */
@Mod.EventBusSubscriber
public class StarTechEventHandler
{
	@SubscribeEvent
	public static void onPunch(PlayerInteractEvent.LeftClickBlock event)
	{
		if (event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				Utils.breakVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public static void onPunch(PlayerEvent.HarvestCheck event)
	{
		if (event.getTargetBlock().getBlock() instanceof BlockHologram)
		{
			event.setCanHarvest(false);
		}
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getEntityPlayer().getHeldItem(event.getHand()).getItem() instanceof ItemPrintedGun && event.getWorld()
				.getTileEntity(event.getPos().down()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			ItemPrintedGun.getGunData(event.getEntityPlayer().getHeldItem(event.getHand()))
					.setByteArray("voxels",
							((BlockBlueprintCreator.TileEntityBlueprintCreator) event.getWorld().getTileEntity(event.getPos().down())).voxels.clone());
			return;
		}

		if (event.getHand() == EnumHand.MAIN_HAND && event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				Utils.placeVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public static void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event)
	{
		if (!event.getPlayer().world.isOutsideBuildHeight(event.getTarget().getBlockPos()) && event.getPlayer().world
				.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
		}
	}
}
