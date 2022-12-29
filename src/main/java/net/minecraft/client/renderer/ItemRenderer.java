package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import vip.radium.module.impl.visuals.Animations;
import vip.radium.module.impl.esp.Chams;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    public float angle;
    private ItemStack itemToRender;
    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    public float equippedProgress;
    public float prevEquippedProgress;
    /**
     * The index of the currently held item (0-8, or -1 if not yet updated)
     */
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GL11.glPushMatrix();

            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GL11.glScalef(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }

            GL11.glPopMatrix();
        }
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void func_178101_a(float angle, float p_178101_2_) {
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(p_178101_2_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer) {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double) clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);

        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }

        float f = (float) (i & 65535);
        float f1 = (float) (i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GL11.glRotatef((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private float func_178100_c(float p_178100_1_) {
        float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GL11.glPushMatrix();
        GL11.glRotatef(54.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(64.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-62.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GL11.glPushMatrix();
        GL11.glRotatef(92.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(41.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GL11.glPopMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer) render;

        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178097_4_ * (float) Math.PI);
        GL11.glTranslatef(f, f1, f2);
        float f3 = this.func_178100_c(p_178097_2_);
        GL11.glTranslatef(0.0F, 0.04F, -0.72F);
        GL11.glTranslatef(0.0F, p_178097_3_ * -1.2F, 0.0F);
        GL11.glTranslatef(0.0F, f3 * -0.5F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float) Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        GL11.glRotatef(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(0.38F, 0.38F, 0.38F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
        GL11.glScalef(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);

        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    private void renderArmInFirstPerson(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
        boolean handChams = Chams.shouldRenderHand();
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI * 2.0F);
        float f2 = -0.4F * MathHelper.sin(p_178095_3_ * (float) Math.PI);
        GL11.glTranslatef(f, f1, f2);
        GL11.glTranslatef(0.64000005F, -0.6F, -0.72F);
        GL11.glTranslatef(0.0F, p_178095_2_ * -0.6F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        float f3 = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float) Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        GL11.glRotatef(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
        GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(5.6F, 0.0F, 0.0F);
        GlStateManager.disableCull();
        Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderPlayer = (RenderPlayer) render;
        if (handChams)
            Chams.preHandRender();
        else {
            this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        renderPlayer.renderRightArm(this.mc.thePlayer);
        if (handChams) Chams.postHandRender();
        GlStateManager.enableCull();
    }

    private void func_178105_d(float p_178105_1_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
        GL11.glTranslatef(f, f1, f2);
    }

    private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_) {
        float f = (float) clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0F;
        float f1 = f / (float) this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);

        if (f1 >= 0.8F) {
            f2 = 0.0F;
        }

        GL11.glTranslatef(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float) Math.pow(f1, 27.0D);
        GL11.glTranslatef(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
        GL11.glRotatef(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     *
     * @param equipProgress The progress of the animation to equip (raise from out of frame) while switching held items.
     * @param swingProgress The progress of the arm swing animation.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GL11.glTranslatef(0.56F, -0.52F, -0.72F);
        GL11.glTranslatef(0.0F, equipProgress * -0.6F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        if (swingProgress > 0.0) {
            float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            GL11.glRotatef(f * -20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        }
        float scale = 0.4F;
        if (Animations.getInstance().isEnabled())
            scale *= Animations.getInstance().itemScale.getValue().floatValue();
        GL11.glScalef(scale, scale, scale);
    }

    private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer) {
        GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
        float f = (float) this.itemToRender.getMaxItemUseDuration() - ((float) clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        if (f1 > 0.1F) {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GL11.glTranslatef(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }

        GL11.glTranslatef(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GL11.glScalef(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }

    private void func_178103_d() {
        GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
        GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     *
     * @param partialTicks The amount of time passed during the current tick, ranging from 0 to 1.
     */
    public void renderItemInFirstPerson(float partialTicks) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            float f = 1.0F - (this.prevEquippedProgress +
                    (this.equippedProgress - this.prevEquippedProgress)
                            * partialTicks);
            EntityPlayerSP abstractclientplayer = this.mc.thePlayer;
            float f1 = abstractclientplayer.getSwingProgress(partialTicks);
            float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            this.func_178101_a(f2, f3);
            this.func_178109_a(abstractclientplayer);
            this.func_178110_a(abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GL11.glPushMatrix();

            if (this.itemToRender != null) {
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    this.renderItemMap(abstractclientplayer, f2, f, f1);
                } else if (abstractclientplayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();

                    switch (enumaction) {
                        case NONE:
                            this.transformFirstPersonItem(f, f1);
                            break;

                        case EAT:
                        case DRINK:
                            this.func_178104_a(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(f, f1);
                            break;

                        case BLOCK:
                            float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                            Animations animations = Animations.getInstance();
                            if (animations.isEnabled()) {
                                float eqiupProgress;
                                if (animations.equipProgressProperty.getValue())
                                    eqiupProgress = f / animations.equipProgMultProperty.getValue().floatValue();
                                else
                                    eqiupProgress = 0.0F;
                                GL11.glTranslated(
                                        animations.xPosProperty.getValue(),
                                        animations.yPosProperty.getValue(),
                                        animations.zPosProperty.getValue());
                                switch (animations.animationModeProperty.getValue()) {
                                    case LOL:
                                        this.transformFirstPersonItem(eqiupProgress, 0.0F);
                                        GL11.glRotatef(-var9 * 30.0F, 1.0F, 0.0F, 0.0F);
                                        GL11.glRotatef(-var9 * 10.0F, 0.0F, 1.0F, 0.0F);
                                        break;
                                    case LUL:
                                        this.transformFirstPersonItem(eqiupProgress, 0.0F);
                                        GL11.glRotatef(-var9 * 30.0F, 1.0F, 0.0F, 0.0F);
                                        break;
                                    case LEL:
                                        this.transformFirstPersonItem(eqiupProgress, 0.0F);
                                        GL11.glRotatef(-var9 * 10.0F, 1.0F, 0.0F, 0.0F);
                                        GL11.glRotatef(-var9 * 30.0F, 1.0F, 0.0F, 0.0F);
                                        break;
                                    case LIL:
                                        this.transformFirstPersonItem(eqiupProgress, 0.0F);
                                        GL11.glRotatef(-var9 * 35.0F, 1.0F, 0.0F, 0.0F);
                                        break;
                                    case EXHIBIBI:
                                        this.transformFirstPersonItem(eqiupProgress, f1);
                                        GL11.glRotatef(var9 * 30.0F / 2.0F, -var9, -0.0F, 9.0F);
                                        GL11.glRotatef(var9 * 40.0F, 1.0F, -var9 / 2.0F, -0.0F);
                                        break;
                                    case EXHIBOBO:
                                        this.transformFirstPersonItem(eqiupProgress, 0.0f);
                                        GL11.glTranslatef(0.1F, 0.4F, -0.1F);
                                        GL11.glRotated(-var9 * 20.0F, var9 / 2, 0.0F, 9.0F);
                                        GL11.glRotated(-var9 * 50.0F, 0.8F, var9 / 2, 0F);
                                        break;
                                }
                                this.func_178103_d();
                            } else {
                                this.transformFirstPersonItem(f, 0.0F);
                                this.func_178103_d();
                            }
                            break;

                        case BOW:
                            this.transformFirstPersonItem(f, f1);
                            this.func_178098_a(partialTicks, abstractclientplayer);
                    }
                } else {
                    angle = 0.0f;
                    this.func_178105_d(f1);
                    this.transformFirstPersonItem(f, f1);
                }

                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else {
                this.renderArmInFirstPerson(abstractclientplayer, f, f1);
            }

            GL11.glPopMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double) (((float) ((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double) entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);

                if (iblockstate1.getBlock().isVisuallyOpaque()) {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            if (iblockstate.getBlock().getRenderType() != -1) {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);

                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, Float.valueOf(partialTicks), object, iblockstate, blockpos)) {
                    this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1F;
        GL11.glColor4f(0.1F, 0.1F, 0.1F, 0.5F);
        GL11.glPushMatrix();
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = p_178108_2_.getMinU();
        float f7 = p_178108_2_.getMaxU();
        float f8 = p_178108_2_.getMinV();
        float f9 = p_178108_2_.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(f7, f9).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(f6, f9).endVertex();
        worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(f6, f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(f7, f8).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     */
    private void renderWaterOverlayTexture(float p_78448_1_) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(p_78448_1_);
            GL11.glColor4f(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glPushMatrix();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson(float p_78442_1_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i) {
            GL11.glPushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;
            float f8 = f7 + f;
            float f9 = -0.5F;
            GL11.glTranslatef((float) (-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GL11.glRotatef((float) (i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
            worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
            worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
            worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayer entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;

        if (this.itemToRender != null && itemstack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
                if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists()) {
                    boolean flag1 = Reflector.callBoolean(this.itemToRender.getItem(), Reflector.ForgeItem_shouldCauseReequipAnimation, this.itemToRender, itemstack, Boolean.valueOf(this.equippedItemSlot != entityplayer.inventory.currentItem));

                    if (!flag1) {
                        this.itemToRender = itemstack;
                        this.equippedItemSlot = entityplayer.inventory.currentItem;
                        return;
                    }
                }

                flag = true;
            }
        } else flag = this.itemToRender != null || itemstack != null;

        float f2 = 0.4F;
        float f = flag ? 0.0F : 1.0F;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;

        if (this.equippedProgress < 0.1F) {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;

            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0F;
    }
}
