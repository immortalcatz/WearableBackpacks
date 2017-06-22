package net.mcft.copy.backpacks.client.config.list;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.BaseEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;

import net.mcft.copy.backpacks.client.config.EntrySetting;
import net.mcft.copy.backpacks.config.Setting;

public class ListEntryWrapper extends BaseEntry {
	
	private final EntrySetting<?> _entry;
	
	public ListEntryWrapper(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
	                        FakeGuiConfig guiConfig, Setting<?> setting) {
		super(owningScreen, owningEntryList, setting.getConfigElement());
		_entry = EntrySetting.Create(guiConfig, guiConfig.entryList, setting);
	}
	
	/*
	public String label;
	public List<String> tooltip;
	public HoverChecker tooltipHoverChecker;
	
	public ListEntryWrapper(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
	                        IConfigElement configElement) {
		super(owningScreen, owningEntryList, configElement);
		
		label = I18n.format(configElement.getLanguageKey());
		if (label.equals(configElement.getLanguageKey()))
			label = configElement.getName();
		
		tooltip = new ArrayList<String>();
		tooltip.add(TextFormatting.GREEN + label);
		String tooltipLangKey = configElement.getLanguageKey() + ".tooltip";
		String tooltipStr = I18n.format(tooltipLangKey);
		if (!tooltipStr.equals(tooltipLangKey))
			Collections.addAll(tooltip, tooltipStr.split("\\n"));
		else tooltip.add(TextFormatting.RED + "No tooltip defined.");
		
		tooltipHoverChecker = new HoverChecker(0, 0, 0, 0, 800);
	}
	*/
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight,
	                      int mouseX, int mouseY, boolean isSelected)
		{ _entry.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected); }
	@Override
	public void drawToolTip(int mouseX, int mouseY)
		{ _entry.drawToolTip(mouseX, mouseY); }
	
	@Override
	public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
		{ return _entry.mousePressed(index, x, y, mouseEvent, relativeX, relativeY); }
	@Override
	public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
		{ _entry.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY); }
	@Override
	public void mouseClicked(int x, int y, int mouseEvent)
		{ _entry.mouseClicked(x, y, mouseEvent); }
	
	@Override
	public void keyTyped(char eventChar, int eventKey)
		{ _entry.keyTyped(eventChar, eventKey); }
	
	
	public static class FakeGuiConfig extends GuiConfig {
		
		public FakeGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, Collections.emptyList(), "", false, false, "");
		}
		
		public void recalculateState(List<IArrayEntry> entries) {
			width = entryList.width = parentScreen.width;
			entryList.left  = 0;
			entryList.right = width;
			
			entryList.maxLabelTextWidth = entries.stream()
				.filter(ListEntryWrapper.class::isInstance)
				.map(ListEntryWrapper.class::cast)
				.mapToInt(entry -> entry._entry.getLabelWidth())
				.max().orElse(0);
			
			int viewWidth = entryList.maxLabelTextWidth + 8 + (width / 2);
			entryList.labelX   = (width / 2) - (viewWidth / 2);
			entryList.controlX = entryList.labelX + entryList.maxLabelTextWidth + 8;
			entryList.resetX   = (width / 2) + (viewWidth / 2) - 45;
			
			entryList.maxEntryRightBound = entryList.resetX + 40;
			entryList.scrollBarX = entryList.maxEntryRightBound + 5;
			entryList.controlWidth = entryList.maxEntryRightBound - entryList.controlX - 45;
		}
		
	}
	
}
