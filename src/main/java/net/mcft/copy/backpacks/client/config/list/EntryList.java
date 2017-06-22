package net.mcft.copy.backpacks.client.config.list;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.BaseEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcft.copy.backpacks.client.config.EntryButton;
import net.mcft.copy.backpacks.config.Setting;
import net.mcft.copy.backpacks.config.SettingList;

@SideOnly(Side.CLIENT)
public class EntryList<T> extends EntryButton<List<T>> {
	
	public EntryList(GuiConfig owningScreen, GuiConfigEntries owningEntryList, Setting<?> setting)
		{ super(owningScreen, owningEntryList, setting); }
	
	
	public GuiEditArray createEditArrayGui(GuiScreen parentScreen)
		{ return new EditArray(parentScreen, this); }
	
	protected GuiEditArrayEntries createArrayEntries(
		GuiEditArray editArrayGui, Object[] beforeValues, Object[] currentValues)
		{ return new Entries(editArrayGui, mc, configElement, beforeValues, currentValues); }
	
	protected IArrayEntry createArrayEntry(GuiEditArray editArrayGui, GuiEditArrayEntries editArrayEntries) {
		String arrayEntryClassName = ((SettingList<T>)setting).getArrayEntryClass();
		if (arrayEntryClassName == null) throw new RuntimeException(
			"Setting '" + setting.getFullName() + "' has no array entry class defined");
		try {
			return (IArrayEntry)Class.forName(arrayEntryClassName)
				.getConstructor(GuiEditArray.class, GuiEditArrayEntries.class, IConfigElement.class, Object.class)
				.newInstance(editArrayGui, editArrayEntries, configElement, null);
		} catch (ReflectiveOperationException ex) { throw new RuntimeException(
			"Exception while instantiating array entry class for '" + setting.getFullName() +
				"' (array entry class '" + arrayEntryClassName + "')", ex); }
	}
	
	
	@Override
	public void buttonPressed() {
		Minecraft.getMinecraft().displayGuiScreen(createEditArrayGui(owningScreen));
	}
	
	@Override
	public void onValueChanged() {
		int count = value.size();
		button.displayString = (count + " entr" + ((count != 1) ? "ies" : "y"));
	}
	
	
	public static class EditArray extends GuiEditArray {
		
		protected final EntryList<?> _entry;
		
		public EditArray(GuiScreen parentScreen, EntryList<?> entry) {
			super(parentScreen, entry.configElement, -1, entry.setting.get().toArray(), true);
			_entry = entry;
		}
		
		@Override
		public void initGui() {
			super.initGui();
			entryList = _entry.createArrayEntries(this, beforeValues, currentValues);
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);
			((Entries)entryList).drawScreenPost(mouseX, mouseY, partialTicks);
		}
		
	}
	
	public static class Entries extends GuiEditArrayEntries {
		
		public Entries(GuiEditArray parent, Minecraft mc, IConfigElement configElement,
		               Object[] beforeValues, Object[] currentValues) {
			super(parent, mc, configElement, beforeValues, currentValues);
			canAddMoreEntries = false;
			listEntries.remove(listEntries.size() - 1);
			listEntries.add(new ListEntryAdd(parent, this, configElement, null));
		}
		
		public int getEntryWidth() { return 320; }
		
		@Override
		protected int getScrollBarX() { return width - (width - getEntryWidth()) / 2; }
		
		@Override
		public void addNewEntry(int index) {
			listEntries.add(index, ((EditArray)owningGui)._entry.createArrayEntry(owningGui, this));
		}
		
		@Override
		public void removeEntry(int index) {
			listEntries.remove(index);
		}
		
		public void drawScreenPost(int mouseX, int mouseY, float partialTicks) {  }
		
	}
	
	public static class ListEntryAdd extends BaseEntry {
		
		public ListEntryAdd(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
							IConfigElement configElement, Object value) {
			super(owningScreen, owningEntryList, configElement);
		}
		
		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent,
		                            int relativeX, int relativeY) {
			if (btnAddNewEntryAbove.mousePressed(owningEntryList.getMC(), x, y)) {
				btnAddNewEntryAbove.playPressSound(owningEntryList.getMC().getSoundHandler());
				owningEntryList.addNewEntry(index);
				owningEntryList.recalculateState();
				return true;
			} else return false;
		}
		
		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight,
		                      int mouseX, int mouseY, boolean isSelected) {
			int entryWidth = ((Entries)owningEntryList).getEntryWidth();
			btnAddNewEntryAbove.xPosition = (listWidth - entryWidth) / 2;
			btnAddNewEntryAbove.yPosition = y;
			btnAddNewEntryAbove.width = entryWidth;
			btnAddNewEntryAbove.drawButton(owningEntryList.getMC(), mouseX, mouseY);
		}
		
	}
	
}
