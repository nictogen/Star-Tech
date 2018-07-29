package com.nic.st.events;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.entity.EntityPowerRocket;
import com.nic.st.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
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
			hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

			if (buttonBox.offset(0.45, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 0;
			}
			else if (buttonBox.offset(0.55, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 1;
			}
			else if (buttonBox.offset(0.65, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
			{
				te.buttonDown = 2;
			}
			else if (buttonBox.offset(0.75, 0.15, -0.05).calculateIntercept(event.getEntityPlayer().getPositionEyes(0.0f), hitVec) != null)
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
		if (!(event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram))
			return;

		if (event.getHand() == EnumHand.MAIN_HAND && event.getEntityPlayer().isSneaking() && event.getWorld()
				.getTileEntity(event.getPos().down()) instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			ItemStack stack = new ItemStack(StarTech.Items.blueprint);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setByteArray("voxels",
					((BlockBlueprintCreator.TileEntityBlueprintCreator) event.getWorld().getTileEntity(event.getPos().down())).voxels.clone());
			int total = 0;
			for (byte voxels : compound.getByteArray("voxels"))
			{
				if (voxels != 0)
					total++;
			}
			compound.setInteger("total", total);
			stack.setTagCompound(compound);

			event.getEntityPlayer().addItemStackToInventory(stack);
		}
		else if (event.getHand() == EnumHand.MAIN_HAND)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				Utils.placeVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public static void onUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayer && event.getEntity().ticksExisted % 20 == 0)
		{
			EntityPowerRocket rocket = new EntityPowerRocket(event.getEntity().world, event.getEntityLiving());
			EnumHand handIn = EnumHand.MAIN_HAND;
			EnumHandSide side = (((EntityPlayer) event.getEntity()).getPrimaryHand() == EnumHandSide.RIGHT) ?
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
					(handIn == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
			Vec3d eyes = event.getEntity().getPositionEyes(0.0f);
			Vec3d rotVec = getVectorForRotation(event.getEntity().rotationPitch,
					(side == EnumHandSide.RIGHT) ? event.getEntity().rotationYaw + 90 : event.getEntity().rotationYaw - 90);
			Vec3d offset = eyes.add(rotVec.scale(0.3)).subtract(0, 0.1, 0);
			rocket.setLocationAndAngles(offset.x, offset.y, offset.z, rocket.rotationYaw, rocket.rotationPitch);
			rocket.shoot(event.getEntity(), -45f - event.getEntity().world.rand.nextInt(45), event.getEntity().world.rand.nextInt(360), 0.0f, 0.25f, 0.5f);
			event.getEntity().world.spawnEntity(rocket);
		}
	}

	private static Vec3d getVectorForRotation(float pitch, float yaw)
	{
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
	}


}
