package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.Rebound;

import java.io.InputStream;

public class ResourceLocation implements Comparable<ResourceLocation> {
    private final String namespace;
    private final String resource;

    public ResourceLocation(String combined)
    {
        String[] split = combined.split(":");
        if (split.length > 2) System.err.println("Trying to create a ResourceLocation from the invalid identifier " + combined);

        if (split.length == 1) {
            this.namespace = Rebound.NAMESPACE;
            this.resource = split[0];
        } else {
            this.namespace = split[0];
            this.resource = split[1];
        }
    }

    public ResourceLocation(String namespace, String resource) {
        this.namespace = namespace;
        this.resource = resource;
    }

    public ResourceLocation append(String suffix) {
        return new ResourceLocation(this.namespace, this.resource + suffix);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getResource() {
        return resource;
    }

    public InputStream getInputStream() {
        return ClassLoader.getSystemClassLoader().getResourceAsStream("assets/" + namespace + "/" + resource);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        else if (!(other instanceof ResourceLocation)) return false;
        ResourceLocation location = (ResourceLocation)other;
        return location.namespace.equals(this.namespace) && location.resource.equals(this.resource);
    }

    @Override
    public int hashCode() {
        int namespaceHash = this.namespace.hashCode();
        return 31 * namespaceHash + this.resource.hashCode();
    }

    @Override
    public int compareTo(ResourceLocation other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return namespace + ":" + resource;
    }
}
