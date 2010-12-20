/*
 * This program is part of Zenoss Core, an open source monitoring platform.
 * Copyright (C) 2010, Zenoss Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 * 
 * For complete information please visit: http://www.zenoss.com/oss/
 */
package org.zenoss.protobufs.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.zenoss.protobufs.JsonFormat;
import org.zenoss.protobufs.ProtobufConstants;

import com.google.protobuf.Message;

/**
 * REST provider used to serialize and deserialize protobuf objects.
 */
@Provider
@Produces({ ProtobufConstants.CONTENT_TYPE_PROTOBUF, MediaType.APPLICATION_JSON })
@Consumes({ ProtobufConstants.CONTENT_TYPE_PROTOBUF, MediaType.APPLICATION_JSON })
public class ProtobufProvider implements MessageBodyWriter<Message>,
        MessageBodyReader<Message> {

    private ProtobufMessageRegistry messageRegistry;

    /**
     * Specifies the message registry to be used for decoding messages. Usually
     * injected automatically via DI framework.
     * 
     * @param messageRegistry
     *            Message registry to use to decode messages.
     */
    public void setMessageRegistry(ProtobufMessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Message message, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        final int size;
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            size = -1;
        } else {
            size = message.getSerializedSize();
        }
        return size;
    }

    @Override
    public void writeTo(Message message, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        String fullName = message.getDescriptorForType().getFullName();
        httpHeaders.add(ProtobufConstants.HEADER_PROTOBUF_FULLNAME, fullName);
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            JsonFormat.writeTo(message, entityStream);
        } else {
            message.writeTo(entityStream);
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public Message readFrom(Class<Message> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        final Message msg;
        final String fullName = httpHeaders
                .getFirst(ProtobufConstants.HEADER_PROTOBUF_FULLNAME);
        if (fullName == null) {
            throw new IOException("Missing required HTTP header: "
                    + ProtobufConstants.HEADER_PROTOBUF_FULLNAME);
        }
        final Message defaultMsg = messageRegistry
                .getMessageByFullName(fullName);
        if (defaultMsg == null) {
            throw new IOException(
                    "Protobuf message full name not supported by registry: "
                            + fullName);
        }
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            msg = JsonFormat.merge(entityStream,
                    defaultMsg.newBuilderForType(),
                    messageRegistry.getExtensionRegistry());
        } else {
            msg = defaultMsg
                    .newBuilderForType()
                    .mergeFrom(entityStream,
                            messageRegistry.getExtensionRegistry()).build();
        }
        return msg;
    }

}
