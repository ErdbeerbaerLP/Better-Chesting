package de.erdbeerbaerlp.betterchesting;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagList;

public enum ChestPermission {
	OPEN,EDIT,BREAK,SETTINGS;
	@Override
	public String toString() {
		return "perm."+this.name();
	}
	@Nullable
	public static ChestPermission fromString(String s) {
		for(ChestPermission p : ChestPermission.values()) {
			if(p.toString().equals(s)) return p;
		}
		return null;
	}
	public static ChestPermission[] all() {
		return ChestPermission.values();
	}
	public static ChestPermission[] fromNBTList(NBTTagList tagList) {
		if(tagList.hasNoTags()) return new ChestPermission[0];
		ArrayList<ChestPermission> list = new ArrayList<ChestPermission>();
		for(int i=0;i<tagList.tagCount();i++) {
			list.add(fromString(tagList.getStringTagAt(i)));
		}
		ChestPermission[] listArray = new ChestPermission[list.size()];
		for(int i=0;i<list.size();i++) {
			listArray[i] = list.get(i);
		}
		return listArray;
	}
}
