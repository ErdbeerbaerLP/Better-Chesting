package de.erdbeerbaerlp.betterchesting;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ChestUser {
	private String name;
	private String uuid;
	private ArrayList<ChestPermission> perms;
	public ChestUser(EntityPlayer player, ChestPermission... perm) {
		this(player.getName(), player.getUniqueID().toString(), perm);
	}
	
	public ChestUser(String name, String uuid, ChestPermission... perm) {
		this.name = name;
		this.uuid = uuid;
		ArrayList<ChestPermission> l = new ArrayList<ChestPermission>();
		for(ChestPermission p : perm) {
			l.add(p);
		}
		this.perms = l;
	}
	public String getName() {
		return name;
	}
	public String getUuid() {
		return uuid;
	}
	public NBTTagCompound toNBT() {
		NBTTagCompound o = new NBTTagCompound();
		o.setString("uuid", uuid);
		o.setString("name", name);
		NBTTagList a = new NBTTagList();
		for(ChestPermission p : perms) {
			if(p!=null)a.appendTag(new NBTTagString(p.toString()));
		}
		o.setTag("permissions", a);
		return o;
	}
	public static ChestUser fromNBT(NBTTagCompound tag) {
		if(tag.hasKey("name", 8) && tag.hasKey("uuid", 8) && tag.hasKey("permissions", 9)) {
			return new ChestUser(tag.getString("name"), tag.getString("uuid"), ChestPermission.fromNBTList(tag.getTagList("permissions", 8)));
		}else return new ChestUser("TEST", "null", ChestPermission.OPEN);
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toNBT().toString();
	}
	public static ChestUser getPublicChestUser() {
		return new ChestUser("*", "*", ChestPermission.all());
	}
	public boolean isPublic() {
		// TODO Auto-generated method stub
		return (this.name == "*" && this.uuid == "*");
	}
}
