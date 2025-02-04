package net.pursue.utils.Germ;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.pursue.event.EventManager;
import net.pursue.event.packet.EventPacket;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.PacketUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class GermManager implements CustomPacket {
    private static final byte[] joinGame1 = new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47};
    private static final byte[] openGUI = new byte[]{0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117};
    public static final byte[] MOD_LIST = new byte[]{2, 28, 9, 109, 105, 110, 101, 99, 114, 97, 102, 116, 6, 49, 46, 49, 50, 46, 50, 9, 100, 101, 112, 97, 114, 116, 109, 111, 100, 3, 49, 46, 48, 13, 115, 99, 114, 101, 101, 110, 115, 104, 111, 116, 109, 111, 100, 3, 49, 46, 48, 3, 101, 115, 115, 5, 49, 46, 48, 46, 50, 7, 118, 101, 120, 118, 105, 101, 119, 6, 50, 46, 54, 46, 49, 48, 18, 98, 97, 115, 101, 109, 111, 100, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 5, 49, 46, 57, 46, 52, 10, 115, 105, 100, 101, 98, 97, 114, 109, 111, 100, 3, 49, 46, 48, 11, 115, 107, 105, 110, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 15, 102, 117, 108, 108, 115, 99, 114, 101, 101, 110, 112, 111, 112, 117, 112, 12, 49, 46, 49, 50, 46, 50, 46, 51, 56, 48, 48, 48, 8, 115, 116, 111, 114, 101, 109, 111, 100, 3, 49, 46, 48, 3, 109, 99, 112, 4, 57, 46, 52, 50, 7, 115, 107, 105, 110, 109, 111, 100, 3, 49, 46, 48, 13, 112, 108, 97, 121, 101, 114, 109, 97, 110, 97, 103, 101, 114, 3, 49, 46, 48, 13, 100, 101, 112, 97, 114, 116, 99, 111, 114, 101, 109, 111, 100, 6, 49, 46, 49, 50, 46, 50, 9, 109, 99, 98, 97, 115, 101, 109, 111, 100, 3, 49, 46, 48, 17, 109, 101, 114, 99, 117, 114, 105, 117, 115, 95, 117, 112, 100, 97, 116, 101, 114, 3, 49, 46, 48, 3, 70, 77, 76, 9, 56, 46, 48, 46, 57, 57, 46, 57, 57, 11, 110, 101, 116, 101, 97, 115, 101, 99, 111, 114, 101, 6, 49, 46, 49, 50, 46, 50, 7, 97, 110, 116, 105, 109, 111, 100, 3, 50, 46, 48, 11, 102, 111, 97, 109, 102, 105, 120, 99, 111, 114, 101, 5, 55, 46, 55, 46, 52, 10, 110, 101, 116, 119, 111, 114, 107, 109, 111, 100, 6, 49, 46, 49, 49, 46, 50, 7, 102, 111, 97, 109, 102, 105, 120, 9, 64, 86, 69, 82, 83, 73, 79, 78, 64, 5, 102, 111, 114, 103, 101, 12, 49, 52, 46, 50, 51, 46, 53, 46, 50, 55, 54, 56, 13, 102, 114, 105, 101, 110, 100, 112, 108, 97, 121, 109, 111, 100, 3, 49, 46, 48, 4, 108, 105, 98, 115, 5, 49, 46, 48, 46, 50, 9, 102, 105, 108, 116, 101, 114, 109, 111, 100, 3, 49, 46, 48, 7, 103, 101, 114, 109, 109, 111, 100, 5, 51, 46, 52, 46, 50, 9, 112, 114, 111, 109, 111, 116, 105, 111, 110, 14, 49, 46, 48, 46, 48, 45, 83, 78, 65, 80, 83, 72, 79, 84};
    public static final byte[] REGISTER_CHANNEL = new byte[]{70, 77, 76, 124, 72, 83, 0, 70, 77, 76, 0, 70, 77, 76, 124, 77, 80, 0, 70, 77, 76, 0, 97, 110, 116, 105, 109, 111, 100, 0, 67, 104, 97, 116, 86, 101, 120, 86, 105, 101, 119, 0, 66, 97, 115, 101, 54, 52, 86, 101, 120, 86, 105, 101, 119, 0, 72, 117, 100, 66, 97, 115, 101, 54, 52, 86, 101, 120, 86, 105, 101, 119, 0, 70, 79, 82, 71, 69, 0, 103, 101, 114, 109, 112, 108, 117, 103, 105, 110, 45, 110, 101, 116, 101, 97, 115, 101, 0, 86, 101, 120, 86, 105, 101, 119, 0, 104, 121, 116, 48, 0, 97, 114, 109, 111, 117, 114, 101, 114, 115, 0, 112, 114, 111, 109, 111, 116, 105, 111, 110};


    private byte[] data;
    private int size;

    private static void sendToServer(PacketBuffer buffer) {
        PacketUtils.sendToServer("germmod-netease", buffer);
    }

    @Override
    public String getChannel() {
        return "germplugin-netease";
    }

    @Override
    public void process(ByteBuf byteBuf) {
        PacketBuffer packetBuffer1 = new PacketBuffer(byteBuf);

        int id = packetBuffer1.readInt();
        switch (id) {
            case -1: {
                final boolean needResize = packetBuffer1.readBoolean();
                final int newSize = packetBuffer1.readInt();
                final boolean isLast = packetBuffer1.readBoolean();
                final byte[] nextArray = packetBuffer1.readByteArray();

                if (needResize) {
                    data = new byte[newSize];
                }

                System.arraycopy(nextArray, 0, data, size, nextArray.length);
                size += nextArray.length;

                if (isLast) {
                    ByteBuf byteBufs = Unpooled.wrappedBuffer(data);
                    final SPacketCustomPayload newWrapper = new SPacketCustomPayload("germplugin-netease", new PacketBuffer(byteBufs));

                    final EventPacket packetReceiveEvent = new EventPacket(newWrapper);

                    EventManager.instance.call(packetReceiveEvent);

                    if (!packetReceiveEvent.isCancelled())
                        newWrapper.processPacket(mc.getConnection());
                }

                break;
            }
            case 73: {
                final String type = packetBuffer1.readStringFromBuffer(32767);
                final String name = packetBuffer1.readStringFromBuffer(32767);
                final String data = packetBuffer1.readStringFromBuffer(99999999);

                if (type.equalsIgnoreCase("gui")) {
                    if (name.equalsIgnoreCase("mainmenu")) {
                        final PacketBuffer newData = new PacketBuffer(Unpooled.buffer());

                        newData.writeInt(4);
                        newData.writeInt(0);
                        newData.writeInt(0);
                        newData.writeString("mainmenu");
                        newData.writeString("mainmenu");
                        newData.writeString("mainmenu");

                        reset();

                        sendToServer(newData);
                    }
                }
                break;
            }
            case 76: {
                String string = byteBuf.toString(Charsets.UTF_8);
                if (!string.contains("mainmenu")) break;
                mc.displayGuiScreen(new GermUI());
                break;
            }
            case 72: {
                final PacketBuffer data = new PacketBuffer(Unpooled.buffer());

                reset();

                data.writeInt(16);
                data.writeString("3.4.2");
                data.writeString(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));

                sendToServer(data);
            }
        }
    }

    public void reset() {
        data = null;
        size = 0;
    }

    public static byte[] buildJoinGamePacket(int entry, String sid) {
        sid = "{\"entry\":" + entry + ",\"sid\":\"" + (String)sid + "\"}";
        byte[] bytes = new byte[joinGame1.length + ((String)sid).getBytes().length + 2];
        System.arraycopy(joinGame1, 0, bytes, 0, joinGame1.length);
        bytes[GermManager.joinGame1.length] = (byte)(48 + entry);
        bytes[GermManager.joinGame1.length + 1] = (byte)((String)sid).length();
        System.arraycopy(((String)sid).getBytes(), 0, bytes, joinGame1.length + 2, ((String)sid).getBytes().length);
        return bytes;
    }

    public static void sendJoin(int num, String sid) {
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(openGUI);
        CPacketCustomPayload packetIn1 = new CPacketCustomPayload("germmod-netease", new PacketBuffer(buf2));
        Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(packetIn1);
        ByteBuf buf1 = Unpooled.buffer();
        byte[] bytes = GermManager.buildJoinGamePacket(num, sid);
        buf1.writeBytes(bytes);
        CPacketCustomPayload packetIn = new CPacketCustomPayload("germmod-netease", new PacketBuffer(buf1));
        Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(packetIn);
        ++num;
    }

    /*
    static {
        ArrayList<GermMenuItem> germMenuItemList1 = new ArrayList<GermMenuItem>();
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-dalu", "练习场"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-solo", "8队单人 绝杀模式"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-double", "8队双人 绝杀模式"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-team", "4队4人 绝杀模式"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bwxp16new", "无限火力16"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bwxp-32", "无限火力32"));
        hytMainMenuItems.put("起床战争", germMenuItemList1);
        ArrayList<GermMenuItem> germMenuItemList2 = new ArrayList<GermMenuItem>();
        germMenuItemList2.add(new GermMenuItem("SKYWAR/nskywar", "空岛战争 单人"));
        germMenuItemList2.add(new GermMenuItem("SKYWAR/nskywar-double", "空岛战争 双人"));
        hytMainMenuItems.put("空岛战争", germMenuItemList2);
        ArrayList<GermMenuItem> germMenuItemList3 = new ArrayList<GermMenuItem>();
        germMenuItemList3.add(new GermMenuItem("FIGHT/bihusuo", "废土"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/pubg-kit", "吃鸡荒野"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/kb-game", "职业战争"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/arenaPVP", "竞技场（等级限制）"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/the-pit", "天坑之战"));
        hytMainMenuItems.put("个人竞技", germMenuItemList3);
        ArrayList<GermMenuItem> germMenuItemList4 = new ArrayList<GermMenuItem>();
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/csbwxp-32", "枪械起床"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/bwkitxp-32", "职业无限火力起床"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/anni", "核心战争"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/battlewalls", "战墙"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/skygiants", "巨人战争"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/pubg-solo", "吃鸡单人"));
        hytMainMenuItems.put("团队竞技", germMenuItemList4);
        ArrayList<GermMenuItem> germMenuItemList5 = new ArrayList<GermMenuItem>();
        germMenuItemList5.add(new GermMenuItem("SURVIVE/oneblock", "单方块"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zskyblock", "空岛"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zjyfy", "监狱风云"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/xianjing", "仙境"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zuanshi", "钻石大陆"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zlysc", "龙域生存"));
        hytMainMenuItems.put("生存游戏", germMenuItemList5);
        ArrayList<GermMenuItem> germMenuItemList6 = new ArrayList<GermMenuItem>();
        germMenuItemList6.add(new GermMenuItem("LEISURE/tower", "守卫水晶"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/mg-game", "小游戏派对"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/sq-team", "抢羊大作战"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/stackgame", "叠叠乐"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/hp-game", "烫手山芋"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/ww-game", "狼人杀"));
        hytMainMenuItems.put("休闲游戏", germMenuItemList6);
    }

     */
}
