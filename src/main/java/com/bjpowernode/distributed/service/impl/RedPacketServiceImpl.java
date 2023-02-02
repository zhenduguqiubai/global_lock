package com.bjpowernode.distributed.service.impl;

import com.bjpowernode.distributed.mapper.RedPacketRecordMapper;
import com.bjpowernode.distributed.model.Constants;
import com.bjpowernode.distributed.model.RedPacketRecord;
import com.bjpowernode.distributed.model.RedPacketRecordExample;
import com.bjpowernode.distributed.service.RedPacketService;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedPacketServiceImpl implements RedPacketService {
    @Resource
    private RedPacketRecordMapper mapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    private final Object syncMonitor=new Object();


/*    @Override
    public int addRedPacket(RedPacketRecord record) {
        synchronized (syncMonitor){ //适合单机版本

            RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
            RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
            criteria.andUserIdEqualTo(record.getUserId());
            //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
            List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);
            if (null == dbredPacketRecords || dbredPacketRecords.size()==0){

                //插入红包数据
                return  mapper.insertSelective(record);
            }

            return 0;
        }

    }*/
/*全局锁 1.0版本
* 问题 :死锁
*
*
*
* */
/*    @Override
    public int addRedPacket(RedPacketRecord record) {
        //setIfAbsent: 底层是setnx
        //redis加锁,true 代表拿到锁
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(Constants.RED_PACKET_KEY + record.getUserId(), record.getRedId().toString());
    if (aBoolean){//拿到分布式锁
            RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
            RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
            criteria.andUserIdEqualTo(record.getUserId());
            //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
            List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);
            int add=0;
            if (null == dbredPacketRecords || dbredPacketRecords.size()==0){
                //插入红包数据
                add=  mapper.insertSelective(record);
            }
            //redis 解锁,删除锁
            stringRedisTemplate.delete(Constants.RED_PACKET_KEY + record.getUserId());

            return  add;
    }else { //未获取分布式锁
        return  -1;

    }
    }*/

    /*全局锁2.0 服务器宕机死锁*/
/*@Override
public int addRedPacket(RedPacketRecord record) {
    //setIfAbsent: 底层是setnx
    //redis加锁,true 代表拿到锁
    Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(Constants.RED_PACKET_KEY + record.getUserId(), record.getRedId().toString());
    if (aBoolean){//拿到分布式锁
        int add=0;
        try {
            RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
            RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
            criteria.andUserIdEqualTo(record.getUserId());
            //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
            List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);

            if (null == dbredPacketRecords || dbredPacketRecords.size()==0){
                //插入红包数据
                add=  mapper.insertSelective(record);
            }

        }finally {
            //业务异常情况下也能解锁
            stringRedisTemplate.delete(Constants.RED_PACKET_KEY + record.getUserId());
        }

        return  add;
    }else { //未获取分布式锁
        return  -1;

    }
}*/

/*分布式锁3.0*/
/*
    public int addRedPacket(RedPacketRecord record) {
        //setIfAbsent: 底层是setnx
        //redis加锁,true 代表拿到锁
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(Constants.RED_PACKET_KEY + record.getUserId(), record.getRedId().toString());
        //给锁设置过期时间   假设这里宕机了 还是会死锁
        stringRedisTemplate.expire(Constants.RED_PACKET_KEY + record.getUserId(),15, TimeUnit.SECONDS);
        if (aBoolean){//拿到分布式锁
            int add=0;
            try {
                RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
                RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
                criteria.andUserIdEqualTo(record.getUserId());
                //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
                List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);

                if (null == dbredPacketRecords || dbredPacketRecords.size()==0){
                    //插入红包数据
                    add=  mapper.insertSelective(record);
                }

            }finally {
                //业务异常情况下也能解锁
                stringRedisTemplate.delete(Constants.RED_PACKET_KEY + record.getUserId());
            }

            return  add;
        }else { //未获取分布式锁
            return  -1;

        }
    }
*/

/*分布式锁4.0*/
  /*  public int addRedPacket(RedPacketRecord record) {
        //setIfAbsent: 底层是setnx
        //redis加锁,true 代表拿到锁
        //加锁和设置过期时间 同时进行原子性操作  返回null 不知道为何 如果业务执行很慢,依旧会出现插入两条数据, 且有可能别人删你的key,你的key被别人删除
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(Constants.RED_PACKET_KEY + record.getUserId(), record.getRedId().toString(),15,TimeUnit.SECONDS);

        if (aBoolean){//拿到分布式锁
            int add=0;
            try {
                RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
                RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
                criteria.andUserIdEqualTo(record.getUserId());
                //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
                List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);

                if (null == dbredPacketRecords || dbredPacketRecords.size()==0){
                    //插入红包数据
                    add=  mapper.insertSelective(record);
                }

            }finally {
                //业务异常情况下也能解锁
                stringRedisTemplate.delete(Constants.RED_PACKET_KEY + record.getUserId());
            }

            return  add;
        }else { //未获取分布式锁
            return  -1;

        }
    }*/
    /*redisson实现*/
@Override
public int addRedPacket(RedPacketRecord record) {
    //获取到锁对象
    RLock lock = redissonClient.getLock(Constants.RED_PACKET_KEY + record.getUserId());
   /* RedissonRedLock redLock = new RedissonRedLock(lock);
    redLock.tryLock();*/
    //添加分布式锁
    try{
        //参数1 多少毫秒内没拿到就放弃  0代表拿一下
        //参数2  超时时间
        //参数3   时间
        boolean b = lock.tryLock(500, 30000, TimeUnit.MILLISECONDS);
        int add=0;
        if (b){
            RedPacketRecordExample redPacketRecordExample = new RedPacketRecordExample();
            RedPacketRecordExample.Criteria criteria = redPacketRecordExample.createCriteria();
            criteria.andUserIdEqualTo(record.getUserId());
            //查询一下,看看用户有没有领过红包 ,,高并发情况下两个线程同时查询发现都不存在,那么就会同时插入数据
            List<RedPacketRecord> dbredPacketRecords = mapper.selectByExample(redPacketRecordExample);

            if (null == dbredPacketRecords || dbredPacketRecords.size()==0){
                //插入红包数据
                add=  mapper.insertSelective(record);
            }
        }
    } catch (InterruptedException e) {


    } finally {
        //释放锁  当前线程的锁, 锁状态
        if (lock.isHeldByCurrentThread()&& lock.isLocked()){
            lock.unlock();
        }
    }
    return 0;
}
}
