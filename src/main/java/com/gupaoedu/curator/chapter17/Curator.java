package com.gupaoedu.curator.chapter17;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**Curator连接zookeeper的一种组件
 * 还有一个zkclient，一般用curator
 * @author : lipu
 * @since : 2020-09-10 00:12
 */
public class Curator {

    //    private static String CONNECTION_STR="192.168.3.50:2181";
    private static String CONNECTION_STR="114.55.95.30:2181";

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
//        createData(curatorFramework);
//        updateData(curatorFramework);
        deleteData(curatorFramework);

        //CRUD
//        curatorFramework.create();//创建
//        curatorFramework.setACL();//修改
//        curatorFramework.delete();//删除
//        curatorFramework.getData();//查询
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

    /**
     * 修改节点
     */
    private static void updateData(CuratorFramework curatorFramework) throws Exception {
        curatorFramework.setData().forPath("/data/program","update".getBytes());

    }

    /**
     * 删除节点
     */
    private static void deleteData(CuratorFramework curatorFramework) throws Exception {
        //deletingChildrenIfNeeded:是否要递归删除，默认：true
        //withVersion:传版本，不对会失败；否则：BadVersion for /data/program;如果其他更改就会失败，就是锁
        //withACL:权限设置
        Stat stat = new Stat();
        byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/data/program");
        String value = new String(bytes);
        curatorFramework.delete().withVersion(stat.getVersion()).forPath("/data/program");

    }

}
