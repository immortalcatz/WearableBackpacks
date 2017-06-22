package net.mcft.copy.backpacks.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

public class SettingString extends SettingSingleValue<String> {
	
	public SettingString(String defaultValue) {
		super(defaultValue);
		setConfigEntryClass("net.mcft.copy.backpacks.client.config.EntryField");
	}
	
	@Override
	public String parse(String str) { return str; }
	
	@Override
	public String read(NBTBase tag) { return ((NBTTagString)tag).getString(); }
	@Override
	public NBTBase write(String value) { return new NBTTagString(value); }
	
}