/*
 * Copyright 2023-2024 the leader
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.org.byc.disnumber.center.server.domain.rule.impl;

import cn.org.byc.disnumber.center.server.domain.rule.Rule;
import cn.org.byc.disnumber.center.model.entity.RuleInstanceHistory;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryUntilElapsed;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SerialRule implements Rule {

    /**
     * 长度
     */
    private Integer len;

    /**
     * 最小值
     */
    private Integer min;

    /**
     * 最大值
     */
    private Integer max;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 定值填充
     */
    private char padding;

    private String instanceCode;

    private RabbitTemplate rabbitTemplate;

    private Environment env;

    private RedissonClient redissonClient;

    private CuratorFramework zkClient;

    /**
     * 前缀节点
     */
    private String prex;

    public SerialRule(Integer len, Integer min, Integer max, Integer step, char padding, CuratorFramework zkClient) {
        this.len = len;
        this.min = min;
        this.max = max;
        this.step = step;
        this.padding = padding;
        this.zkClient = zkClient;
    }

    public SerialRule(Integer len, Integer min, Integer max, Integer step, char padding, String instanceCode, RabbitTemplate rabbitTemplate, Environment env, CuratorFramework zkClient) {
        this.len = len;
        this.min = min;
        this.max = max;
        this.step = step;
        this.padding = padding;
        this.instanceCode = instanceCode;
        this.rabbitTemplate = rabbitTemplate;
        this.env = env;
        this.zkClient = zkClient;
    }

    public String getPrex() {
        return prex;
    }

    public void setPrex(String prex) {
        this.prex = prex;
    }


    @Override
    public String process(Map<String, Object> params) throws Exception {
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(zkClient, prex
                , new RetryUntilElapsed(10000, 1000));
        InterProcessMutex processMutex = new InterProcessMutex(zkClient, prex + "-lock");
        try {
            if (processMutex.acquire(10, TimeUnit.SECONDS)) {
                AtomicValue<Integer> atomicValue = atomicInteger.get();
                String result = null;
                if (atomicValue.succeeded()) {
                    Integer val = atomicValue.postValue();
                    if (val == null) {
                        atomicInteger.initialize(min);
                        result = Strings.padStart(min + "", len, padding);
                        return result;
                    } else {
                        if (val + step >= max) {
                            throw new IllegalArgumentException(String.format("%s,(%s)超出最大值(%s)", prex, val, max));
                        } else {
                            AtomicValue<Integer> add = atomicInteger.trySet(val + step);
                            if (add.succeeded()) {
                                Integer postValue = add.postValue();
                                result = Strings.padStart(postValue + "", len, padding);
                                //创建历史记录
                                RuleInstanceHistory history = new RuleInstanceHistory();
                                history.setInstanceCode(instanceCode);
                                history.setCode(prex);
                                history.setVal(postValue);
                                rabbitTemplate.setExchange(env.getProperty("rule.history.create.exchange.name"));
                                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                                rabbitTemplate.convertAndSend(history);
                                return result;
                            } else {
                                throw new IllegalArgumentException(String.format("%s,自增失败", prex));
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException(String.format("%s,获取失败", prex));
                }
            } else {
                throw new IllegalArgumentException("获取锁失败");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            processMutex.release();
        }
    }
}
