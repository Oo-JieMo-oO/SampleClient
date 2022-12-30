package vip.xiatian.cnfont;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public abstract class FontLoaders {
    public static FontDrawer F18;
    public static FontDrawer F16;

    public static void initFonts() {
        F18 = getFont("misans", 18, true);
        F16 = getFont("misans", 16, true);
    }

    public static FontDrawer getFont(String name, int size, boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("radium/fonts/" + name + ".ttf")).getInputStream()).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return new FontDrawer(font, antiAliasing);
    }
}
