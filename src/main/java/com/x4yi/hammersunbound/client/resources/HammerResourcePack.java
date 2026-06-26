package com.x4yi.hammersunbound.client.resources;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
public class HammerResourcePack implements IResourcePack {
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        ResourceLocation redirected = redirect(location);
        String path = "/assets/" + redirected.getResourceDomain() + "/" + redirected.getResourcePath();
        InputStream stream = com.x4yi.hammersunbound.HammersUnbound.class.getResourceAsStream(path);
        if (stream == null) {
            throw new java.io.FileNotFoundException("Resource not found: " + path);
        }
        return stream;
    }
    @Override
    public boolean resourceExists(ResourceLocation location) {
        ResourceLocation redirected = redirect(location);
        String path = "/assets/" + redirected.getResourceDomain() + "/" + redirected.getResourcePath();
        try (InputStream stream = com.x4yi.hammersunbound.HammersUnbound.class.getResourceAsStream(path)) {
            return stream != null;
        } catch (IOException e) {
            return false;
        }
    }
    private ResourceLocation redirect(ResourceLocation location) {
        if ("hammersunbound".equals(location.getResourceDomain())) {
            String path = location.getResourcePath();
            if (path.startsWith("textures/items/")) {
                String newPath = "textures/item/" + path.substring("textures/items/".length());
                return new ResourceLocation(location.getResourceDomain(), newPath);
            }
        }
        return location;
    }
    @Override
    public Set<String> getResourceDomains() {
        return Collections.singleton("hammersunbound");
    }
    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }
    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }
    @Override
    public String getPackName() {
        return "Hammers Unbound Redirect Pack";
    }
}