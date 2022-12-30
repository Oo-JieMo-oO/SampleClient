package vip.radium.module.impl.movement;


import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import vip.radium.event.impl.packet.PacketReceiveEvent;
import vip.radium.module.Module;
import vip.radium.module.ModuleCategory;
import vip.radium.module.ModuleInfo;
import vip.radium.property.impl.DoubleProperty;
import vip.radium.property.impl.EnumProperty;
import vip.radium.property.impl.Representation;
import vip.radium.utils.MovementUtils;
import vip.xiatian.TimeHelper;

import java.util.ArrayList;

@ModuleInfo(label = "Anti Fall", category = ModuleCategory.MOVEMENT)
public final class AntiFall extends Module {

    public static TimeHelper timer = new TimeHelper();
    private DoubleProperty pullbackTime =new  DoubleProperty("pullbacktime",1000.0,1000.0,2000.0,100.0, Representation.DISTANCE);
    private final EnumProperty<NoVoidMode> noVoidModeProperty = new EnumProperty<>("Mode", NoVoidMode.PACKET);

    public double[] lastGroundPos = new double[3];
    public AntiFall() {
        setSuffixListener(noVoidModeProperty);
    }
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MovementUtils.isOnGround2(i)) {
                return false;
            }
        }
        return true;
    }
    @EventLink
    public final Listener<PacketReceiveEvent> onpacketPositionEvent = event -> {
        if (!packets.isEmpty() && Minecraft.getMinecraft().thePlayer.ticksExisted < 100)
            packets.clear();

        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = ((C03PacketPlayer) event.getPacket());
            if (isInVoid()) {
                event.setCancelled(true);
                packets.add(packet);

                if (timer.isDelayComplete(pullbackTime.getValue())) {
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                }
            } else {
                lastGroundPos[0] = Minecraft.getMinecraft().thePlayer.posX;
                lastGroundPos[1] = Minecraft.getMinecraft().thePlayer.posY;
                lastGroundPos[2] = Minecraft.getMinecraft().thePlayer.posZ;

                if (!packets.isEmpty()) {
                    for (C03PacketPlayer p : packets)
                       Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(p);
                    packets.clear();
                }
                timer.reset();
            }
        }
    };

    private enum NoVoidMode {
        PACKET, MOTION
    }
}
