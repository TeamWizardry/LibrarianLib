package com.teamwizardry.librarianlib.courier

import dev.thecodewarrior.prism.annotation.Refract

public interface CourierPacket {
    /**
     * Writes the packet data to the buffer. Any [@Refract][Refract] annotated fields do NOT need to be written in this
     * method.
     */
    public fun writeBytes(buffer: CourierBuffer) {}

    /**
     * Reads the packet data from the buffer. Any [@Refract][Refract] annotated fields do NOT need to be read in this
     * method.
     */
    public fun readBytes(buffer: CourierBuffer) {}
}