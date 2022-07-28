package greekfantasy.client.screen.radial;

import greekfantasy.client.screen.InstrumentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

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
public class NoteRadialMenuItem extends RadialMenuItem {

    private final InstrumentScreen screen;
    private final Component text;
    private final int note;
    private final int color;

    public NoteRadialMenuItem(InstrumentScreen owner, Component text, int note) {
        this(owner, text, note, 0xFFFFFFFF);
    }

    public NoteRadialMenuItem(InstrumentScreen owner, Component text, int note, int color) {
        super(owner);
        this.screen = owner;
        this.text = text;
        this.note = note;
        // calculate color using the equation found in NoteParticle
        double noteColorData = ((float) Math.pow(2.0D, (double) (note) / 24.0D));
        int noteColorR = (int) (255.0F * Math.max(0.0F, Mth.sin(((float) noteColorData + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F));
        int noteColorG = (int) (255.0F * Math.max(0.0F, Mth.sin(((float) noteColorData + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F));
        int noteColorB = (int) (255.0F * Math.max(0.0F, Mth.sin(((float) noteColorData + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F));
        this.color = (0xff) << 24 | (noteColorR & 0xff) << 16 | (noteColorG & 0xff) << 8 | (noteColorB & 0xff);
    }

    public Component getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void draw(DrawingContext context) {
        // draw text
        String textString = text.getString();
        float x = context.x - context.fontRenderer.width(textString) / 2.0f;
        float y = context.y - context.fontRenderer.lineHeight / 2.0F;
        context.fontRenderer.drawShadow(context.poseStack, textString, x, y, color);
    }

    public void setHovered(boolean hovered) {
        // notify screen to play sound when first hovered
        if (hovered && !this.isHovered()) {
            screen.playNote(this.note);
        }
        super.setHovered(hovered);
    }

    @Override
    public void drawTooltips(DrawingContext context) {
        // nothing to do (yet)
    }
}