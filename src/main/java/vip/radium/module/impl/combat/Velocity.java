package vip.radium.module.impl.combat;

import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import vip.radium.event.impl.packet.PacketReceiveEvent;
import vip.radium.module.Module;
import vip.radium.module.ModuleCategory;
import vip.radium.module.ModuleInfo;
import vip.radium.property.impl.DoubleProperty;
import vip.radium.property.impl.Representation;
import vip.radium.utils.ServerUtils;
import vip.radium.utils.Wrapper;

import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(label = "Velocity", category = ModuleCategory.COMBAT)
public final class Velocity extends Module {

    private final DoubleProperty horizontalPercentProperty = new DoubleProperty("Horizontal", 0, 0,
            100, 0.5, Representation.PERCENTAGE);
    private final DoubleProperty verticalPercentProperty = new DoubleProperty("Vertical", 0, 0,
            100, 0.5, Representation.PERCENTAGE);

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = e -> {
        Packet<?> packet = e.getPacket();
        if (packet instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) packet).getEntityID() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
                S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity) packet;
                if(horizontalPercentProperty.getValue() == 0f && verticalPercentProperty.getValue() == 0f){
                    e.setCancelled(true);
                }else{
                    velocityPacket.motionX *= (horizontalPercentProperty.getValue() / 100);
                    velocityPacket.motionY *= (verticalPercentProperty.getValue() / 100);
                    velocityPacket.motionZ *= (horizontalPercentProperty.getValue() / 100);
                }
            }

            if (e.getPacket() instanceof S27PacketExplosion) {
                e.setCancelled(true);
            }
        }
        if(e.getPacket() instanceof S27PacketExplosion){
            e.setCancelled(true);
        }
    };
}
