package redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *  2 . 测试
 *
 *	下面就用一个简单的例子测试刚才实现的分布式锁。 
 *   
 *  例子中使用50个线程模拟秒杀一个商品，使用--运算符来实现商品减少，从结果有序性就可以看出是否为加锁状态。
 *	
 *	模拟秒杀服务，在其中配置了jedis线程池，在初始化的时候传给分布式锁，供其使用。
 *
 * Created by liuyang on 2017/4/20.
 */
public class Service {
	
    private static JedisPool pool = null;

    static {
    	
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "172.16.1.177", 6379, 3000);
    }

    DistributedLock lock = new DistributedLock(pool);

    int n = 500;
    
    public void seckill() {
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = lock.lockWithTimeout("resource", 5000, 1000);
        
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        
        System.out.println(--n);
        
        lock.releaseLock("resource", indentifier);
    }
}
