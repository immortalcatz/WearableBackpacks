package net.mcft.copy.backpacks.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcft.copy.backpacks.misc.util.NbtUtils;

public abstract class SettingList<T> extends Setting<List<T>> {
	
	private String _arrayEntryClass = null;
	
	public SettingList() {
		super(new ArrayList<T>());
		setConfigEntryClass("net.mcft.copy.backpacks.client.config.list.EntryList");
	}
	
	@SideOnly(Side.CLIENT)
	public String getArrayEntryClass() { return _arrayEntryClass; }
	public SettingList<T> setArrayEntryClass(String arrayEntryClass)
		{ _arrayEntryClass = arrayEntryClass; return this; }
	
	public abstract T parseEntry(String str);
	public String stringifyEntry(T entry) { return Objects.toString(entry); }
	
	public abstract T readEntry(NBTBase tag);
	public abstract NBTBase writeEntry(T entry);
	
	
	private Property getPropertyFromConfig(Configuration config) {
		return config.get(getCategory(), getName(),
			String.valueOf(getDefault()), getComment(), Property.Type.STRING);
	}
	@Override
	protected final void loadFromConfiguration(Configuration config) {
		set(Arrays.stream(getPropertyFromConfig(config).getStringList())
			.map(value -> parseEntry(value))
			.collect(Collectors.toList()));
	}
	@Override
	protected final void saveToConfiguration(Configuration config) {
		getPropertyFromConfig(config).set(get().stream()
			.map(val -> stringifyEntry(val))
			.toArray(size -> new String[size]));
	}
	
	
	@Override
	public final List<T> read(NBTBase tag) {
		return NbtUtils.stream((NBTTagList)tag)
			.map(entry -> readEntry(entry))
			.collect(Collectors.toList());
	}
	@Override
	public final NBTBase write(List<T> value) {
		return value.stream()
			.map(entry -> writeEntry(entry))
			.collect(NbtUtils.toList());
	}
	
}