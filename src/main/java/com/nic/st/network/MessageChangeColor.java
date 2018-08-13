package com.nic.st.network;

import com.nic.st.blocks.BlockBlueprintCreator;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

/**
 * Created by Nictogen on 8/12/18.
 */
public class MessageChangeColor implements IMessage
{
	public Color color;
	public BlockPos pos;
	public int index;

	public MessageChangeColor()
	{
	}

	public MessageChangeColor(Color color, BlockPos pos, int index)
	{
		this.color = color;
		this.pos = pos;
		this.index = index;
	}

	@Override public void fromBytes(ByteBuf buf)
	{
		color = new Color(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		index = buf.readInt();
	}

	@Override public void toBytes(ByteBuf buf)
	{
		buf.writeInt(color.getRed());
		buf.writeInt(color.getBlue());
		buf.writeInt(color.getGreen());
		buf.writeInt(color.getAlpha());
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(index);
	}

	public static class Handler implements IMessageHandler<MessageChangeColor, IMessage>
	{

		@Override public IMessage onMessage(MessageChangeColor message, MessageContext ctx)
		{
			ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
				TileEntity te = ctx.getServerHandler().player.world.getTileEntity(message.pos);
				if (te instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
				{
					((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[message.index] = message.color;
					te.markDirty();
					IBlockState state = te.getWorld().getBlockState(te.getPos());
					te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
				}
			});
			return null;
		}
	}
}
