package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.Rebound;

import java.io.InputStream;

public class ResourceName implements Comparable<ResourceName> {
    private final String namespace;
    private final String resourceName;

    public ResourceName(String combined)
    {
        String[] split = combined.split(":");
        if (split.length > 2) Rebound.getLogger().warning("Trying to create a ResourceName from the invalid identifier " + combined);

        if (split.length == 1) {
            this.namespace = Rebound.NAMESPACE;
            this.resourceName = split[0];
        } else {
            this.namespace = split[0];
            this.resourceName = split[1];
        }
    }

    public ResourceName(String namespace, String resourceName) {
        this.namespace = namespace;
        this.resourceName = resourceName;
    }

    public ResourceName append(String suffix) {
        return new ResourceName(this.namespace, this.resourceName + suffix);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getResourceName() {
        return resourceName;
    }

    public InputStream getInputStream() {
        return ClassLoader.getSystemClassLoader().getResourceAsStream("assets/" + namespace + "/" + resourceName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        else if (!(other instanceof ResourceName)) return false;
        ResourceName location = (ResourceName)other;
        return location.namespace.equals(this.namespace) && location.resourceName.equals(this.resourceName);
    }

    @Override
    public int hashCode() {
        int namespaceHash = this.namespace.hashCode();
        return 31 * namespaceHash + this.resourceName.hashCode();
    }

    @Override
    public int compareTo(ResourceName other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return namespace + ":" + resourceName;
    }
}
