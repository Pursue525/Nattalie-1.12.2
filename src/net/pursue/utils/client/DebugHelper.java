package net.pursue.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DebugHelper {
    public static void sendMessage(Object message) {
        String chatPrefix = "\2477[" + TextFormatting.AQUA + TextFormatting.BOLD + "Nattalie" + TextFormatting.RESET + "\2477] " + TextFormatting.RESET;
        Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(chatPrefix + message));
    }

    public static void sendPacketMessage(Packet<?> packet) {
        String chatPrefix = "\2477[" + TextFormatting.AQUA + TextFormatting.BOLD + "PacketManager" + TextFormatting.RESET + "\2477] " + TextFormatting.RESET;

        Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(chatPrefix + "收到数据包" + packet.getClass().getSimpleName() + "以下为参数："));

        switch (packet) {
            case CPacketPlayer.Rotation rotation -> {
                sendMessage("Yaw", rotation.getYaw());
                sendMessage("Pitch", rotation.getPitch());
                sendMessage("OnGround", rotation.isOnGround());
            }

            case CPacketPlayer.Position position -> {
                sendMessage("X", position.getX());
                sendMessage("Y", position.getY());
                sendMessage("Z", position.getZ());
                sendMessage("OnGround", position.isOnGround());
            }

            case CPacketPlayer.PositionRotation positionRotation -> {
                sendMessage("X", positionRotation.getX());
                sendMessage("Y", positionRotation.getY());
                sendMessage("Z", positionRotation.getZ());
                sendMessage("Yaw", positionRotation.getYaw());
                sendMessage("Pitch", positionRotation.getPitch());
                sendMessage("OnGround", positionRotation.isOnGround());
            }

            case CPacketPlayer player -> sendMessage("OnGround", player.isOnGround());

            case CPacketEntityAction entityAction -> sendMessage("Type", entityAction.getAction());

            case CPacketHeldItemChange heldItemChange -> sendMessage("Slot", heldItemChange.getSlotId());

            case CPacketChatMessage chatMessage -> sendMessage("Message", chatMessage.getMessage());

            case CPacketUseEntity useEntity -> {
                switch (useEntity.getAction()) {
                    case ATTACK -> sendMessage("UseEntityID", useEntity.getEntityId());
                    case INTERACT -> {
                        sendMessage("UseEntityID", useEntity.getEntityId());
                        sendMessage("EnumHand", useEntity.getHand());
                    }
                    case INTERACT_AT -> {
                        sendMessage("UseEntityID", useEntity.getEntityId());
                        sendMessage("EnumHand", useEntity.getHand());
                        sendMessage("Vec3d", useEntity.getHitVec());
                    }
                }
            }

            case CPacketClickWindow clickWindow -> {
                sendMessage("WindowID", clickWindow.getWindowId());
                sendMessage("Type", clickWindow.getClickType());
                sendMessage("SlotID", clickWindow.getSlotId());
                sendMessage("UsedButton", clickWindow.getUsedButton());
                sendMessage("Item", clickWindow.getClickedItem().getDisplayName());
                sendMessage("ActionNumber", clickWindow.getActionNumber());
            }

            case CPacketPlayerDigging playerDigging -> {
                sendMessage("Type", playerDigging.getAction());
                sendMessage("BlockPos", playerDigging.getPosition());
                sendMessage("EnumFacing", playerDigging.getFacing());
            }

            case CPacketPlayerTryUseItemOnBlock playerTryUseItemOnBlock -> {
                sendMessage("BlockPos", playerTryUseItemOnBlock.getPos());
                sendMessage("EnumFacing", playerTryUseItemOnBlock.getDirection());
                sendMessage("EnumHand",playerTryUseItemOnBlock.getHand());
                sendMessage("Vec3d", new Vec3d(playerTryUseItemOnBlock.getFacingX(), playerTryUseItemOnBlock.getFacingY(), playerTryUseItemOnBlock.getFacingZ()));
            }

            case CPacketPlayerTryUseItem tryUseItem -> sendMessage("EnumHand",tryUseItem.getHand());

            case CPacketCloseWindow closeWindow -> sendMessage("WindowID", closeWindow.getWindowId());

            case CPacketConfirmTransaction confirmTransaction -> {
                sendMessage("WindowID", confirmTransaction.getWindowId());
                sendMessage("UID", confirmTransaction.getUid());
                sendMessage("Accepted", confirmTransaction.getAccepted());

            }

            case CPacketAnimation animation -> sendMessage("EnumHand", animation.getHand());

            case CPacketCustomPayload customPayload -> {
                sendMessage("Channel", customPayload.getChannelName());
                sendMessage("Data", customPayload.getBufferData());
            }

            case CPacketRecipeInfo recipeInfo -> {
                switch (recipeInfo.func_194156_a()) {
                    case SHOWN -> {
                        sendMessage("Type", "SHOWN");
                        sendMessage("IRecipe", recipeInfo.getField_193649_d());
                    }
                    case SETTINGS -> {
                        sendMessage("Type", "SETTINGS");
                        sendMessage("Field_192631_e", recipeInfo.isField_192631_e());
                        sendMessage("Field_192632_f", recipeInfo.isField_192632_f());
                    }
                }
            }

            case SPacketTitle title -> {
                sendMessage("Type", title.getType());
                sendMessage("Message", title.getMessage());
                sendMessage("FadeInTime", title.getFadeInTime());
                sendMessage("FadeOutTime", title.getFadeOutTime());
                sendMessage("DisplayTime", title.getDisplayTime());
            }

            case SPacketPlayerPosLook posLook -> {
                sendMessage("X", posLook.getX());
                sendMessage("Y", posLook.getY());
                sendMessage("Z", posLook.getZ());
                sendMessage("Yaw", posLook.getYaw());
                sendMessage("Pitch", posLook.getPitch());
                sendMessage("Flags", posLook.getFlags());
                sendMessage("TeleportId", posLook.getTeleportId());
            }

            case SPacketChat chat -> {
                sendMessage("Type", chat.getType());
                sendMessage("ChatComponent", chat.getChatComponent());
            }

            case SPacketTeams teams -> {
                sendMessage("Name", teams.getName());
                sendMessage("DisplayName", teams.getDisplayName());
                sendMessage("Prefix", teams.getPrefix());
                sendMessage("Suffix", teams.getSuffix());
                sendMessage("NameTagVisibility", teams.getNameTagVisibility());
                sendMessage("CollisionRule", teams.getCollisionRule());
                sendMessage("Color", teams.getColor());
                sendMessage("players", teams.getPlayers());
                sendMessage("Action", teams.getAction());
                sendMessage("FriendlyFlags", teams.getFriendlyFlags());
            }

            case SPacketEntityVelocity entityVelocity -> {
                sendMessage("EntityID", entityVelocity.getEntityID());
                sendMessage("X", entityVelocity.getMotionX());
                sendMessage("Y", entityVelocity.getMotionY());
                sendMessage("Z", entityVelocity.getMotionZ());
                sendMessage("计算后实际击退为：" + new Vec3d(entityVelocity.getMotionX() / 8000.0D, entityVelocity.getMotionY() / 8000.0D, entityVelocity.getMotionZ() / 8000.0D));
            }

            case SPacketWindowItems windowItems -> {
                sendMessage("WindowID", windowItems.getWindowId());

                final List<String> itemNames = new ArrayList<>();

                for (ItemStack itemStack : windowItems.getItemStacks()) {
                    itemNames.add(itemStack.getDisplayName());
                }

                sendMessage("ItemStacks", String.join(", ", itemNames));
            }

            default -> sendMessage("错误", "该数据包的参数暂时不支持查看");

        }
    }

    public static void sendMessage(Object chatPrefix, Object message) {

        String string = "\2477[" + TextFormatting.AQUA + TextFormatting.BOLD + chatPrefix + TextFormatting.RESET + "\2477] " + TextFormatting.RESET;
        Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(string + message));
    }

    public static void displayTray(String Title, String Text, TrayIcon.MessageType type) {
        if (!SystemTray.isSupported()) {
            System.out.println("This platform does not support System Tray.");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage("icon.png");
        if (image == null) {
            System.out.println("Failed to load tray icon image.");
            return;
        }
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System Tray Icon Demo");

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Tray icon clicked!");
            }
        });

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(Title, Text, type);
        } catch (AWTException e) {
            System.out.println("An error occurred while adding the tray icon.");
            e.printStackTrace();
        }
    }
}
