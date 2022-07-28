package greekfantasy.client.screen.radial;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;

/**
 * Adapted from https://github.com/gigaherz/ToolBelt under the following license:
 * <p>
 * Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the author nor the
 * names of the contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public final class RadialMenuHelper {

    private static final float PRECISION = 2.5f / 360.0f;
    private static final double TWO_PI = 2.0 * Math.PI;

    public final int backgroundColor;
    public final int backgroundColorHover;

    private Screen screen;
    private DrawingContext.IDrawingHelper drawingHelper;
    private List<RadialMenuItem> items;
    private float radiusIn;
    private float radiusOut;
    private float itemRadius;

    public RadialMenuHelper(final Screen screen, List<RadialMenuItem> items,
                            final float radiusIn, final float radiusOut,
                            final int backgroundColor, final int backgroundColorHover) {
        this.screen = screen;
        this.drawingHelper = (poseStack, mouseX, mouseY) -> {};
        this.items = items;
        this.radiusIn = radiusIn;
        this.radiusOut = radiusOut;
        this.itemRadius = (radiusOut + radiusIn) / 2.0F;
        this.backgroundColor = backgroundColor;
        this.backgroundColorHover = backgroundColorHover;
    }

    public void drawBackground(PoseStack poseStack, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        iterateVisible((item, s, e) -> {
            int color = item.isHovered() ? backgroundColorHover : backgroundColor;
            drawPieArc(buffer, x, y, z, radiusIn, radiusOut, s, e, color);
        });
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void drawItems(PoseStack poseStack, int x, int y, float z, int width, int height, Font font, ItemRenderer itemRenderer)
    {
        iterateVisible((item, s, e) -> {
            float middle = (s + e) * 0.5f;
            float posX = x + itemRadius * (float) Math.cos(middle);
            float posY = y + itemRadius * (float) Math.sin(middle);

            DrawingContext context = new DrawingContext(poseStack, width, height, posX, posY, z, font, itemRenderer, drawingHelper);
            item.draw(context);
        });
    }

    private void iterateVisible(TriConsumer<RadialMenuItem, Float, Float> consumer) {
        int numItems = items.size();
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            RadialMenuItem item = items.get(i);
            consumer.accept(item, s, e);
        }
    }

    private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int color) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));

        angle = endAngle - startAngle;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = startAngle + i * slice;
            float angle2 = startAngle + (i + 1) * slice;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.vertex(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }

    public void processMouse(float x, float y, int mouseX, int mouseY) {
        int numItems = items.size();
        double a = Math.atan2(mouseY - y, mouseX - x);
        double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (numItems > 0) {
            double s0 = getAngleFor(0 - 0.5, numItems);
            double s1 = getAngleFor(numItems - 0.5, numItems);
            while (a < s0) {
                a += TWO_PI;
            }
            while (a >= s1) {
                a -= TWO_PI;
            }
        }

        int hovered = -1;
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            if (a >= s && a < e && d >= radiusIn && (d < radiusOut)) {
                hovered = i;
                break;
            }
        }
        setHovered(hovered);
    }

    private void setHovered(int which) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setHovered(i == which);
        }
    }

    private double getAngleFor(double i, int numItems) {
        if (numItems == 0)
            return 0;
        double angle = ((i / numItems) + 0.25) * TWO_PI + Math.PI;
        return angle;
    }
}
