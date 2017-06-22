package net.mcft.copy.backpacks.client.config.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.BaseEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.client.config.list.ListEntryWrapper.FakeGuiConfig;
import net.mcft.copy.backpacks.config.Setting;
import net.mcft.copy.backpacks.config.custom.SettingSpawn;

@SideOnly(Side.CLIENT)
public class EntryListSpawn extends EntryList<SettingSpawn.Entity> {
	
	public EntryListSpawn(GuiConfig owningScreen, GuiConfigEntries owningEntryList, Setting<?> setting) {
		super(owningScreen, owningEntryList, setting);
	}
	
	@Override
	public GuiEditArray createEditArrayGui(GuiScreen parentScreen) {
		return new EditArray(parentScreen, this) {{
			titleLine2 = I18n.format("config.wearablebackpacks.category.spawn");
			titleLine3 = null;
			tooltipHoverChecker = null;
		}};
	}
	
	@Override
	protected GuiEditArrayEntries createArrayEntries(
		GuiEditArray editArrayGui, Object[] beforeValues, Object[] currentValues) {
		FakeGuiConfig guiConfig = new FakeGuiConfig(editArrayGui);
		return new Entries(editArrayGui, mc, configElement, beforeValues, currentValues) {
			{
				listEntries.add(0, new ListEntryWrapper(owningGui, this, guiConfig,
					WearableBackpacks.CONFIG.getSetting("spawn.enabled")));
			}
			@Override
			public void drawScreen(int mouseX, int mouseY, float partialTicks) {
				guiConfig.recalculateState(listEntries);
				super.drawScreen(mouseX, mouseY, partialTicks);
			}
			@Override
			public int getEntryWidth() { return 240; }
		};
	}
	
	@Override
	protected IArrayEntry createArrayEntry(GuiEditArray editArrayGui, GuiEditArrayEntries editArrayEntries)
		{ return new Entry(editArrayGui, editArrayEntries, configElement, null); }
	
	
	public static class Entry extends BaseEntry {
		
		private static final int BUTTON_SIZE = 18;
		private static final int ENTRIES_WIDTH = 72;
		
		private final GuiTextField _fieldEntityName;
		private final GuiButtonExt _buttonEntries;
		
		public Entry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
		             IConfigElement configElement, Object value) {
			super(owningScreen, owningEntryList, configElement);
			SettingSpawn.Entity entity = (value != null)
				? (SettingSpawn.Entity)value
				: new SettingSpawn.Entity();
			
			int entryWidth = ((EntryList.Entries)owningEntryList).getEntryWidth();
			int width = entryWidth - BUTTON_SIZE - ENTRIES_WIDTH - btnRemoveEntry.width - 3;
			
			FontRenderer fontRenderer = owningEntryList.getMC().fontRenderer;
			_fieldEntityName = new GuiTextField(0, fontRenderer, 0, 0, width, 16);
			_fieldEntityName.setMaxStringLength(256);
			_fieldEntityName.setText(entity.entityName);
			_fieldEntityName.setCursorPositionEnd();
			
			_buttonEntries = new GuiButtonExt(0, 0, 0, ENTRIES_WIDTH, 18,
				I18n.format("config.wearablebackpacks.spawn.options"));
		}
		
		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight,
		                      int mouseX, int mouseY, boolean isSelected) {
			Minecraft mc = owningScreen.mc;
			
			int entryWidth = ((EntryList.Entries)owningEntryList).getEntryWidth();
			int xx = (listWidth - entryWidth) / 2;
			int width = entryWidth - BUTTON_SIZE - ENTRIES_WIDTH - btnRemoveEntry.width - 3;
			
			Item entity = null; //_fieldEntityName.getText();
			
			_fieldEntityName.xPosition = xx + BUTTON_SIZE + 1;
			_fieldEntityName.yPosition = y + 1;
			_buttonEntries.xPosition = xx + BUTTON_SIZE + width + 3;
			_buttonEntries.yPosition = y;
			btnRemoveEntry.xPosition = xx + entryWidth - btnRemoveEntry.width;
			btnRemoveEntry.yPosition = y;
			
			if (entity == null) Gui.drawRect(xx, y - 1,
				xx + BUTTON_SIZE + width + 3, y + BUTTON_SIZE + 1, 0xFFCC2222);
			
			_fieldEntityName.drawTextBox();
			_buttonEntries.drawButton(mc, mouseX, mouseY);
			btnRemoveEntry.drawButton(mc, mouseX, mouseY);
			
			Gui.drawRect(xx + 1, y, xx + BUTTON_SIZE + 1, y + BUTTON_SIZE, 0xFFAAAAAA);
			Gui.drawRect(xx + 2, y + 1, xx + BUTTON_SIZE, y + BUTTON_SIZE - 1, 0xFF333333);
			if (entity != null) {
				// TODO: Render entity.
			}
			
		}
		
		@Override
		public void keyTyped(char eventChar, int eventKey) {
			_fieldEntityName.textboxKeyTyped(eventChar, eventKey);
		}
		
		@Override
		public void mouseClicked(int x, int y, int mouseEvent) {
			_fieldEntityName.mouseClicked(x, y, mouseEvent);
		}
		
		@Override
		public Object getValue() {
			return null;
		}
		
	}
	
}
