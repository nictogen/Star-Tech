package com.nic.st.blocks;

import com.nic.st.StarTech;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created by Nictogen on 4/1/18.
 */
@Mod.EventBusSubscriber
public class BlockBlueprintCreator extends Block implements ITileEntityProvider
{
	public static final AxisAlignedBB HOLO_BOX = new AxisAlignedBB(0.25, 1, 0, 0.75, 1.5, 1);

	public BlockBlueprintCreator()
	{
		super(Material.IRON);
		setRegistryName(StarTech.MODID, "blueprint_creator");
		setUnlocalizedName("blueprint_creator");
	}

	@SubscribeEvent
	public static void onPunch(PlayerInteractEvent.LeftClickBlock event)
	{
		if (event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				breakVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
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
				.getTileEntity(event.getPos().down()) instanceof TileEntityBlueprintCreator)
		{
			ItemPrintedGun.getGunData(event.getEntityPlayer().getHeldItem(event.getHand()))
					.setByteArray("voxels", ((TileEntityBlueprintCreator) event.getWorld().getTileEntity(event.getPos().down())).voxels.clone());
			return;
		}

		if (event.getHand() == EnumHand.MAIN_HAND && event.getEntity().world.getBlockState(event.getPos()).getBlock() instanceof BlockHologram)
		{
			event.setCanceled(true);
			if (!event.getWorld().isRemote)
				placeVoxels(event.getWorld(), event.getPos(), event.getEntityPlayer());
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

	public static void placeVoxels(World w, BlockPos pos, EntityPlayer player)
	{
		ArrayList<TileEntityBlueprintCreator> creators = new ArrayList<>();

		if (w.getTileEntity(pos.down()) instanceof TileEntityBlueprintCreator)
		{
			creators.add((TileEntityBlueprintCreator) w.getTileEntity(pos.down()));
		}

		Vec3d hitVec = player.getPositionEyes(0.0f);
		Vec3d lookPos = player.getLook(0.0f);
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getEmptyVoxel(HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels, creator.getPos());
			if (voxel != -1)
				creator.voxels[voxel] = 1;

			creator.markDirty();
			IBlockState state = creator.getWorld().getBlockState(creator.getPos());
			creator.getWorld().notifyBlockUpdate(creator.getPos(), state, state, 3);
		}
	}

	public static void breakVoxels(World w, BlockPos pos, EntityPlayer player)
	{
		ArrayList<TileEntityBlueprintCreator> creators = new ArrayList<>();

		if (w.getTileEntity(pos.down()) instanceof TileEntityBlueprintCreator)
		{
			creators.add((TileEntityBlueprintCreator) w.getTileEntity(pos.down()));
		}
		if (w.getTileEntity(pos.down().north()) instanceof TileEntityBlueprintCreator)
		{
			creators.add((TileEntityBlueprintCreator) w.getTileEntity(pos.down().north()));
		}
		if (w.getTileEntity(pos.down().south()) instanceof TileEntityBlueprintCreator)
		{
			creators.add((TileEntityBlueprintCreator) w.getTileEntity(pos.down().south()));
		}

		Vec3d hitVec = player.getPositionEyes(0.0f);
		Vec3d lookPos = player.getLook(0.0f);
		hitVec = hitVec.addVector(lookPos.x * 5, lookPos.y * 5, lookPos.z * 5);

		for (TileEntityBlueprintCreator creator : creators)
		{
			int voxel = getVoxel(HOLO_BOX.offset(creator.getPos()), player.getPositionEyes(0.0f), hitVec, creator.voxels,
					creator.getPos());
			if (voxel != -1)
				creator.voxels[voxel] = 0;

			creator.markDirty();
			IBlockState state = creator.getWorld().getBlockState(creator.getPos());
			creator.getWorld().notifyBlockUpdate(creator.getPos(), state, state, 3);
		}
	}

	public static int getVoxel(AxisAlignedBB bb, Vec3d origin, Vec3d end, byte[] voxels, BlockPos pos)
	{
		RayTraceResult result = Utils.calculateFurthestIntercept(bb, origin, end);

		if (result == null)
			return -1;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);
		Vec3d closest = null;
		int closestIndex = -1;
		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			if (voxels[i] != 0)
			{
				AxisAlignedBB current = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
				RayTraceResult currentResult = current.calculateIntercept(origin, end);
				if (currentResult != null)
				{
					if (Utils.isClosest(origin, closest, currentResult.hitVec))
					{
						closest = currentResult.hitVec;
						closestIndex = i;
					}
				}
			}
		}

		return closestIndex;
	}

	public static int getEmptyVoxel(AxisAlignedBB bb, Vec3d origin, Vec3d end, byte[] voxels, BlockPos pos)
	{
		if (Utils.calculateFurthestIntercept(bb, origin, end) == null)
			return -1;

		AxisAlignedBB voxel = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);

		RayTraceResult closestVoxel = null;

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
			if (voxels[i] != 0)
			{
				RayTraceResult cvResult = voxelPos.calculateIntercept(origin, end);
				if (cvResult != null)
				{
					if (Utils.isClosest(origin, closestVoxel == null ? null : closestVoxel.hitVec, cvResult.hitVec))
					{
						closestVoxel = cvResult;
					}
				}
			}
		}

		RayTraceResult farthestEmptyVoxel = null;
		int farthestEmptyIndex = -1;

		for (int i = 0, x = 0, y = 0, z = 0; i < voxels.length; i++, x = i % 8, y = (i % 64) / 8, z = i / 64)
		{
			AxisAlignedBB voxelPos = voxel.offset(pos.getX() + x * 0.0625 + 0.25, pos.getY() + 1 + y * 0.0625, pos.getZ() + z * 0.0625);
			if (voxels[i] == 0)
			{
				RayTraceResult fvResult = voxelPos.calculateIntercept(origin, end);
				if (fvResult != null)
				{
					if (Utils.isClosest(origin, closestVoxel == null ? null : closestVoxel.hitVec, fvResult.hitVec))
					{
						if (Utils.isFarthest(origin, farthestEmptyVoxel == null ? null : farthestEmptyVoxel.hitVec, fvResult.hitVec))
						{
							farthestEmptyVoxel = fvResult;
							farthestEmptyIndex = i;
						}
					}
				}
			}
		}

		return farthestEmptyIndex;
	}

	@Deprecated
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override public boolean hasTileEntity()
	{
		return true;
	}

	@Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return super.getBoundingBox(state, source, pos).union(HOLO_BOX);
	}

	@Nullable @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return super.getCollisionBoundingBox(blockState, worldIn, pos).union(HOLO_BOX);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override @Nullable
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityBlueprintCreator();
	}

	@Nullable @Override public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return createTileEntity(worldIn, getStateFromMeta(meta));
	}

	public static class TileEntityBlueprintCreator extends TileEntity implements ITickable
	{

		public byte[] voxels = new byte[1024];

		public TileEntityBlueprintCreator()
		{
		}

		@Override public void update()
		{
			setHologram(getWorld(), getPos().up());
		}

		void setHologram(World w, BlockPos pos)
		{
			if (w.isAirBlock(pos))
			{
				w.setBlockState(pos, StarTech.Blocks.hologram.getDefaultState());
			}
		}

		@Override public void readFromNBT(NBTTagCompound compound)
		{
			super.readFromNBT(compound);
			if (compound.hasKey("voxels"))
			{
				voxels = compound.getByteArray("voxels");
			}
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			NBTTagCompound nbt = super.writeToNBT(compound);
			nbt.setByteArray("voxels", voxels);
			return nbt;
		}

		@Nullable @Override public SPacketUpdateTileEntity getUpdatePacket()
		{
			return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
		}

		@Override public NBTTagCompound getUpdateTag()
		{
			return writeToNBT(new NBTTagCompound());
		}

		@Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
		{
			readFromNBT(pkt.getNbtCompound());
		}

		@Override public AxisAlignedBB getRenderBoundingBox()
		{
			return super.getRenderBoundingBox().union(HOLO_BOX);
		}
	}

}
