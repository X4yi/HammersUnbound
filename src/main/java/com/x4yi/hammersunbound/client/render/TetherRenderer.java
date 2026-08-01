package com.x4yi.hammersunbound.client.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;
public class TetherRenderer {
    private static final ResourceLocation TEX_MUSCLE = new ResourceLocation("hammersunbound:textures/particle/tether_muscle.png");
    private static final ResourceLocation TEX_GORE = new ResourceLocation("hammersunbound:textures/particle/tether_gore.png");
    public static class TetherNode {
        public Vec3d pos;
        public Vec3d prevPos;
        public Vec3d velocity = Vec3d.ZERO;
        public boolean onGround = false;
        public TetherNode(Vec3d pos) {
            this.pos = pos;
            this.prevPos = pos;
        }
    }
    private final List<TetherNode> nodes = new ArrayList<>();
    private int numSegments = 0;
    private float time = 0;
    public void updatePhysics(Entity player, Entity target, float maxDist, int madness) {
        World world = player.world;
        time += 1.0f + (madness / 50.0f);
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d pPos = new Vec3d(player.posX, player.posY + player.height / 2.0, player.posZ);
        if (player == mc.player && mc.gameSettings.thirdPersonView == 0) {
            Vec3d look = player.getLookVec();
            Vec3d right = look.crossProduct(new Vec3d(0, 1, 0)).normalize();
            pPos = pPos.add(new Vec3d(0, -0.4, 0)).add(right.scale(0.5));
        }
        Vec3d tPos = new Vec3d(target.posX, target.posY + target.height / 2.0, target.posZ);
        double currentDist = pPos.distanceTo(tPos);
        int targetSegments = Math.max(5, (int)(currentDist * 1.5));
        if (targetSegments > 40) targetSegments = 40;
        if (nodes.isEmpty()) {
            numSegments = targetSegments;
            for (int i = 0; i < numSegments; i++) {
                float t = (float) i / (numSegments - 1);
                Vec3d interp = pPos.add(tPos.subtract(pPos).scale(t));
                nodes.add(new TetherNode(interp));
            }
        } else if (Math.abs(nodes.size() - targetSegments) > 1) {
            List<TetherNode> newNodes = new ArrayList<>();
            for (int i = 0; i < targetSegments; i++) {
                float t = (float) i / (targetSegments - 1);
                float oldIndex = t * (nodes.size() - 1);
                int idx1 = (int) Math.floor(oldIndex);
                int idx2 = (int) Math.ceil(oldIndex);
                float frac = oldIndex - idx1;
                Vec3d pos1 = nodes.get(idx1).pos;
                Vec3d pos2 = nodes.get(idx2).pos;
                Vec3d newPos = pos1.add(pos2.subtract(pos1).scale(frac));
                Vec3d vel1 = nodes.get(idx1).velocity;
                Vec3d vel2 = nodes.get(idx2).velocity;
                Vec3d newVel = vel1.add(vel2.subtract(vel1).scale(frac));
                TetherNode n = new TetherNode(newPos);
                n.velocity = newVel;
                newNodes.add(n);
            }
            nodes.clear();
            nodes.addAll(newNodes);
            numSegments = targetSegments;
        }
        nodes.get(0).prevPos = nodes.get(0).pos;
        nodes.get(nodes.size() - 1).prevPos = nodes.get(nodes.size() - 1).pos;
        nodes.get(0).pos = pPos;
        nodes.get(nodes.size() - 1).pos = tPos;
        double segmentLength = pPos.distanceTo(tPos) / Math.max(1, numSegments - 1);
        for (int i = 1; i < numSegments - 1; i++) {
            TetherNode node = nodes.get(i);
            node.prevPos = node.pos;
            node.velocity = node.velocity.add(new Vec3d(0, -0.05, 0));
            node.velocity = node.velocity.scale(0.7);
            node.pos = node.pos.add(node.velocity);
            BlockPos bp = new BlockPos(node.pos);
            net.minecraft.block.state.IBlockState state = world.getBlockState(bp);
            net.minecraft.util.math.AxisAlignedBB aabb = state.getCollisionBoundingBox(world, bp);
            if (aabb != net.minecraft.block.Block.NULL_AABB && aabb != null) {
                double topY = bp.getY() + aabb.maxY;
                if (node.pos.y < topY) {
                    node.pos = new Vec3d(node.pos.x, topY, node.pos.z);
                    node.velocity = new Vec3d(node.velocity.x * 0.5, 0, node.velocity.z * 0.5);
                    node.onGround = true;
                } else {
                    node.onGround = false;
                }
            } else {
                node.onGround = false;
            }
        }
        for (int iter = 0; iter < 10; iter++) {
            for (int i = 0; i < numSegments - 1; i++) {
                TetherNode n1 = nodes.get(i);
                TetherNode n2 = nodes.get(i + 1);
                Vec3d diff = n2.pos.subtract(n1.pos);
                double dist = diff.lengthVector();
                if (dist == 0) continue;
                double error = dist - segmentLength;
                Vec3d correction = diff.normalize().scale(error * 0.5);
                if (i != 0) n1.pos = n1.pos.add(correction);
                if (i + 1 != numSegments - 1) n2.pos = n2.pos.subtract(correction);
            }
        }
    }
    public void render(Entity renderView, float partialTicks, float maxDist, double interpPosX, double interpPosY, double interpPosZ) {
        if (nodes.size() < 2) return;
        Minecraft mc = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(true);
        float pitch = renderView.prevRotationPitch + (renderView.rotationPitch - renderView.prevRotationPitch) * partialTicks;
        float yaw = renderView.prevRotationYaw + (renderView.rotationYaw - renderView.prevRotationYaw) * partialTicks;
        if (mc.gameSettings.thirdPersonView == 2) {
            yaw += 180.0F;
            pitch = -pitch;
        }
        float fCos = net.minecraft.util.math.MathHelper.cos(-pitch * 0.017453292F);
        float fSin = net.minecraft.util.math.MathHelper.sin(-pitch * 0.017453292F);
        float cosYaw = net.minecraft.util.math.MathHelper.cos(yaw * 0.017453292F);
        float sinYaw = net.minecraft.util.math.MathHelper.sin(yaw * 0.017453292F);
        float camX = -sinYaw * fCos;
        float camY = fSin;
        float camZ = cosYaw * fCos;
        double currentDist = nodes.get(0).pos.distanceTo(nodes.get(nodes.size()-1).pos);
        float tension = (float) Math.min(1.0, currentDist / maxDist);
        Vec3d[] rights = new Vec3d[nodes.size()];
        Vec3d[] ups = new Vec3d[nodes.size()];
        float[] radii = new float[nodes.size()];
        Vec3d[] renderPos = new Vec3d[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            TetherNode n = nodes.get(i);
            Vec3d p = new Vec3d(n.prevPos.x + (n.pos.x - n.prevPos.x)*partialTicks,
                                n.prevPos.y + (n.pos.y - n.prevPos.y)*partialTicks,
                                n.prevPos.z + (n.pos.z - n.prevPos.z)*partialTicks);
            renderPos[i] = new Vec3d(p.x - interpPosX, p.y - interpPosY, p.z - interpPosZ);
        }
        for (int i = 0; i < nodes.size(); i++) {
            Vec3d prev = i > 0 ? renderPos[i - 1] : renderPos[i];
            Vec3d next = i < nodes.size() - 1 ? renderPos[i + 1] : renderPos[i];
            if (i == 0 && nodes.size() > 1) next = renderPos[1];
            if (i == nodes.size() - 1 && nodes.size() > 1) prev = renderPos[i - 1];
            Vec3d dir = next.subtract(prev).normalize();
            if (dir.lengthVector() < 0.001) dir = new Vec3d(0, 1, 0);
            Vec3d up = new Vec3d(0, 1, 0);
            if (Math.abs(dir.y) > 0.99) up = new Vec3d(1, 0, 0);
            Vec3d right = dir.crossProduct(up).normalize();
            up = right.crossProduct(dir).normalize();
            rights[i] = right;
            ups[i] = up;
            float pulse = (float) Math.sin((time + i * 5) * 0.1f) * 0.5f + 0.5f;
            float w = 0.08f + pulse * 0.05f;
            if (tension > 0.8f) w *= (1.0f - (tension - 0.8f) * 2.0f);
            radii[i] = Math.max(0.02f, w);
        }
        for (int pass = 0; pass < 2; pass++) {
            if (pass == 0) mc.getTextureManager().bindTexture(TEX_MUSCLE);
            else mc.getTextureManager().bindTexture(TEX_GORE);
            if (pass == 1 && tension < 0.8f) continue;
            GlStateManager.disableCull();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (int i = 0; i < nodes.size() - 1; i++) {
                float v1 = -(time * 0.05f) + (i * 0.5f);
                float v2 = -(time * 0.05f) + ((i+1) * 0.5f);
                float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
                if (pass == 1) {
                    a = (tension - 0.8f) * 5.0f;
                    a = Math.min(1.0f, Math.max(0.0f, a));
                }
                Vec3d p1 = renderPos[i];
                Vec3d p2 = renderPos[i+1];
                Vec3d u1 = ups[i].scale(radii[i]);
                Vec3d r1 = rights[i].scale(radii[i]);
                Vec3d u2 = ups[i+1].scale(radii[i+1]);
                Vec3d r2 = rights[i+1].scale(radii[i+1]);
                buffer.pos(p1.x + u1.x + r1.x, p1.y + u1.y + r1.y, p1.z + u1.z + r1.z).tex(0, v1).color(r, g, b, a).endVertex();
                buffer.pos(p1.x + u1.x - r1.x, p1.y + u1.y - r1.y, p1.z + u1.z - r1.z).tex(1, v1).color(r, g, b, a).endVertex();
                buffer.pos(p2.x + u2.x - r2.x, p2.y + u2.y - r2.y, p2.z + u2.z - r2.z).tex(1, v2).color(r, g, b, a).endVertex();
                buffer.pos(p2.x + u2.x + r2.x, p2.y + u2.y + r2.y, p2.z + u2.z + r2.z).tex(0, v2).color(r, g, b, a).endVertex();
                buffer.pos(p1.x - u1.x - r1.x, p1.y - u1.y - r1.y, p1.z - u1.z - r1.z).tex(0, v1).color(r, g, b, a).endVertex();
                buffer.pos(p1.x - u1.x + r1.x, p1.y - u1.y + r1.y, p1.z - u1.z + r1.z).tex(1, v1).color(r, g, b, a).endVertex();
                buffer.pos(p2.x - u2.x + r2.x, p2.y - u2.y + r2.y, p2.z - u2.z + r2.z).tex(1, v2).color(r, g, b, a).endVertex();
                buffer.pos(p2.x - u2.x - r2.x, p2.y - u2.y - r2.y, p2.z - u2.z - r2.z).tex(0, v2).color(r, g, b, a).endVertex();
                buffer.pos(p1.x + r1.x - u1.x, p1.y + r1.y - u1.y, p1.z + r1.z - u1.z).tex(0, v1).color(r, g, b, a).endVertex();
                buffer.pos(p1.x + r1.x + u1.x, p1.y + r1.y + u1.y, p1.z + r1.z + u1.z).tex(1, v1).color(r, g, b, a).endVertex();
                buffer.pos(p2.x + r2.x + u2.x, p2.y + r2.y + u2.y, p2.z + r2.z + u2.z).tex(1, v2).color(r, g, b, a).endVertex();
                buffer.pos(p2.x + r2.x - u2.x, p2.y + r2.y - u2.y, p2.z + r2.z - u2.z).tex(0, v2).color(r, g, b, a).endVertex();
                buffer.pos(p1.x - r1.x + u1.x, p1.y - r1.y + u1.y, p1.z - r1.z + u1.z).tex(0, v1).color(r, g, b, a).endVertex();
                buffer.pos(p1.x - r1.x - u1.x, p1.y - r1.y - u1.y, p1.z - r1.z - u1.z).tex(1, v1).color(r, g, b, a).endVertex();
                buffer.pos(p2.x - r2.x - u2.x, p2.y - r2.y - u2.y, p2.z - r2.z - u2.z).tex(1, v2).color(r, g, b, a).endVertex();
                buffer.pos(p2.x - r2.x + u2.x, p2.y - r2.y + u2.y, p2.z - r2.z + u2.z).tex(0, v2).color(r, g, b, a).endVertex();
            }
            tessellator.draw();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}