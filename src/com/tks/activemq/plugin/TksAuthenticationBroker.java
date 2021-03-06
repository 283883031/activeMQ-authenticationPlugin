package com.tks.activemq.plugin;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TksAuthenticationBroker extends AbstractAuthenticationBroker {
    private static final Logger log = LoggerFactory.getLogger(TksAuthenticationBroker.class);
    String secretUrl;
    List<String> allowedMac;
    List<String> allowedIp;

    Pattern pattern = Pattern.compile("^([0-9\\.]*):(.*)");

    public TksAuthenticationBroker(Broker next, String secretUrl, List<String> allowedMac, List<String> allowedIp) {
        super(next);
        this.secretUrl = secretUrl;
        this.allowedMac = allowedMac;
        this.allowedIp = allowedIp;
    }

    public String getSubAddress(String remoteAddress) {
        String subAddress = null;

        for (int i = 0; i < remoteAddress.length(); ++i) {
            char ch = remoteAddress.charAt(i);
            if (ch >= '0' && ch <= '9') {
                subAddress = remoteAddress.substring(i);
                break;
            }
        }

        return subAddress;
    }

    @Override
    public void addConnection(ConnectionContext context,
                              ConnectionInfo info) throws Exception {
        log.debug("secretUrl" + secretUrl);
        log.debug("allowedMac" + allowedMac);
        log.debug("allowedIp" + allowedIp);

        SecurityContext securityContext = context.getSecurityContext();

        if (securityContext == null) {
            log.debug("info=======" + info.toString());
            String remoteAddress = context.getConnection().getRemoteAddress();
            Matcher matcher = this.pattern.matcher(this.getSubAddress(remoteAddress));
            String ip = null;
            if (matcher.matches()) {
                ip = matcher.group(1);
            } else {
                log.debug("remoteAddress======" + remoteAddress);
                if (remoteAddress.contains("localhost") || remoteAddress.contains("LOCALHOST")) {
                    ip = "127.0.0.1";
                }
            }

            securityContext = authenticate(info.getUserName(), info.getPassword(), null, ip);
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);

        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
    }

    @Override
    public SecurityContext authenticate(String s, String s1, X509Certificate[] x509Certificates) throws SecurityException {
        return null;
    }

    /**
     * ??????
     * <p>Title: authenticate</p>
     */
    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates, String ip) throws SecurityException {
        SecurityContext securityContext = null;
        log.debug("remote ip:" + ip);

        if (null != allowedIp && allowedIp.size() > 0 && ip != null && allowedIp.stream().anyMatch(o -> o.equals(ip))) {
            log.debug("remote ip:" + ip + " has Verify through auth!");
            return new SecurityContext(username) {
                @Override
                public Set<Principal> getPrincipals() {
                    Set<Principal> groups = new HashSet<Principal>();
                    groups.add(new GroupPrincipal("admins"));
                    return groups;
                }
            };
        }

        if (username != null && !"".equals(username)) {
            if (null != allowedMac && allowedMac.size() > 0 && allowedMac.stream().anyMatch(o -> o.equals(username))) {
                log.debug("remote mac:" + username + " has Verify through auth!");
                securityContext = new SecurityContext(username) {
                    @Override
                    public Set<Principal> getPrincipals() {
                        Set<Principal> groups = new HashSet<Principal>();
                        groups.add(new GroupPrincipal("admins"));
                        return groups;
                    }
                };
            } else {
                String pw;
                //todo ?????????????????????????????????????????????????????????E7E80A21B346CFC4A7EC24D3C751AC8E
                try {
                    pw = syncTksActiveMqSecret(username);
                } catch (Exception e) {
                    throw new SecurityException("??????ActiveMQ??????????????????!");
                }
                log.debug("username:" + username + "-------------password:" + password + "-------------pw:" + pw);
                if (null != pw && pw.equals(password)) {
                    log.debug("remote username:" + username + " has Verify through auth!");
                    securityContext = new SecurityContext(username) {
                        @Override
                        public Set<Principal> getPrincipals() {
                            Set<Principal> groups = new HashSet<Principal>();
                            groups.add(new GroupPrincipal("admins"));
                            return groups;
                        }
                    };
                } else {
                    throw new SecurityException("????????????");
                }
            }
        } else {
            throw new SecurityException("????????????");
        }

        return securityContext;
    }

    private String syncTksActiveMqSecret(String username) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        String res = HttpUtils.doPost(secretUrl, params);
        log.debug("??????ActiveMQ ???????????????params=" + params + " res:" + res + " secretUrl:" + secretUrl);
        return res;
    }
}
