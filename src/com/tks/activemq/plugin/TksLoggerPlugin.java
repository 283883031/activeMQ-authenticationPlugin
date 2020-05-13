package com.tks.activemq.plugin;

import org.apache.activemq.broker.*;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.*;
import org.apache.activemq.usage.Usage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import java.util.Iterator;
import java.util.Set;

/**
 * Titel:一卡通云平台tks
 * Description:
 * Author: zhujing
 * Date: 2018/5/30
 * Time: 09:52
 * QQ:283883031
 */
public class TksLoggerPlugin extends BrokerPluginSupport {

    private static final Logger LOG = LoggerFactory.getLogger(TksLoggerPlugin.class);
    private boolean logAll = false;
    private boolean logConnectionEvents = true;
    private boolean logSessionEvents = true;
    private boolean logTransactionEvents = false;
    private boolean logConsumerEvents = false;
    private boolean logProducerEvents = false;
    private boolean logInternalEvents = false;
    private boolean perDestinationLogger = false;

    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public void afterPropertiesSet() throws Exception {
        LOG.info("Created LoggingBrokerPlugin: {}", this.toString());
    }

    public boolean isLogAll() {
        return this.logAll;
    }

    public void setLogAll(boolean logAll) {
        this.logAll = logAll;
    }

    public boolean isLogConnectionEvents() {
        return this.logConnectionEvents;
    }

    public void setLogConnectionEvents(boolean logConnectionEvents) {
        this.logConnectionEvents = logConnectionEvents;
    }

    public boolean isLogSessionEvents() {
        return this.logSessionEvents;
    }

    public void setLogSessionEvents(boolean logSessionEvents) {
        this.logSessionEvents = logSessionEvents;
    }

    public boolean isLogTransactionEvents() {
        return this.logTransactionEvents;
    }

    public void setLogTransactionEvents(boolean logTransactionEvents) {
        this.logTransactionEvents = logTransactionEvents;
    }

    public boolean isLogConsumerEvents() {
        return this.logConsumerEvents;
    }

    public void setLogConsumerEvents(boolean logConsumerEvents) {
        this.logConsumerEvents = logConsumerEvents;
    }

    public boolean isLogProducerEvents() {
        return this.logProducerEvents;
    }

    public void setLogProducerEvents(boolean logProducerEvents) {
        this.logProducerEvents = logProducerEvents;
    }

    public boolean isLogInternalEvents() {
        return this.logInternalEvents;
    }

    public void setLogInternalEvents(boolean logInternalEvents) {
        this.logInternalEvents = logInternalEvents;
    }

    public void acknowledge(ConsumerBrokerExchange consumerExchange, MessageAck ack) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LOG.info("Acknowledging message for client ID: {}{}", consumerExchange.getConnectionContext().getClientId(), ack.getMessageCount() == 1 ? ", " + ack.getLastMessageId() : "");
            if (ack.getMessageCount() > 1) {
                LOG.trace("Message count: {}, First Message Id: {}, Last Message Id: {}", new Object[]{ack.getMessageCount(), ack.getFirstMessageId(), ack.getLastMessageId()});
            }
        }

        super.acknowledge(consumerExchange, ack);
    }

    public Response messagePull(ConnectionContext context, MessagePull pull) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LOG.info("Message Pull from: {} on {}", context.getClientId(), pull.getDestination().getPhysicalName());
        }

        return super.messagePull(context, pull);
    }

    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConnectionEvents()) {
            LOG.info("Adding Connection: {}", info);
        }

        super.addConnection(context, info);
    }

    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LOG.info("Adding Consumer: {}", info);
        }

        return super.addConsumer(context, info);
    }

    public void addProducer(ConnectionContext context, ProducerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            LOG.info("Adding Producer: {}", info);
        }

        super.addProducer(context, info);
    }

    public void commitTransaction(ConnectionContext context, TransactionId xid, boolean onePhase) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LOG.info("Committing transaction: {}", xid.getTransactionKey());
        }

        super.commitTransaction(context, xid, onePhase);
    }

    public void removeSubscription(ConnectionContext context, RemoveSubscriptionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LOG.info("Removing subscription: {}", info);
        }

        super.removeSubscription(context, info);
    }

    public TransactionId[] getPreparedTransactions(ConnectionContext context) throws Exception {
        TransactionId[] result = super.getPreparedTransactions(context);
        if ((this.isLogAll() || this.isLogTransactionEvents()) && result != null) {
            StringBuffer tids = new StringBuffer();
            TransactionId[] var4 = result;
            int var5 = result.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                TransactionId tid = var4[var6];
                if (tids.length() > 0) {
                    tids.append(", ");
                }

                tids.append(tid.getTransactionKey());
            }

            LOG.info("Prepared transactions: {}", tids);
        }

        return result;
    }

    public int prepareTransaction(ConnectionContext context, TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LOG.info("Preparing transaction: {}", xid.getTransactionKey());
        }

        return super.prepareTransaction(context, xid);
    }

    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error) throws Exception {
        if (this.isLogAll() || this.isLogConnectionEvents()) {
            LOG.info("Removing Connection: {}", info);
        }

        super.removeConnection(context, info, error);
    }

    public void removeConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LOG.info("Removing Consumer: {}", info);
        }

        super.removeConsumer(context, info);
    }

    public void removeProducer(ConnectionContext context, ProducerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            LOG.info("Removing Producer: {}", info);
        }

        super.removeProducer(context, info);
    }

    public void rollbackTransaction(ConnectionContext context, TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LOG.info("Rolling back Transaction: {}", xid.getTransactionKey());
        }

        super.rollbackTransaction(context, xid);
    }

    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            this.logSend(messageSend.copy());
        }

        super.send(producerExchange, messageSend);
    }

    private void logSend(Message copy) {
        copy.getSize();
        Logger perDestinationsLogger = LOG;
        if (this.isPerDestinationLogger()) {
            ActiveMQDestination destination = copy.getDestination();
            perDestinationsLogger = LoggerFactory.getLogger(LOG.getName() + "." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName());
        }
        if (copy.getMessage() instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage) copy.getMessage();
            try {
                perDestinationsLogger.info("Sending message: {}  textInfo:{}", copy, textMessage.getText());
            } catch (JMSException e) {
            }
        } else {
            perDestinationsLogger.info("Sending message: {}", copy);
        }

    }

    public void beginTransaction(ConnectionContext context, TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LOG.info("Beginning transaction: {}", xid.getTransactionKey());
        }

        super.beginTransaction(context, xid);
    }

    public void forgetTransaction(ConnectionContext context, TransactionId transactionId) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LOG.info("Forgetting transaction: {}", transactionId.getTransactionKey());
        }

        super.forgetTransaction(context, transactionId);
    }

    public Connection[] getClients() throws Exception {
        Connection[] result = super.getClients();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LOG.info("Get Clients returned empty list.");
            } else {
                StringBuffer cids = new StringBuffer();
                Connection[] var3 = result;
                int var4 = result.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    Connection c = var3[var5];
                    cids.append(cids.length() > 0 ? ", " : "");
                    cids.append(c.getConnectionId());
                }

                LOG.info("Connected clients: {}", cids);
            }
        }

        return super.getClients();
    }

    public Destination addDestination(ConnectionContext context, ActiveMQDestination destination, boolean create) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Adding destination: {}:{}", destination.getDestinationTypeAsString(), destination.getPhysicalName());
        }

        return super.addDestination(context, destination, create);
    }

    public void removeDestination(ConnectionContext context, ActiveMQDestination destination, long timeout) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Removing destination: {}:{}", destination.getDestinationTypeAsString(), destination.getPhysicalName());
        }

        super.removeDestination(context, destination, timeout);
    }

    public ActiveMQDestination[] getDestinations() throws Exception {
        ActiveMQDestination[] result = super.getDestinations();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LOG.info("Get Destinations returned empty list.");
            } else {
                StringBuffer destinations = new StringBuffer();
                ActiveMQDestination[] var3 = result;
                int var4 = result.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    ActiveMQDestination dest = var3[var5];
                    destinations.append(destinations.length() > 0 ? ", " : "");
                    destinations.append(dest.getPhysicalName());
                }

                LOG.info("Get Destinations: {}", destinations);
            }
        }

        return result;
    }

    public void start() throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Starting {}", this.getBrokerName());
        }

        super.start();
    }

    public void stop() throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Stopping {}", this.getBrokerName());
        }

        super.stop();
    }

    public void addSession(ConnectionContext context, SessionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogSessionEvents()) {
            LOG.info("Adding Session: {}", info);
        }

        super.addSession(context, info);
    }

    public void removeSession(ConnectionContext context, SessionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogSessionEvents()) {
            LOG.info("Removing Session: {}", info);
        }

        super.removeSession(context, info);
    }

    public void addBroker(Connection connection, BrokerInfo info) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Adding Broker {}", info.getBrokerName());
        }

        super.addBroker(connection, info);
    }

    public void removeBroker(Connection connection, BrokerInfo info) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Removing Broker {}", info.getBrokerName());
        }

        super.removeBroker(connection, info);
    }

    public BrokerInfo[] getPeerBrokerInfos() {
        BrokerInfo[] result = super.getPeerBrokerInfos();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LOG.info("Get Peer Broker Infos returned empty list.");
            } else {
                StringBuffer peers = new StringBuffer();
                BrokerInfo[] var3 = result;
                int var4 = result.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    BrokerInfo bi = var3[var5];
                    peers.append(peers.length() > 0 ? ", " : "");
                    peers.append(bi.getBrokerName());
                }

                LOG.info("Get Peer Broker Infos: {}", peers);
            }
        }

        return result;
    }

    public void preProcessDispatch(MessageDispatch messageDispatch) {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LOG.info("preProcessDispatch: {}", messageDispatch);
        }

        super.preProcessDispatch(messageDispatch);
    }

    public void postProcessDispatch(MessageDispatch messageDispatch) {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LOG.info("postProcessDispatch: {}", messageDispatch);
        }

        super.postProcessDispatch(messageDispatch);
    }

    public void processDispatchNotification(MessageDispatchNotification messageDispatchNotification) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LOG.info("ProcessDispatchNotification: {}", messageDispatchNotification);
        }

        super.processDispatchNotification(messageDispatchNotification);
    }

    public Set<ActiveMQDestination> getDurableDestinations() {
        Set<ActiveMQDestination> result = super.getDurableDestinations();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LOG.info("Get Durable Destinations returned empty list.");
            } else {
                StringBuffer destinations = new StringBuffer();
                Iterator var3 = result.iterator();

                while (var3.hasNext()) {
                    ActiveMQDestination dest = (ActiveMQDestination) var3.next();
                    destinations.append(destinations.length() > 0 ? ", " : "");
                    destinations.append(dest.getPhysicalName());
                }

                LOG.info("Get Durable Destinations: {}", destinations);
            }
        }

        return result;
    }

    public void addDestinationInfo(ConnectionContext context, DestinationInfo info) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Adding destination info: {}", info);
        }

        super.addDestinationInfo(context, info);
    }

    public void removeDestinationInfo(ConnectionContext context, DestinationInfo info) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Removing destination info: {}", info);
        }

        super.removeDestinationInfo(context, info);
    }

    public void messageExpired(ConnectionContext context, MessageReference message, Subscription subscription) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = message.getMessage().toString();
            LOG.info("Message has expired: {}", msg);
        }

        super.messageExpired(context, message, subscription);
    }

    public boolean sendToDeadLetterQueue(ConnectionContext context, MessageReference messageReference, Subscription subscription, Throwable poisonCause) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LOG.info("Sending to DLQ: {}", msg);
        }

        return super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
    }

    public void fastProducer(ConnectionContext context, ProducerInfo producerInfo, ActiveMQDestination destination) {
        if (this.isLogAll() || this.isLogProducerEvents() || this.isLogInternalEvents()) {
            LOG.info("Fast Producer: {}", producerInfo);
        }

        super.fastProducer(context, producerInfo, destination);
    }

    public void isFull(ConnectionContext context, Destination destination, Usage<?> usage) {
        if (this.isLogAll() || this.isLogProducerEvents() || this.isLogInternalEvents()) {
            LOG.info("Destination is full: {}", destination.getName());
        }

        super.isFull(context, destination, usage);
    }

    public void messageConsumed(ConnectionContext context, MessageReference messageReference) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LOG.info("Message consumed: {}", msg);
        }

        super.messageConsumed(context, messageReference);
    }

    public void messageDelivered(ConnectionContext context, MessageReference messageReference) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LOG.info("Message delivered: {}", msg);
        }

        super.messageDelivered(context, messageReference);
    }

    public void messageDiscarded(ConnectionContext context, Subscription sub, MessageReference messageReference) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LOG.info("Message discarded: {}", msg);
        }

        super.messageDiscarded(context, sub, messageReference);
    }

    public void slowConsumer(ConnectionContext context, Destination destination, Subscription subs) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            LOG.info("Detected slow consumer on {}", destination.getName());
            StringBuffer buf = new StringBuffer("Connection(");
            buf.append(subs.getConsumerInfo().getConsumerId().getConnectionId());
            buf.append(") Session(");
            buf.append(subs.getConsumerInfo().getConsumerId().getSessionId());
            buf.append(")");
            LOG.info(buf.toString());
        }

        super.slowConsumer(context, destination, subs);
    }

    public void nowMasterBroker() {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LOG.info("Is now the master broker: {}", this.getBrokerName());
        }

        super.nowMasterBroker();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("LoggingBrokerPlugin(");
        buf.append("logAll=");
        buf.append(this.isLogAll());
        buf.append(", logConnectionEvents=");
        buf.append(this.isLogConnectionEvents());
        buf.append(", logSessionEvents=");
        buf.append(this.isLogSessionEvents());
        buf.append(", logConsumerEvents=");
        buf.append(this.isLogConsumerEvents());
        buf.append(", logProducerEvents=");
        buf.append(this.isLogProducerEvents());
        buf.append(", logTransactionEvents=");
        buf.append(this.isLogTransactionEvents());
        buf.append(", logInternalEvents=");
        buf.append(this.isLogInternalEvents());
        buf.append(")");
        return buf.toString();
    }

    public void setPerDestinationLogger(boolean perDestinationLogger) {
        this.perDestinationLogger = perDestinationLogger;
    }

    public boolean isPerDestinationLogger() {
        return this.perDestinationLogger;
    }


}
