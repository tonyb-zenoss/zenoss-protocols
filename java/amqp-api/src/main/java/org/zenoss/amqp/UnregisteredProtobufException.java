/*
 * This program is part of Zenoss Core, an open source monitoring platform.
 * Copyright (C) 2010, Zenoss Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 or (at your
 * option) any later version as published by the Free Software Foundation.
 * 
 * For complete information please visit: http://www.zenoss.com/oss/
 */
package org.zenoss.amqp;

import com.google.protobuf.Descriptors.Descriptor;

/**
 * Exception thrown by the {@link ProtobufConverter} if a protobuf message sends
 * a protobuf full name which is not supported by the decoder.
 */
public class UnregisteredProtobufException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String fullName;

    /**
     * Creates a new UnregisteredProtobufException with the full name of the
     * protobuf which failed decoding.
     * 
     * @param fullName
     *            The full name of the protobuf.
     */
    public UnregisteredProtobufException(String fullName) {
        super();
        this.fullName = fullName;
    }

    /**
     * Returns the full name of the protobuf which was sent to a queue by a
     * producer and couldn't be converted by a consumer.
     * 
     * @return The full name of the protobuf.
     * @see Descriptor#getFullName()
     */
    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append('[');
        sb.append("fullName=").append(this.fullName);
        sb.append(']');
        return sb.toString();
    }
}
