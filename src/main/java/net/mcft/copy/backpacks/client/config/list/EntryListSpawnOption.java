package net.mcft.copy.backpacks.client.config.list;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.BaseEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.IBackpackType;
import net.mcft.copy.backpacks.config.Setting;
import net.mcft.copy.backpacks.config.custom.SettingSpawn;
import net.mcft.copy.backpacks.misc.util.LangUtils;

@SideOnly(Side.CLIENT)
public class EntryListSpawnOption extends EntryList<SettingSpawn.Entry> {
	
	private static final int CHANCE_WIDTH = 38;
	private static final int BUTTON_SIZE  = 18;
	
	public EntryListSpawnOption(GuiConfig owningScreen, GuiConfigEntries owningEntryList, Setting<?> setting) {
		super(owningScreen, owningEntryList, setting);
	}
	
	@Override
	protected GuiEditArrayEntries createArrayEntries(
		GuiEditArray editArrayGui, Object[] beforeValues, Object[] currentValues) {
		return new Entries(editArrayGui, mc, configElement, beforeValues, currentValues);
	}
	
	@Override
	protected IArrayEntry createArrayEntry(GuiEditArray editArrayGui, GuiEditArrayEntries editArrayEntries) {
		return new Entry(editArrayGui, editArrayEntries, configElement, null);
	}
	
	public static class Entries extends EntryList.Entries {
		
		private final HoverChecker _chanceHoverChecker;
		private final HoverChecker _backpackHoverChecker;
		private final HoverChecker _lootTableHoverChecker;
		
		private final List<String> _chanceTooltip;
		private final List<String> _backpackTooltip;
		private final List<String> _lootTableTooltip;
		
		public Entries(GuiEditArray parent, Minecraft mc, IConfigElement configElement,
		               Object[] beforeValues, Object[] currentValues) {
			super(parent, mc, configElement, beforeValues, currentValues);
			top += 12;
			
			int entryWidth = getEntryWidth();
			int x = (width - entryWidth) / 2;
			int width = (entryWidth - CHANCE_WIDTH - BUTTON_SIZE * 2) / 2 - 6;
			
			_chanceHoverChecker    = new HoverChecker(top - 12, top, x + 2, x + 2 + CHANCE_WIDTH, 200);
			_backpackHoverChecker  = new HoverChecker(top - 12, top, x + CHANCE_WIDTH + BUTTON_SIZE + 5, x + CHANCE_WIDTH + BUTTON_SIZE + 5 + width, 200);
			_lootTableHoverChecker = new HoverChecker(top - 12, top, x + CHANCE_WIDTH + BUTTON_SIZE + width + 9, x + CHANCE_WIDTH + BUTTON_SIZE + width + 9 + width, 200);
			
			String langKeyPre = "config." + WearableBackpacks.MOD_ID + ".category.spawn.entry.";
			_chanceTooltip    = LangUtils.format(langKeyPre + "chance.tooltip");
			_backpackTooltip  = LangUtils.format(langKeyPre + "backpack.tooltip");
			_lootTableTooltip = LangUtils.format(langKeyPre + "lootTable.tooltip");
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);
				
			int xx = (width - getEntryWidth()) / 2;
			int yy = top - 10;
			int width = (getEntryWidth() - CHANCE_WIDTH - BUTTON_SIZE * 2) / 2 - 6;
			
			FontRenderer fontRenderer = mc.fontRenderer;
			owningGui.drawCenteredString(fontRenderer, "Chance", xx + CHANCE_WIDTH / 2 + 2, yy, 0xFFFFFF);
			owningGui.drawCenteredString(fontRenderer, "Backpack", xx + CHANCE_WIDTH + 5 + (BUTTON_SIZE + 2 + width) / 2, yy, 0xFFFFFF);
			owningGui.drawCenteredString(fontRenderer, "Loot Table", xx + BUTTON_SIZE + CHANCE_WIDTH + width + 9 + width / 2, yy, 0xFFFFFF);
		}
		
		@Override
		public void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
			FontRenderer fontRenderer = mc.fontRenderer;
			if (_chanceHoverChecker.checkHover(mouseX, mouseY))
				GuiUtils.drawHoveringText(_chanceTooltip, mouseX, mouseY, width, height, 300, fontRenderer);
			if (_backpackHoverChecker.checkHover(mouseX, mouseY))
				GuiUtils.drawHoveringText(_backpackTooltip, mouseX, mouseY, width, height, 300, fontRenderer);
			if (_lootTableHoverChecker.checkHover(mouseX, mouseY))
				GuiUtils.drawHoveringText(_lootTableTooltip, mouseX, mouseY, width, height, 300, fontRenderer);
		}
		
	}
	
	public static class Entry extends BaseEntry {
		
		private final GuiTextField _fieldChance;
		private final GuiTextField _fieldBackpack;
		private final GuiTextField _fieldLootTable;
		
		public Entry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList,
		             IConfigElement configElement, Object value) {
			super(owningScreen, owningEntryList, configElement);
			SettingSpawn.Entry entry = (value != null)
				? (SettingSpawn.Entry)value
				: SettingSpawn.Entry.DEFAULT;
			
			int entryWidth = ((EntryList.Entries)owningEntryList).getEntryWidth();
			int width = (entryWidth - CHANCE_WIDTH - BUTTON_SIZE * 2) / 2 - 6;
			FontRenderer fontRenderer = owningEntryList.getMC().fontRenderer;
			_fieldChance = new GuiTextField(0, fontRenderer, 0, 0, CHANCE_WIDTH, 16);
			_fieldChance.setMaxStringLength(5);
			_fieldChance.setText(Integer.toString(entry.chance));
			_fieldChance.setCursorPositionEnd();
			_fieldBackpack = new GuiTextField(0, fontRenderer, 0, 0, width, 16);
			_fieldBackpack.setMaxStringLength(256);
			_fieldBackpack.setText(entry.backpack);
			_fieldBackpack.setCursorPositionEnd();
			_fieldLootTable = new GuiTextField(0, fontRenderer, 0, 0, width, 16);
			_fieldLootTable.setMaxStringLength(256);
			_fieldLootTable.setText(entry.lootTable);
			_fieldLootTable.setCursorPositionEnd();
		}
		
		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight,
		                      int mouseX, int mouseY, boolean isSelected) {
			Minecraft mc = owningScreen.mc;
			
			int entryWidth = ((EntryList.Entries)owningEntryList).getEntryWidth();
			int xx = (listWidth - entryWidth) / 2;
			int width = (entryWidth - CHANCE_WIDTH - BUTTON_SIZE * 2) / 2 - 6;
			
			Item item = Item.getByNameOrId(_fieldBackpack.getText());
			if (!(item instanceof IBackpackType)) item = null;
			
			_fieldChance.xPosition = xx + 2;
			_fieldChance.yPosition = y + 1;
			_fieldBackpack.xPosition = xx + CHANCE_WIDTH + BUTTON_SIZE + 5;
			_fieldBackpack.yPosition = y + 1;
			_fieldLootTable.xPosition = xx + CHANCE_WIDTH + BUTTON_SIZE + width + 9;
			_fieldLootTable.yPosition = y + 1;
			btnRemoveEntry.xPosition = xx + entryWidth - btnRemoveEntry.width;
			btnRemoveEntry.yPosition = y;
			
			if (item == null) Gui.drawRect(xx + CHANCE_WIDTH + 4, y - 1,
				xx + CHANCE_WIDTH + BUTTON_SIZE + width + 7, y + BUTTON_SIZE + 1, 0xFFCC2222);
			
			_fieldChance.drawTextBox();
			_fieldBackpack.drawTextBox();
			_fieldLootTable.drawTextBox();
			btnRemoveEntry.drawButton(mc, mouseX, mouseY);
			
			Gui.drawRect(xx + CHANCE_WIDTH + 5, y, xx + CHANCE_WIDTH + 5 + BUTTON_SIZE, y + BUTTON_SIZE, 0xFFAAAAAA);
			Gui.drawRect(xx + CHANCE_WIDTH + 6, y + 1, xx + CHANCE_WIDTH + 5 + BUTTON_SIZE - 1, y + BUTTON_SIZE - 1, 0xFF333333);
			if (item != null) {
				GlStateManager.enableDepth();
				GlStateManager.enableRescaleNormal();
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemIntoGUI(
					new ItemStack(item), xx + CHANCE_WIDTH + 6, y + 1);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				GlStateManager.disableDepth();
			}
			
		}
		
		@Override
		public void keyTyped(char eventChar, int eventKey) {
			
			// If tab key is pressed, focus the next / previous field.
			if (eventKey == Keyboard.KEY_TAB) {
				if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { // Focus next field.
					if (_fieldChance.isFocused()) {
						_fieldChance.setFocused(false);
						_fieldBackpack.setFocused(true);
					} else if (_fieldBackpack.isFocused()) {
						_fieldBackpack.setFocused(false);
						_fieldLootTable.setFocused(true);
					}
				} else { // Focus previous field.
					if (_fieldBackpack.isFocused()) {
						_fieldBackpack.setFocused(false);
						_fieldChance.setFocused(true);
					} else if (_fieldLootTable.isFocused()) {
						_fieldLootTable.setFocused(false);
						_fieldBackpack.setFocused(true);
					}
				}
				return;
			}
			
			boolean isAllowedKey = (eventKey == Keyboard.KEY_BACK) || (eventKey == Keyboard.KEY_DELETE) ||
			                       (eventKey == Keyboard.KEY_LEFT) || (eventKey == Keyboard.KEY_RIGHT) ||
			                       (eventKey == Keyboard.KEY_HOME) || (eventKey == Keyboard.KEY_END);
			if (isAllowedKey || ("0123456789".indexOf(eventChar) >= 0))
				_fieldChance.textboxKeyTyped(eventChar, eventKey);
			_fieldBackpack.textboxKeyTyped(eventChar, eventKey);
			_fieldLootTable.textboxKeyTyped(eventChar, eventKey);
		}
		
		@Override
		public void mouseClicked(int x, int y, int mouseEvent) {
			_fieldChance.mouseClicked(x, y, mouseEvent);
			_fieldBackpack.mouseClicked(x, y, mouseEvent);
			_fieldLootTable.mouseClicked(x, y, mouseEvent);
		}
		
		@Override
		public Object getValue() {
			int chance = 0;
			try { chance = Integer.parseInt(_fieldChance.getText()); }
			catch (NumberFormatException ex) {  }
			return new SettingSpawn.Entry(chance,
				_fieldBackpack.getText(), _fieldLootTable.getText());
		}
		
	}
	
}
