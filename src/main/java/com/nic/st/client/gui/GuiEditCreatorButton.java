package com.nic.st.client.gui;

import com.nic.st.StarTech;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.network.MessageChangeVoxel;
import lucraft.mods.lucraftcore.util.container.ContainerDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Nictogen on 8/4/18.
 */
public class GuiEditCreatorButton extends GuiContainer
{
	private static ResourceLocation TEX = new ResourceLocation(StarTech.MODID, "textures/edit_creator.png");

	private GuiTextField red, blue, green;

	private GuiButton usage;
	private int usageIndex;

	private BlockPos pos;
	private int button;

	public GuiEditCreatorButton()
	{
		super(new ContainerDummy());
	}

	public GuiEditCreatorButton(BlockPos pos, int button)
	{
		super(new ContainerDummy());
		this.pos = pos;
		this.button = button;
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);

		red = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, 0, 0, 26, 14);
		red.setMaxStringLength(3);
		red.setText("0");

		green = new GuiTextField(1, Minecraft.getMinecraft().fontRenderer, 0, 0, 26, 14);
		green.setMaxStringLength(3);
		green.setText("0");

		blue = new GuiTextField(2, Minecraft.getMinecraft().fontRenderer, 0, 0, 26, 14);
		blue.setMaxStringLength(3);
		blue.setText("0");

		usage = addButton(new GuiButton(3, 0, 0, ""));
		if (te instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			red.setText(((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[button].getRed() + "");
			green.setText(((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[button].getGreen() + "");
			blue.setText(((BlockBlueprintCreator.TileEntityBlueprintCreator) te).colors[button].getBlue() + "");
			usage.displayString = ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).uses[button].name;
		}

	}

	@Override public void initGui()
	{
		super.initGui();
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);

		usage = addButton(new GuiButton(3, 0, 0, ""));
		if (te instanceof BlockBlueprintCreator.TileEntityBlueprintCreator)
		{
			usage.displayString = ((BlockBlueprintCreator.TileEntityBlueprintCreator) te).uses[button].name;
		}

	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.drawDefaultBackground();
		this.mc.getTextureManager().bindTexture(TEX);
		this.drawTexturedModalRect((width - 255) / 2, (height - 93) / 2, 0, 0, 255, 93);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;

		red.x = i + 43;
		red.y = j + 70;
		red.drawTextBox();
		this.drawCenteredString(mc.fontRenderer, "Red", red.x + 13, red.y - 10, Color.RED.getRGB());

		green.x = i + 73;
		green.y = j + 70;
		green.drawTextBox();
		this.drawCenteredString(mc.fontRenderer, "Green", green.x + 13, green.y - 10, Color.GREEN.getRGB());

		blue.x = i + 103;
		blue.y = j + 70;
		blue.drawTextBox();
		this.drawCenteredString(mc.fontRenderer, "Blue", blue.x + 12, blue.y - 10, Color.BLUE.getRGB());

		usage.x = i - 10;
		usage.y = j + 95;
		usage.drawButton(mc, mouseX, mouseY, mc.getRenderPartialTicks());

		GlStateManager.disableTexture2D();
		GlStateManager.color((float) Integer.parseInt(red.getText()) / 255f, (float) Integer.parseInt(green.getText()) / 255f,
				(float) Integer.parseInt(blue.getText()) / 255f);
		this.drawTexturedModalRect(i - 10, j + 90, 0, 0, 200, 5);
		GlStateManager.enableTexture2D();
	}

	@Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (red != null)
			red.mouseClicked(mouseX, mouseY, mouseButton);
		if (blue != null)
			blue.mouseClicked(mouseX, mouseY, mouseButton);
		if (green != null)
			green.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		if (button == usage)
		{
			usageIndex++;
			if (usageIndex >= ItemPrintedGun.VoxelUses.values().length)
				usageIndex = 0;
			button.displayString = ItemPrintedGun.VoxelUses.values()[usageIndex].name;
			StarTech.simpleNetworkWrapper.sendToServer(
					new MessageChangeVoxel(new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText())),
							usageIndex, pos,
							this.button));
		}
	}

	@Override protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		typeInBox(red, typedChar, keyCode);
		typeInBox(green, typedChar, keyCode);
		typeInBox(blue, typedChar, keyCode);

	}

	private void typeInBox(GuiTextField box, char typedChar, int keyCode)
	{
		if (box != null)
		{
			String s = box.getText();
			box.textboxKeyTyped(typedChar, keyCode);

			int text = 0;
			if (!box.getText().isEmpty())
			{

				try
				{
					text = Integer.parseInt(box.getText());
				}
				catch (NumberFormatException e)
				{
					text = Integer.parseInt(s);
				}

			}
			box.setText(String.valueOf(Math.min(text, 255)));

			StarTech.simpleNetworkWrapper.sendToServer(
					new MessageChangeVoxel(new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText())),
							usageIndex, pos,
							button));
		}
	}
}
