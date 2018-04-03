package com.nic.st.events;

import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.LimbManipulationUtil;
import com.nic.st.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

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
		else if (event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockBlueprintCreator)
		{
			BlockBlueprintCreator.TileEntityBlueprintCreator te = (BlockBlueprintCreator.TileEntityBlueprintCreator) event.getEntity().world
					.getTileEntity(event.getPos());
			AxisAlignedBB buttonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.85, 0.1).offset(te.getPos());
			Vec3d hitVec = event.getEntityPlayer().getPositionEyes(0.0f);
			Vec3d lookPos = event.getEntityPlayer().getLook(0.0f);
			hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

			if (buttonBox.offset(0.8, 0, 0.75).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 0;
			}
			else if (buttonBox.offset(0.8, 0, 0.55).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 1;
			}
			else if (buttonBox.offset(0.8, 0, 0.35).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 2;
			}
			else if (buttonBox.offset(0.8, 0, 0.15).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 3;
			}
			else
				return;
			event.setCanceled(true);
			te.markDirty();
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
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
			ItemPrintedGun
					.createGunData(((BlockBlueprintCreator.TileEntityBlueprintCreator) event.getWorld().getTileEntity(event.getPos().down())).voxels.clone(),
							event.getEntityPlayer().getHeldItem(event.getHand()));
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
		if (event.getTarget().getBlockPos() != null && event.getPlayer().world
				.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event)
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
}
