package com.romiiis.configuration;

/**
 * Record representing a resource header with its name and data.
 *
 * @param resourceName the name of the resource
 * @param resourceData the data of the resource as a byte array
 */
public record ResourceHeader(String resourceName, byte[] resourceData) {
}
