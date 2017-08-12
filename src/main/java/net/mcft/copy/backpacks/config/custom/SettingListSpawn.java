package net.mcft.copy.backpacks.config.custom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.mcft.copy.backpacks.config.Setting;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils.NbtType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.INBTSerializable;

public class SettingListSpawn extends Setting<List<SettingListSpawn.EntityEntry>> {
	
	public static class EntityEntry implements INBTSerializable<NBTTagCompound> {
		
		public static final String TAG_NAME    = "name";
		public static final String TAG_ENTRIES = "entries";
		
		public String entityName = "";
		public List<BackpackEntry> entries = Collections.emptyList();
		
		public NBTTagCompound serializeNBT() {
			return NbtUtils.createCompound(
				TAG_NAME, entityName,
				TAG_ENTRIES, entries);
		}
		public void deserializeNBT(NBTTagCompound nbt) {
			entityName = nbt.getString(TAG_NAME);
			entries = NbtUtils.getTagList(nbt.getTagList(TAG_ENTRIES, NbtType.COMPOUND), BackpackEntry::new);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EntityEntry)) return false;
			EntityEntry entry = (EntityEntry)obj;
			return (entry.entityName == entityName)
				&& (entry.entries.equals(entries));
		}
		
	}
	
	public static class BackpackEntry implements INBTSerializable<NBTTagCompound> {
		
		public static final BackpackEntry DEFAULT = new BackpackEntry(
			1000, "wearablebackpacks:backpack", "wearablebackpacks:backpack/default");
		
		public static final String TAG_CHANCE     = "chance";
		public static final String TAG_BACKPACK   = "backpack";
		public static final String TAG_LOOT_TABLE = "lootTable";
		
		public int chance;
		public String backpack;
		public String lootTable;
		
		public BackpackEntry() {  }
		public BackpackEntry(NBTBase tag) { deserializeNBT((NBTTagCompound)tag); }
		public BackpackEntry(int chance, String backpack, String lootTable)
			{ this.chance = chance; this.backpack = backpack; this.lootTable = lootTable; }
		
		public static BackpackEntry parse(String str) {
			String[] values = str.split(";", 4);
			if (values.length != 3) throw new IllegalArgumentException("Expected 3 parts, got " + values.length);
			int chance = Integer.parseInt(values[0]);
			if (chance < 0) throw new IllegalArgumentException("Chance is negative");
			String backpack = values[1];
			String lootTable = values[2];
			return new BackpackEntry(chance, backpack, lootTable);
		}
		
		@Override
		public String toString()
			{ return (chance + ";" + backpack + ";" + lootTable); }
		
		public NBTTagCompound serializeNBT() {
			return NbtUtils.createCompound(
				TAG_CHANCE, chance,
				TAG_BACKPACK, backpack,
				TAG_LOOT_TABLE, lootTable);
		}
		public void deserializeNBT(NBTTagCompound nbt) {
			chance = nbt.getInteger(TAG_CHANCE);
			backpack = nbt.getString(TAG_BACKPACK);
			lootTable = nbt.getString(TAG_LOOT_TABLE);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof BackpackEntry)) return false;
			BackpackEntry entry = (BackpackEntry)obj;
			return (entry.chance == chance)
				&& (entry.backpack.equals(backpack))
				&& (entry.lootTable.equals(lootTable));
		}
		
	}
	
	public SettingListSpawn() {
		super(Collections.emptyList()); // TODO: Proper default.
		setConfigEntryClass("net.mcft.copy.backpacks.client.gui.config.custom.EntryListSpawn");
	}
	
	@Override
	protected void loadFromConfiguration(Configuration config) {
		set(config.getCategoryNames().stream()
			.filter(name -> name.startsWith(getCategory() + Configuration.CATEGORY_SPLITTER))
			.map(category -> {
				EntityEntry entry = new EntityEntry();
				entry.entityName = category.split("\\.", 2)[1];
				entry.entries = Arrays.stream(config.getStringList("entries", category, null, null))
					.map(BackpackEntry::parse).collect(Collectors.toList());
				return entry;
			}).collect(Collectors.toList()));
	}
	@Override
	protected void saveToConfiguration(Configuration config) {
		config.getCategoryNames().stream()
			.filter(name -> name.startsWith(getCategory() + Configuration.CATEGORY_SPLITTER))
			.forEach(category -> config.removeCategory(config.getCategory(category)));
		for (EntityEntry entity : get()) {
			String category = getCategory() + Configuration.CATEGORY_SPLITTER + entity.entityName;
			config.get("entities", category, new String[0]).set(entity.entries.stream()
				.map(BackpackEntry::toString).toArray(length -> new String[length]));
		}
	}
	
	@Override
	public List<EntityEntry> read(NBTBase tag)
		{ return NbtUtils.getTagList((NBTTagList)tag, EntityEntry::new); }
	@Override
	public NBTBase write(List<EntityEntry> value)
		{ return NbtUtils.createTag(value); }
	
}
