package com.nic.st.events;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
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
		else if (event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockBlueprintCreator)
		{
			BlockBlueprintCreator.TileEntityBlueprintCreator te = (BlockBlueprintCreator.TileEntityBlueprintCreator) event.getEntity().world
					.getTileEntity(event.getPos());
			AxisAlignedBB buttonBox = new AxisAlignedBB(0.0, 0.75, 0, 0.1, 0.85, 0.1).offset(te.getPos());
			Vec3d hitVec = event.getEntityPlayer().getPositionEyes(0.0f);
			Vec3d lookPos = event.getEntityPlayer().getLook(0.0f);
			hitVec = hitVec.add(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

			boolean exit = true;
			for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES && exit; i++)
			{
				if (buttonBox.offset(0.75 - 0.1*i, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
				{
					te.buttonDown = i;
					exit = false;
				}
			}
			if(exit) return;
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
		if (!(event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram))
			return;
		TileEntity te = event.getWorld().getTileEntity(event.getPos().down());

		if (event.getHand() == EnumHand.MAIN_HAND && event.getEntityPlayer().isSneaking() && te instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			ItemStack stack = new ItemStack(StarTech.Items.blueprint);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setByteArray("voxels", ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).voxels.clone());


			ItemPrintedGun.GunStats gunStats = new ItemPrintedGun.GunStats(((BlockBlueprintCreator.TileEntityBlueprintCreator) te).voxels, ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).uses);

			if(gunStats.isValid())
			{
				for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
				{
					compound.setIntArray("color" + i, new int[] { ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[i].getRed(),
							((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[i].getGreen(),
							((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[i].getBlue(),
							((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[i].getAlpha() });
				}

				int[] uses = new int[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES];
				for (int i = 0; i < uses.length; i++)
				{
					uses[i] = ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).uses[i].ordinal();
				}
				compound.setIntArray("uses", uses);

				compound.setInteger("total", gunStats.totalVoxels);

				stack.setTagCompound(compound);
				event.getEntityPlayer().addItemStackToInventory(stack);
			} else {
				event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("Structure voxels must make up at least 25% of your weapon."), true); //TODO translate
			}
		}
		else if (event.getHand() == EnumHand.MAIN_HAND)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				Utils.placeVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
		}
	}

}
