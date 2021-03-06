/*****************************************************************************
 * 
 * Copyright (C) Zenoss, Inc. 2010-2011, all rights reserved.
 * 
 * This content is made available according to terms specified in
 * License.zenoss under the directory where your Zenoss product is installed.
 * 
 ****************************************************************************/


package org.zenoss.amqp;

import java.util.concurrent.TimeUnit;

/**
 * A consumer created on a {@link Channel} which consumes messages from a queue
 * (optionally performing message decoding using a {@link MessageConverter}.
 * 
 * @param <T>
 *            The underlying datatype of the message body
 */
public interface Consumer<T> {
    /**
     * Consumes the next message from the queue.
     * 
     * @return The next message from the queue.
     * @throws AmqpException
     *             If an error occurs reading from the queue.
     * @throws InterruptedException If the thread was interrupted while consuming from the queue.
     */
    public Message<T> nextMessage() throws AmqpException, InterruptedException;

    /**
     * Waits up to the specified amount of time for a message to arrive on the
     * queue.
     * 
     * @param waitTime
     *            The amount of time to wait for a message to arrive on the
     *            queue.
     * @param unit
     *            Unit of time.
     * @return The next message from the queue, or null if no messages were
     *         available in the specified time.
     * @throws AmqpException
     *             If an error occurs reading from the queue.
     * @throws InterruptedException If the thread was interrupted while consuming from the queue.
     */
    public Message<T> nextMessage(long waitTime, TimeUnit unit)
            throws AmqpException, InterruptedException;

    /**
     * Cancels the consumer so the server sends no more messages to the client.
     * 
     * @throws AmqpException
     *             If an error occurs canceling the consumer.
     */
    public void cancel() throws AmqpException;

    /**
     * Returns the queue this consumer is consuming from.
     * 
     * @return The queue this consumer is consuming from.
     */
    public Queue getQueue();

    /**
     * Returns the channel this consumer is consuming from.
     *
     * @return The channel this consumer is consuming from.
     */
    public Channel getChannel();

    /**
     * Acknowledges a message.
     * 
     * @param message
     *            Message to acknowledge.
     * @throws AmqpException
     *             If an error occurs sending the acknowledgment.
     */
    public void ackMessage(Message<?> message) throws AmqpException;

    /**
     * Rejects the message.
     * 
     * @param message
     *            Message to reject.
     * @param requeue
     *            Whether the message should be re-queued.
     * @throws AmqpException
     *             If an error occurs rejecting the message.
     */
    public void rejectMessage(Message<?> message, boolean requeue)
            throws AmqpException;
}
