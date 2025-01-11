/**
 *
 * @author MecoX
 * @date 7/24/2023
 */
package net.pursue.mode.misc;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.value.values.BooleanValue;

public class Teams extends Mode {

	public static Teams instance;

	private final BooleanValue<Boolean> scoreboard = new BooleanValue<>(this,"Scoreboard", false);
	private final BooleanValue<Boolean> color = new BooleanValue<>(this,"Color", true);
	private final BooleanValue<Boolean> armorColor = new BooleanValue<>(this,"ArmorColor", false);

	public Teams() {
		super("Teams", "队伍", "检测你当前的队伍并且杀戮不打你的队友",  Category.MISC);
		instance = this;
	}
	
	public boolean isTeam(EntityLivingBase entity) {
		if (!this.isEnable()) {
			return false;
		}
		EntityPlayerSP thePlayer = mc.player;

		if (scoreboard.getValue() && thePlayer.getTeam() != null && entity.getTeam() != null) {
			Team iTeam = thePlayer.getTeam();
			Team iTeam2 = entity.getTeam();
			if (iTeam.isSameTeam(iTeam2)) {
				return true;
			}
		}

		ITextComponent displayName = thePlayer.getDisplayName();

		if(armorColor.getValue()){
			EntityPlayer entityPlayer = (EntityPlayer) entity;
			if (entityPlayer.getInventory().get(3) != null && entityPlayer.getInventory().get(3) != null) {
				ItemStack myHead;
				ItemStack iItemStack = myHead = entityPlayer.getInventory().get(3);
				Item team = iItemStack.getItem();
				ItemArmor myItemArmor = (ItemArmor) team;
				ItemStack entityHead = entityPlayer.getInventory().get(3);
				Item iItem2 = myHead.getItem();
				ItemArmor entityItemArmor = (ItemArmor) iItem2;
				int n = myItemArmor.getColor(myHead);
				if (n == entityItemArmor.getColor(entityHead)) {
					return true;
				}
			}
		}

		if (color.getValue() && displayName != null && entity.getDisplayName() != null) {
			String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
			String clientName = displayName.getFormattedText().replace("§r", "");
			return targetName.startsWith(String.valueOf('§') + clientName.charAt(1));
		}

		return false;
	}
}
