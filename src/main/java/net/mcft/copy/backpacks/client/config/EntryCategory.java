package net.mcft.copy.backpacks.client.config;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ListEntryBase;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.client.config.BackpacksGuiConfig;

public class EntryCategory extends ListEntryBase {
	
	public String category;
	
	protected final GuiButtonExt button;
	protected final BackpacksGuiConfig childScreen;
	
	public EntryCategory(GuiConfig owningScreen, GuiConfigEntries owningEntryList, String category) {
		super(owningScreen, owningEntryList, new ConfigElement(
			WearableBackpacks.CONFIG.getCategory(category).setLanguageKey(
				"config." + WearableBackpacks.MOD_ID + ".category." + category)));
		this.category = category;
		
		button = new GuiButtonExt(0, 0, 0, 300, 18, I18n.format(name));
		tooltipHoverChecker = new HoverChecker(button, 800);
		drawLabel = false;
		
		childScreen = new BackpacksGuiConfig(
			owningScreen, category, owningScreen.title,
			((owningScreen.titleLine2 != null) ? owningScreen.titleLine2 : "") + " > " + name,
			owningScreen.allRequireWorldRestart || configElement.requiresWorldRestart(),
			owningScreen.allRequireMcRestart || configElement.requiresMcRestart());
	}
	
	private List<IConfigEntry> getEntries() { return childScreen.entryList.listEntries; }
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight,
	                      int mouseX, int mouseY, boolean isSelected) {
		super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
		
		button.xPosition = listWidth / 2 - 150;
		button.yPosition = y;
		button.enabled = enabled();
		button.drawButton(mc, mouseX, mouseY);
	}
	
	@Override
	public void drawToolTip(int mouseX, int mouseY) {
		boolean canHover = ((mouseY < owningScreen.entryList.bottom) &&
		                    (mouseY > owningScreen.entryList.top));
		if (tooltipHoverChecker.checkHover(mouseX, mouseY, canHover))
			owningScreen.drawToolTip(toolTip, mouseX, mouseY);
		super.drawToolTip(mouseX, mouseY);
	}
	
	@Override
	public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		if (!button.mousePressed(mc, x, y))
			return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
		button.playPressSound(mc.getSoundHandler());
		Minecraft.getMinecraft().displayGuiScreen(childScreen);
		return true;
	}

	@Override
	public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		button.mouseReleased(x, y);
	}
	
	@Override
	public void keyTyped(char eventChar, int eventKey) {  }
	@Override
	public void updateCursorCounter() {  }
	@Override
	public void mouseClicked(int x, int y, int mouseEvent) {  }
	
	
	@Override
	public boolean isDefault() { return getEntries().stream().allMatch(IConfigEntry::isDefault); }
	@Override
	public void setToDefault() { if (enabled()) getEntries().forEach(IConfigEntry::setToDefault); }
	
	@Override
	public boolean isChanged() { return getEntries().stream().anyMatch(IConfigEntry::isChanged); }
	@Override
	public void undoChanges() { if (enabled()) getEntries().forEach(IConfigEntry::undoChanges); }
	
	@Override
	public boolean saveConfigElement() {
		if (!enabled() || !isChanged()) return false;
		boolean requireRestart = false;
		for (IConfigEntry entry : getEntries())
			requireRestart |= entry.saveConfigElement();
		return requireRestart;
		
	}
	
	@Override
	public Object getCurrentValue() { return ""; }
	@Override
	public Object[] getCurrentValues() { return new Object[] { getCurrentValue() }; }
	
	@Override
	public int getLabelWidth() { return 0; }
	
	@Override
	public int getEntryRightBound() { return (owningEntryList.width / 2) + 155 + 22 + 18; }
	
}
