package com.nic.st.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Nictogen on 1/18/19.
 */
public class MessageMovePlayer implements IMessage
{

	private double x, y, z;
	public MessageMovePlayer(){}

	public MessageMovePlayer(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override public void fromBytes(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	public static class Handler implements IMessageHandler<MessageMovePlayer, IMessage>
	{

		@Override public IMessage onMessage(MessageMovePlayer message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().player.motionX = message.x;
				Minecraft.getMinecraft().player.motionY = message.y;
				Minecraft.getMinecraft().player.motionZ = message.z;
			});
			return null;
		}
	}
}
