package com.nic.st.events;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.entity.EntityPowerRocket;
import com.nic.st.items.ItemPowerStone;
import com.nic.st.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();

			int progress = ItemPowerStone.getPowerStoneDuration(player);

			if (player.getHeldItemMainhand().getItem() instanceof ItemPowerStone)
			{
				if (!player.world.isRemote && player.ticksExisted % 20 == 0)
					shootRocket(EnumHand.MAIN_HAND, player);
				if (player.world.isRemote)
					smokeRing(player, progress);
			}

			if (player.getHeldItemOffhand().getItem() instanceof ItemPowerStone)
			{
				if (!player.world.isRemote && player.ticksExisted % 20 == 0)
					shootRocket(EnumHand.OFF_HAND, player);
				if (player.world.isRemote)
					smokeRing(player, progress);
			}
		}
	}

	private static void shootRocket(EnumHand hand, EntityPlayer player)
	{
		EntityPowerRocket rocket = new EntityPowerRocket(player.world, player);
		EnumHandSide side = (player.getPrimaryHand() == EnumHandSide.RIGHT) ?
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
		Vec3d eyes = player.getPositionEyes(0.0f);
		Vec3d rotVec = Utils.getVectorForRotation(player.rotationPitch,
				(side == EnumHandSide.RIGHT) ? player.rotationYaw + 90 : player.rotationYaw - 90);
		Vec3d offset = eyes.add(rotVec.scale(0.3)).subtract(0, 0.1, 0);
		rocket.setLocationAndAngles(offset.x, offset.y, offset.z, rocket.rotationYaw, rocket.rotationPitch);
		rocket.shoot(player, -45f - player.world.rand.nextInt(45), player.world.rand.nextInt(360), 0.0f, 0.25f, 0.5f);
		player.world.spawnEntity(rocket);
	}

	private static void smokeRing(EntityPlayer player, int progress)
	{
		World w = player.world;

		for (int y = 1; y <= 10 && y <= progress / 10; y++)
		{
			double radius = (10 - y) + Utils.d(w, 8) * Utils.p(w);
			for (int i = 0; i < 8; i++)
			{
				double deltaX = Math.cos(Math.toRadians(i * 45 + (player.ticksExisted) * 6)) * radius;
				double deltaZ = -Math.sin(Math.toRadians(i * 45 + (player.ticksExisted) * 6)) * radius;
				double finalX = player.posX + deltaX;
				double finalZ = player.posZ + deltaZ;
				Particle p = Minecraft.getMinecraft().effectRenderer
						.spawnEffectParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), finalX, player.posY + y + Utils.d(w, 4) * Utils.p(w), finalZ, 0,
								0, 0);
				if (p != null)
				{
					if (player.getRNG().nextInt(5) == 0)
					{
						p.setRBGColorF((float) Math.max(p.getRedColorF() + 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
								(float) Math.max(p.getBlueColorF() + 0.5, 0.0));
					}
					else
						p.setRBGColorF((float) Math.max(p.getRedColorF() - 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
								(float) Math.max(p.getBlueColorF() - 0.5, 0.0));

				}
			}
		}
	}


}
