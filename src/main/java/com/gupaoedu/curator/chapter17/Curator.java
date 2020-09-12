package com.gupaoedu.curator.chapter17;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**Curator连接zookeeper的一种组件
 * @author : lipu
 * @since : 2020-09-10 00:12
 */
public class Curator {

    private static String CONNECTION_STR="192.168.3.540:2181";

    public static void main(String[] args) throws Exception {
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("");
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR)//连接字符串
                .sessionTimeoutMs(5000)  //session超时时间，毫秒
                //ExponentialBackoffRetry，衰减重试策略，每睡1000毫秒重试一次，最多3次，这3次的睡眠时间是递增的
                //RetryOneTime，只重试一次
                //RetryUtilElapsed，一直重试，知道规定的时间结束
                //RetryNTimes，指定最大的重试次数
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();

        curatorFramework.start();//启动
        createData(curatorFramework);

        //CRUD
//        curatorFramework.create();//创建
//        curatorFramework.setACL();//修改
//        curatorFramework.delete();//删除
//        curatorFramework.getData();//查询
    }

    private static void createData(CuratorFramework curatorFramework) throws Exception {
        //creatingParentsIfNeeded:是否要创建父节点，默认：true
        //withMode:设置节点的类型PERSISTENT(默认)，PERSISTENT_SEQUENTIAL，EPHEMERAL。。。。
        //withACL:权限设置
        curatorFramework.create().creatingParentsIfNeeded().forPath("/data/program","test".getBytes());

    }

}
