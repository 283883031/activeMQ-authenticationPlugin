package com.tks.activemq.plugin;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TksAuthenticationPlugin implements BrokerPlugin {

    private static final Logger log = LoggerFactory.getLogger(TksAuthenticationPlugin.class);

    private String secretUrl;
    private List<String> allowedMac;
    private List<String> allowedIp;


    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        return new TksAuthenticationBroker(broker, secretUrl, allowedMac, allowedIp);
    }

    public String getSecretUrl() {
        return secretUrl;
    }

    public void setSecretUrl(String secretUrl) {
        this.secretUrl = secretUrl;
    }

    public List<String> getAllowedMac() {
        return allowedMac;
    }

    public void setAllowedMac(List<String> allowedMac) {
        this.allowedMac = allowedMac;
    }

    public List<String> getAllowedIp() {
        return allowedIp;
    }

    public void setAllowedIp(List<String> allowedIp) {
        this.allowedIp = allowedIp;
    }
}
