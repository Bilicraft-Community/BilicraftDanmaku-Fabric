package com.bilicraft.danmaku.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class RenderUtils {
    public static final float fW = 0.00393700787401574803149606299213F;// 254
    public static final float fH = 0.00357142857142857142857142857143F;// 280

    public static void drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1, float r, float g, float b, float a) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrices, x0, y1, z).texture(u0, v1).color(r, g, b, a).next();
        bufferBuilder.vertex(matrices, x1, y1, z).texture(u1, v1).color(r, g, b, a).next();
        bufferBuilder.vertex(matrices, x1, y0, z).texture(u1, v0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrices, x0, y0, z).texture(u0, v0).color(r, g, b, a).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public static void drawRectTexture(MatrixStack matrixStack, double x, double y, int w, int h, double u, double v) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f matrices = matrixStack.peek().getPositionMatrix();
        bufferBuilder.vertex(matrices,(float)x + 0, (float)y + h, 0).texture( ((float) (u + 0) * fW), ((float) (v + h) * fH)).next();
        bufferBuilder.vertex(matrices,(float)x + w, (float)y + h, 0).texture( ((float) (u + w) * fW), ((float) (v + h) * fH)).next();
        bufferBuilder.vertex(matrices,(float)x + w, (float)y + 0, 0).texture( ((float) (u + w) * fW), ((float) (v + 0) * fH)).next();
        bufferBuilder.vertex(matrices,(float)x + 0, (float)y + 0, 0).texture( ((float) (u + 0) * fW), ((float) (v + 0) * fH)).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public static void bindTexture(Identifier identifier) {
        RenderSystem.setShaderTexture(0, identifier);
    }
}

