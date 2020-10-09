package com.gupaoedu.curator.chapter17;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**监听机制
 * @author : lipu
 * @since : 2020-09-18 21:25
 */
public class WatcherDemo {

    //    private static String CONNECTION_STR="192.168.3.50:2181";
    private static String CONNECTION_STR="114.55.95.30:2181";


    public static void main(String[] args) throws Exception {
        //PathChildCache  --针对子节点的创建、删除、更新  触发事件
        //NodeCache  --针对当前节点的变化触发事件
        //TreeCache  --综合事件

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_STR)//连接字符串
                .sessionTimeoutMs(5000)  //session超时时间，毫秒
//                .authorization("digest","admin:admin".getBytes())//给本次会话添加授权限
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        curatorFramework.start();//启动

//        addListenerWithNode(curatorFramework);
        addListenerWithChild(curatorFramework);


        //阻塞，防止main函数关闭，就监听不到了
        System.in.read();

    }


    /**定义一个自定义的监听，绑定到指定zookeeper的指定节点的事件上;
     * ###########这里是一直监听的
     * @author lipu
     * @since 2020/9/18 21:46
     */
    private static void addListenerWithNode(CuratorFramework curatorFramework) throws Exception{
        //定义一个事件是绑定的哪个zookeeper的哪个节点
        NodeCache nodeCache = new NodeCache(curatorFramework,"/watch",false);
        //定义一个监听（及它内部实现的逻辑）
        NodeCacheListener nodeCacheListener = ()->{
            System.out.println("receive Node Changed");
            System.out.println(nodeCache.getCurrentData().getPath()+";data="+new String(nodeCache.getCurrentData().getData()));
        };
        //把定义好的监听绑定到定义好的事件中
        nodeCache.getListenable().addListener(nodeCacheListener);

        //启动事件监听
        nodeCache.start();
    }

    /**定义一个自定义的监听，绑定到指定zookeeper的指定节点的《子节点》的事件上;
     * 监听子节点的变化(创建和删除子节点不监听)
     *
     * 注册中心：对服务做动态感知
     * @author lipu
     * @since 2020/9/18 21:54
     */
    private static void addListenerWithChild(CuratorFramework curatorFramework) throws Exception{
        //定义一个事件是绑定的哪个zookeeper的哪个节点
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework,"/watch",false);
        //定义一个监听（及它内部实现的逻辑）
        PathChildrenCacheListener nodeCacheListener = (curatorFramework1,pathChildrenCacheEvent)->{
            System.out.println("receive ChildNode Changed");
            System.out.println(pathChildrenCacheEvent.getType());
            System.out.println(new String(pathChildrenCacheEvent.getData().getPath()));
            ChildData data = pathChildrenCacheEvent.getData();
            String path = pathChildrenCacheEvent.getData().getPath();
            byte[] data1 = pathChildrenCacheEvent.getData().getData();
            System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
            System.out.println(pathChildrenCacheEvent.getType()+"->"+new String(pathChildrenCacheEvent.getData().getData()));
            System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
            System.out.println("=======");
        };
        //把定义好的监听绑定到定义好的事件中
        nodeCache.getListenable().addListener(nodeCacheListener);

        //启动事件监听
        nodeCache.start(PathChildrenCache.StartMode.NORMAL);
    }



}
