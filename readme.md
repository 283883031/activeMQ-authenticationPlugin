前置机ActiveMQ插件，用于连接验证


在外部activeMQ服务器中加入验证插件:
插件放入地址为`activeMQ/lib`

activeMQ.xml 集群的配置文件

```xml
<plugins>
                    <bean xmlns="http://www.springframework.org/schema/beans" id="tksAuthPlugin" class="com.tks.activemq.plugin.TksAuthenticationPlugin">
                        <property name="secretUrl">
                            <value>http://127.0.0.1:8082/activeMQ/checkSecret</value>
                        </property>
                        <property name="allowedMac">
                          <list>
                            <value>74-27-EA-19-FD-32</value>
                          </list>
                        </property>
                        <property name="allowedIp">
                          <list>
                            <value>127.0.0.1</value>
                             <value>localhost</value>
                              <value>192.168.2.216</value>
                          </list>
                        </property>
                    </bean>
             </plugins>
```
代码块中这段为插件配置。
`secretUrl `密钥验证服务器地址。这个需要自己开发，详情见AbstractAuthenticationBroker，就是发送用户名，获取密码，然后进行验证。
`allowedMac` 无需验证的客户端MAC地址
`allowedIp`无需验证的客户端IP地址

3.查看activeMQ服务器的data文件夹下的日志文件
```java
2018-01-03 14:34:47,167 | INFO  | info=======ConnectionInfo {commandId = 1, responseRequired = true, connectionId = ID:zhujing-2.local-53190-1514961286996-1:1, clientId = ID:zhujing-2.local-53190-1514961286996-0:1, clientIp = tcp://127.0.0.1:53191, userName = A2-CE-C8-80-52-64, password = *****, brokerPath = null, brokerMasterConnector = false, manageable = true, clientMaster = true, faultTolerant = true, failoverReconnect = false} | org.apache.activemq.advisory.AdvisoryBroker | ActiveMQ Transport: tcp:///127.0.0.1:53191@61616
2018-01-03 14:34:47,170 | INFO  | username:A2-CE-C8-80-52-64 | com.tks.activemq.plugin.HttpUtils | ActiveMQ Transport: tcp:///127.0.0.1:53191@61616
2018-01-03 14:34:47,176 | INFO  | 获取ActiveMQ 密钥信息：params={username=A2-CE-C8-80-52-64} res:bd1e500f-6b81-47c5-a066-7c324e31affa | org.apache.activemq.advisory.AdvisoryBroker | ActiveMQ Transport: tcp:///127.0.0.1:53191@61616
2018-01-03 14:34:47,178 | INFO  | username:A2-CE-C8-80-52-64-------------password:bd1e500f-6b81-47c5-a066-7c324e31affa-------------pw:bd1e500f-6b81-47c5-a066-7c324e31affa | org.apache.activemq.advisory.AdvisoryBroker | ActiveMQ Transport: tcp:///127.0.0.1:53191@61616
