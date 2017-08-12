package net.mcft.copy.backpacks.client.gui.config.custom;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mcft.copy.backpacks.client.gui.Alignment;
import net.mcft.copy.backpacks.client.gui.Direction;
import net.mcft.copy.backpacks.client.gui.GuiContainer;
import net.mcft.copy.backpacks.client.gui.GuiLayout;
import net.mcft.copy.backpacks.client.gui.config.BackpacksConfigScreen;
import net.mcft.copy.backpacks.client.gui.config.BaseConfigScreen;
import net.mcft.copy.backpacks.client.gui.config.IConfigEntry;
import net.mcft.copy.backpacks.client.gui.control.GuiButton;
import net.mcft.copy.backpacks.client.gui.control.GuiLabel;
import net.mcft.copy.backpacks.config.Setting.ChangeRequiredAction;
import net.mcft.copy.backpacks.config.custom.SettingListSpawn;
import net.mcft.copy.backpacks.config.custom.SettingListSpawn.EntityEntry;

@SideOnly(Side.CLIENT)
public class EntryListSpawn extends GuiLayout implements IConfigEntry {
	
	private static final int WIDTH = 240;
	
	private final SettingListSpawn _setting;
	private final List<EntityEntry> _previousValue;
	private final List<EntityEntry> _defaultValue;
	
	public final GuiButton buttonAdd;
	
	public EntryListSpawn(SettingListSpawn setting) {
		super(Direction.VERTICAL);
		setCenteredHorizontal(WIDTH);
		_setting = setting;
		
		_previousValue = setting.get();
		_defaultValue  = setting.getDefault();
		
		GuiContainer labelEntry = new GuiContainer();
		labelEntry.setFillHorizontal();
		labelEntry.setHeight(ENTRY_HEIGHT);
			GuiLabel label = new GuiLabel("Entity Spawn Entries"); // FIXME: Language key.
			label.setCenteredHorizontal();
			label.setBottom(2);
			labelEntry.add(label);
		addFixed(labelEntry);
		
		buttonAdd = new GuiButton(0, ENTRY_HEIGHT, TextFormatting.GREEN + "+");
		buttonAdd.setFillHorizontal();
		buttonAdd.setAction(() -> display(new ListEntryEntityScreen(this, null)));
		addFixed(buttonAdd);
		
		setValue(_previousValue);
	}
	
	public ListEntryEntity addEntry(EntityEntry value) {
		ListEntryEntity entry = new ListEntryEntity(this, value);
		insertFixed(children.size() - 1, entry);
		return entry;
	}
	
	public Stream<ListEntryEntity> getEntries() {
		return children.stream()
			.filter(ListEntryEntity.class::isInstance)
			.map(ListEntryEntity.class::cast);
	}
	public Stream<EntityEntry> getValueStream()
		{ return getEntries().map(entry -> entry.value); }
	public List<EntityEntry> getValue()
		{ return getValueStream().collect(Collectors.toList()); }
	
	
	public void setValue(List<EntityEntry> value) {
		for (int i = children.size() - 1; i >= 0; i--)
			if (children.get(i) instanceof ListEntryEntity)
				remove(children.get(i));
		value.forEach(this::addEntry);
	}
	
	private static boolean iteratorEquals(Iterator<?> it1, Iterator<?> it2) {
		while (it1.hasNext() && it2.hasNext())
			if (!Objects.equals(it1.next(), it2.next())) return false;
		return (!it1.hasNext() && !it2.hasNext());
	}
	
	
	public static class ListEntryEntity extends GuiLayout {
		
		public EntityEntry value;
		
		public final GuiButton buttonMove;
		public final GuiButton buttonEdit;
		public final GuiButton buttonRemove;
		
		private int _yOffset = 0;
		
		public ListEntryEntity(EntryListSpawn owningList, EntityEntry value) {
			super(Direction.HORIZONTAL);
			setFillHorizontal();
			this.value = value;
			
			buttonMove   = new MoveButton();
			buttonEdit   = new GuiButton(0, ENTRY_HEIGHT);
			buttonRemove = new GuiButton(ENTRY_HEIGHT, ENTRY_HEIGHT, TextFormatting.RED + "x");
			
			buttonEdit.setAction(() -> { display(new ListEntryEntityScreen(owningList, this)); });
			buttonRemove.setAction(() -> owningList.remove(this));
			
			addFixed(buttonMove);
			addWeighted(buttonEdit);
			addFixed(buttonRemove);
		}
		
		public boolean isValid() { return true; } // TODO: 
		
		@Override
		public void draw(int mouseX, int mouseY, float partialTicks) {
			buttonEdit.setText(value.entityName);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, _yOffset, 0);
			super.draw(mouseX, mouseY, partialTicks);
			GlStateManager.popMatrix();
		}
		
		public class MoveButton extends GuiButton {
			public MoveButton() {
				super(12, ENTRY_HEIGHT - 2, "=");
				setCenteredVertical();
			}
			@Override
			public boolean canDrag() { return true; }
			@Override
			public void onDragged(int mouseX, int mouseY, int startX, int startY)
				{ _yOffset = mouseY - startY; }
			@Override
			public void onMouseUp(int mouseButton, int mouseX, int mouseY)
				{ _yOffset = 0; }
			
		}
		
	}
	
	public static class ListEntryEntityScreen extends BaseConfigScreen {
		
		private static final int BUTTON_WIDTH = 120;
		
		public final EntryListSpawn owningList;
		public final ListEntryEntity entry;
		
		public final GuiLabel labelEntityName;
		public final GuiButton buttonCancel;
		
		public ListEntryEntityScreen(EntryListSpawn owningList, ListEntryEntity entry) {
			super(getCurrentScreen(), ((BackpacksConfigScreen)getCurrentScreen()).titleLines.toArray(new String[0]));
			this.owningList = owningList;
			this.entry      = entry;
			
			labelEntityName = new GuiLabel("<invalid entity name>");
			labelEntityName.setCenteredHorizontal();
			layoutTitle.addFixed(labelEntityName);
			
			buttonDone.setWidth(BUTTON_WIDTH);
			buttonCancel = new GuiButton(BUTTON_WIDTH, "Cancel"); // FIXME: Translate me!
			buttonCancel.setAction(this::cancelClicked);
			layoutButtons.addFixed(buttonCancel);
		}
		
		@Override
		protected void doneClicked() {
			if (entry != null) {  } // TODO: Update value.
			else owningList.addEntry(new EntityEntry());
			super.doneClicked();
		}
		
		private void cancelClicked() {
			super.doneClicked();
		}
		
	}
	
	
	// IConfigEntry implementation
	
	@Override
	public GuiLabel getLabel() { return null; }
	
	@Override
	public boolean isChanged() { return !iteratorEquals(getValueStream().iterator(), _previousValue.iterator()); }
	@Override
	public boolean isDefault() { return iteratorEquals(getValueStream().iterator(), _defaultValue.iterator()); }
	@Override
	public boolean isValid() { return getEntries().allMatch(ListEntryEntity::isValid); }
	
	@Override
	public void undoChanges() { setValue(_previousValue); }
	@Override
	public void setToDefault() { setValue(_defaultValue); }
	
	@Override
	public ChangeRequiredAction applyChanges() {
		if (!isChanged()) return ChangeRequiredAction.None;
		_setting.set(getValue());
		if (_setting.requiresMinecraftRestart()) _setting.update();
		return _setting.getChangeRequiredAction();
	}
	
}
