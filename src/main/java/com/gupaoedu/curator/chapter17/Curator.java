package com.gupaoedu.curator.chapter17;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author : lipu
 * @since : 2020-09-10 00:12
 */
public class Curator {

    private static String CONNECTION_STR="192.168.3.50:2181";

    public static void main(String[] args) {
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("");
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        curatorFramework.start();


        //CRUD todo
    }
}
