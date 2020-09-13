package com.gupaoedu.curator.chapter17;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;

/**权限
 * @author : lipu
 * @since : 2020-09-12 00:12
 */
public class AclDemo {

    private static String CONNECTION_STR="192.168.3.50:2181";

    public static void main(String[] args) throws Exception {
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("");

        //授权



        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR)//连接字符串
                .sessionTimeoutMs(5000)  //session超时时间，毫秒
                .authorization("digest","admin:admin".getBytes())//给本次会话添加授权限
                //ExponentialBackoffRetry，衰减重试策略，每睡1000毫秒重试一次，最多3次，这3次的睡眠时间是递增的
                //RetryOneTime，只重试一次
                //RetryUtilElapsed，一直重试，知道规定的时间结束
                //RetryNTimes，指定最大的重试次数
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        curatorFramework.start();//启动
        /*
        ##########1.操作权限######################
        int READ = 1 << 0;读权限
        int WRITE = 1 << 1;写权限
        int CREATE = 1 << 2;创建权限
        int DELETE = 1 << 3;删除权限
        int ADMIN = 1 << 4;读权限
        int ALL = READ | WRITE | CREATE | DELETE | ADMIN;
        ##########2.操作权限######################
        new Id()：身份验证的信息
            scheme：权限模式
                IP：基于IP的访问
                Digest：基于用户名密码
                world：对所用用户开放
                super：对超级用户开放
          DigestAuthenticationProvider.generateDiges:对数据加密

        */
        List<ACL> list = new ArrayList<>();
        //只有读权限，写的话：Authentication is not valid : /auth
        //不管什么权限，删除都可以操作'；认证  addauth digest admin:admin
        ACL acl = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE,new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin")));
        list.add(acl);
        //给已经存在没有权限控制的节点加上权限控制
        curatorFramework.setACL().withACL(list).forPath("/temp");

        //给新创建的节点加上权限控制
//        curatorFramework.create().withMode(CreateMode.PERSISTENT).withACL(list).forPath("/auth");

        //2.Ip################################
        //只有这个ip能访问
        ACL acl1 = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE,new Id("ip", "192.168.1.1"));
        //对所用用户开放
        ACL acl2 = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE,new Id("world", "anyone"));

        //3.内置封装的权限控制
        curatorFramework.setACL().withACL(ZooDefs.Ids.CREATOR_ALL_ACL).forPath("/temp");
        list.add(acl1);


//        createData(curatorFramework);

    }

    /**
     * 创建节点
     */
    private static void createData(CuratorFramework curatorFramework) throws Exception {
        //creatingParentsIfNeeded:是否要创建父节点，默认：true
        //withMode:设置节点的类型PERSISTENT(默认)，PERSISTENT_SEQUENTIAL，EPHEMERAL。。。。
        //withACL:权限设置
        curatorFramework.create().creatingParentsIfNeeded().forPath("/data/program","test".getBytes());

    }


}
