package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.Rebound;

import java.io.InputStream;

public class ResourceLocation {
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
    public String toString() {
        return namespace + ":" + resource;
    }
}
