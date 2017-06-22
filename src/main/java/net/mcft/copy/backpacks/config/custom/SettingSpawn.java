package net.mcft.copy.backpacks.config.custom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.INBTSerializable;

import net.mcft.copy.backpacks.config.Setting;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils.NbtType;

public class SettingSpawn extends Setting<List<SettingSpawn.Entity>> {
	
	public static class Entity implements INBTSerializable<NBTTagCompound> {
		
		public static final String TAG_NAME    = "name";
		public static final String TAG_ENTRIES = "entries";
		
		public String entityName = "";
		public List<Entry> entries = Collections.emptyList();
		
		public NBTTagCompound serializeNBT() {
			return NbtUtils.createCompound(
				TAG_NAME, entityName,
				TAG_ENTRIES, entries);
		}
		public void deserializeNBT(NBTTagCompound nbt) {
			entityName = nbt.getString(TAG_NAME);
			entries = NbtUtils.getTagList(nbt.getTagList(TAG_ENTRIES, NbtType.COMPOUND), () -> new Entry());
		}
		
	}
	
	public static class Entry implements INBTSerializable<NBTTagCompound> {
		
		public static final Entry DEFAULT = new Entry(
			1000, "wearablebackpacks:backpack", "wearablebackpacks:backpack/default");
		
		public static final String TAG_CHANCE     = "chance";
		public static final String TAG_BACKPACK   = "backpack";
		public static final String TAG_LOOT_TABLE = "lootTable";
		
		public int chance;
		public String backpack;
		public String lootTable;
		
		public Entry() {  }
		public Entry(NBTBase tag) { deserializeNBT((NBTTagCompound)tag); }
		public Entry(int chance, String backpack, String lootTable)
			{ this.chance = chance; this.backpack = backpack; this.lootTable = lootTable; }
		
		public static Entry parse(String str) {
			String[] values = str.split(";", 4);
			if (values.length != 3) throw new IllegalArgumentException("Expected 3 parts, got " + values.length);
			int chance = Integer.parseInt(values[0]);
			if (chance < 0) throw new IllegalArgumentException("Chance is negative");
			String backpack = values[1];
			String lootTable = values[2];
			return new Entry(chance, backpack, lootTable);
		}
		
		@Override
		public String toString() { return (chance + ";" + backpack + ";" + lootTable); }
		
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
		
	}
	
	
	public SettingSpawn() {
		super(Collections.emptyList());
		setConfigEntryClass("net.mcft.copy.backpacks.client.config.list.EntryListSpawn");
	}
	
	@Override
	protected void loadFromConfiguration(Configuration config) {
		set(config.getCategoryNames().stream()
			.filter(name -> name.startsWith(getCategory() + Configuration.CATEGORY_SPLITTER))
			.map(category -> {
				Entity entity = new Entity();
				entity.entityName = category.split("\\.", 2)[1];
				entity.entries = Arrays.stream(config.getStringList("entries", category, null, null))
					.map(Entry::parse).collect(Collectors.toList());
				return entity;
			}).collect(Collectors.toList()));
	}
	@Override
	protected void saveToConfiguration(Configuration config) {
		config.getCategoryNames().stream()
			.filter(name -> name.startsWith(getCategory() + Configuration.CATEGORY_SPLITTER))
			.forEach(category -> config.removeCategory(config.getCategory(category)));
		for (Entity entity : get()) {
			String category = getCategory() + Configuration.CATEGORY_SPLITTER + entity.entityName;
			config.get("entities", category, (String[])null).set(entity.entries.stream()
				.map(Entry::toString).toArray(length -> new String[length]));
		}
	}
	
	
	@Override
	public final List<Entity> read(NBTBase tag) {
		return NbtUtils.getTagList((NBTTagList)tag, () -> new Entity());
	}
	@Override
	public final NBTBase write(List<Entity> value) {
		return NbtUtils.createTag(value);
	}
	
}