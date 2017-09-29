使用commons-thunisoft-ftppool需要spring文件引入以下依赖：

```
<bean id="ftpClientManager" class="com.thunisoft.ftppool.FtpClientPoolManager" init-method="init">
    <property name="config" ref="ftpClientPoolConfig"></property>
    <property name="factory" ref="ftpClientFactory"></property>
</bean>

<bean id="ftpClientFactory" class="com.thunisoft.ftppool.FtpClientFactory"></bean>

<bean id="ftpClientPoolConfig" class="com.thunisoft.ftppool.pool.FtpClientPoolConfig">
    <property name="maxTotal" value="${ftp.pool.maxTotal}" />
    <property name="maxTotalPerKey" value="${ftp.pool.maxTotalPerKey}" />
    <property name="maxIdlePerKey" value="${ftp.pool.maxIdlePerKey}" />
    <property name="minIdlePerKey" value="${ftp.pool.minIdlePerKey}" />
    <property name="maxWaitMillis" value="${ftp.pool.maxWaitMillis}" />
    <property name="timeBetweenEvictionRunsMillis" value="${ftp.pool.timeBetweenEvictionRunsMillis}" />
</bean>
```

配置说明：

#FTP连接池最大连接数，默认不限制
ftp.pool.maxTotal=

#每个key对应的FTP连接池最大连接数，默认不限制
ftp.pool.maxTotalPerKey=

#每个key对应的FTP连接池最大空闲连接数，默认50
ftp.pool.maxIdlePerKey=

#每个key对应的FTP连接池最小空闲连接数，默认20
ftp.pool.minIdlePerKey=

#获取连接最大等待时间，单位毫秒，默认不限制
ftp.pool.maxWaitMillis=

#每隔多长时间运行一次空闲连接回收器，单位毫秒，默认：10000毫秒
ftp.pool.timeBetweenEvictionRunsMillis=
