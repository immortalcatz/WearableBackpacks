package net.mcft.copy.backpacks.client.config.list;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.client.config.EntryCategory;
import net.mcft.copy.backpacks.config.Setting;

@SideOnly(Side.CLIENT)
public class EntryCategorySpawn extends EntryCategory {
	
	public EntryCategorySpawn(GuiConfig owningScreen, GuiConfigEntries owningEntryList, String category)
		{ super(owningScreen, owningEntryList, category); }
	
	@Override
	public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		if (!button.mousePressed(mc, x, y))
			return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
		button.playPressSound(mc.getSoundHandler());
		Setting<?> setting   = WearableBackpacks.CONFIG.getSetting(category + ".options");
		EntryListSpawn entry = (EntryListSpawn)setting.getEntry();
		GuiEditArray screen  = entry.createEditArrayGui(owningScreen);
		Minecraft.getMinecraft().displayGuiScreen(screen);
		return true;
	}
	
}
