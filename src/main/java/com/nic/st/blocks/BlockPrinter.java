package com.nic.st.blocks;

import com.nic.st.StarTech;
import com.nic.st.items.ItemBlueprint;
import com.nic.st.items.ItemPrintedGun;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class BlockPrinter extends Block
{
	public static final PropertyBool EMPTY = PropertyBool.create("empty");
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public BlockPrinter()
	{
		super(Material.IRON);
		setRegistryName(StarTech.MODID, "printer");
		setTranslationKey("printer");
		setHardness(2.0f).setResistance(10.0f);
		setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EMPTY, true));
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(worldIn, pos, state);
		this.setDefaultDirection(worldIn, pos, state);
	}

	private void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			EnumFacing enumfacing = state.getValue(FACING);
			boolean flag = worldIn.getBlockState(pos.north()).isFullBlock();
			boolean flag1 = worldIn.getBlockState(pos.south()).isFullBlock();

			if (enumfacing == EnumFacing.NORTH && flag && !flag1)
			{
				enumfacing = EnumFacing.SOUTH;
			}
			else if (enumfacing == EnumFacing.SOUTH && flag1 && !flag)
			{
				enumfacing = EnumFacing.NORTH;
			}
			else
			{
				boolean flag2 = worldIn.getBlockState(pos.west()).isFullBlock();
				boolean flag3 = worldIn.getBlockState(pos.east()).isFullBlock();

				if (enumfacing == EnumFacing.WEST && flag2 && !flag3)
				{
					enumfacing = EnumFacing.EAST;
				}
				else if (enumfacing == EnumFacing.EAST && flag3 && !flag2)
				{
					enumfacing = EnumFacing.WEST;
				}
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
	}

	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
	}

	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | state.getValue(FACING).getIndex();

		return i;
	}

	@Override public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityPrinter)
		{
			return state.withProperty(EMPTY, ((TileEntityPrinter) te).blueprint.isEmpty());
		}
		return state.withProperty(EMPTY, false);
	}

	@Override protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, EMPTY, FACING);
	}

	@Deprecated
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ)
	{
		if (playerIn.getHeldItemMainhand().getItem() instanceof ItemBlueprint)
		{
			ItemStack stack = playerIn.getHeldItemMainhand();
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
			te.blueprint = stack;
			te.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		else if (playerIn.getHeldItemMainhand().getItem() instanceof ItemAir && playerIn.isSneaking())
		{
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			if (te.blueprint.isEmpty())
				return false;

			playerIn.setHeldItem(EnumHand.MAIN_HAND, te.blueprint);
			te.blueprint = ItemStack.EMPTY;
			te.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		else if (playerIn.getHeldItemMainhand().getItem() instanceof ItemAir)
		{
			TileEntityPrinter te = (TileEntityPrinter) worldIn.getTileEntity(pos);
			if (te.gun.isEmpty() || te.ticks != 0)
				return false;

			playerIn.setHeldItem(EnumHand.MAIN_HAND, te.gun);
			te.gun = ItemStack.EMPTY;

			if (new Random().nextInt(4) == 0)
			{
				playerIn.addItemStackToInventory(te.blueprint);
				worldIn.setBlockToAir(pos);
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.0f, false);
			}
			else
			{
				te.markDirty();
				worldIn.notifyBlockUpdate(pos, state, state, 3);
			}
			return true;
		}
		return false;
	}

	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override @Nullable
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityPrinter();
	}

	public static class TileEntityPrinter extends TileEntity implements ITickable
	{

		public int ticks = 0;
		public ItemStack blueprint = ItemStack.EMPTY;
		public ItemStack gun = ItemStack.EMPTY;

		@Override public void update()
		{
			if (ticks == 0 && gun.isEmpty() && !blueprint.isEmpty() && blueprint.getItem() instanceof ItemBlueprint && getWorld().isBlockPowered(getPos()))
			{
				ticks++;
				gun = new ItemStack(StarTech.Items.printedGun, 1);
				int[][] colors = new int[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES][];
				for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
				{
					colors[i] = blueprint.getTagCompound().getIntArray("color" + i);
				}

				ItemPrintedGun.VoxelUses[] uses = new ItemPrintedGun.VoxelUses[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES];
				for (int i = 0; i < uses.length; i++)
				{
					uses[i] = ItemPrintedGun.VoxelUses.values()[blueprint.getTagCompound().getIntArray("uses")[i]];
				}
				ItemPrintedGun.createGunData(new byte[0], gun, colors, uses);
				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			else if (ticks > 0 && getWorld().isBlockPowered(getPos()) && blueprint.getItem() instanceof ItemBlueprint)
			{
				ticks += 500;
				byte[] blueprintArray = blueprint.getTagCompound().getByteArray("voxels");
				byte[] newArray = new byte[blueprintArray.length];
				for (int i = blueprintArray.length - 1, n = 0; ticks >= n * 200 && i >= 0; i--)
				{
					newArray[i] = blueprintArray[i];
					if (blueprintArray[i] != 0)
						n++;
				}
				int[][] colors = new int[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES][];
				for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
				{
					colors[i] = blueprint.getTagCompound().getIntArray("color" + i);
				}
				ItemPrintedGun.VoxelUses[] uses = new ItemPrintedGun.VoxelUses[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES];
				for (int i = 0; i < uses.length; i++)
				{
					uses[i] = ItemPrintedGun.VoxelUses.values()[blueprint.getTagCompound().getIntArray("uses")[i]];
				}
				ItemPrintedGun.createGunData(newArray, gun, colors, uses);
				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			else if (ticks != 0 && (!getWorld().isBlockPowered(getPos()) || blueprint.isEmpty()))
			{
				gun = ItemStack.EMPTY;
				ticks = 0;

				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			if (!blueprint.isEmpty() && blueprint.getItem() instanceof ItemBlueprint && ticks > blueprint.getTagCompound().getInteger("total") * 200)
			{
				ticks = 0;
				int[][] colors = new int[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES][];
				for (int i = 0; i < BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES; i++)
				{
					colors[i] = blueprint.getTagCompound().getIntArray("color" + i);
				}
				ItemPrintedGun.VoxelUses[] uses = new ItemPrintedGun.VoxelUses[BlockBlueprintCreator.TileEntityBlueprintCreator.VOXEL_TYPES];
				for (int i = 0; i < uses.length; i++)
				{
					uses[i] = ItemPrintedGun.VoxelUses.values()[blueprint.getTagCompound().getIntArray("uses")[i]];
				}
				ItemPrintedGun.createGunData(blueprint.getTagCompound().getByteArray("voxels"), gun, colors, uses);
				IBlockState state = getWorld().getBlockState(getPos());
				markDirty();
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
		}

		@Override public void readFromNBT(NBTTagCompound compound)
		{
			super.readFromNBT(compound);
			blueprint = new ItemStack(compound.getCompoundTag("blueprint"));
			gun = new ItemStack(compound.getCompoundTag("gun"));
			ticks = compound.getInteger("ticks");
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			NBTTagCompound nbt = super.writeToNBT(compound);
			nbt.setTag("blueprint", blueprint.writeToNBT(new NBTTagCompound()));
			nbt.setTag("gun", gun.writeToNBT(new NBTTagCompound()));
			nbt.setInteger("ticks", ticks);
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
	}
}
