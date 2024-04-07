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

package cn.org.byc.disnumber.center.server.service.impl;

import cn.org.byc.disnumber.center.api.dto.GenerateDto;
import cn.org.byc.disnumber.center.api.exception.InstanceKeyExistsException;
import cn.org.byc.disnumber.center.api.exception.InvalidInstanceKeyException;
import cn.org.byc.disnumber.center.api.exception.InvalidRuleIdException;
import cn.org.byc.disnumber.center.model.entity.Rule;
import cn.org.byc.disnumber.center.model.entity.RuleInstance;
import cn.org.byc.disnumber.center.model.entity.RuleInstanceHistory;
import cn.org.byc.disnumber.center.model.enums.RuleType;
import cn.org.byc.disnumber.center.model.mapper.RuleInstanceHistoryMapper;
import cn.org.byc.disnumber.center.model.mapper.RuleInstanceMapper;
import cn.org.byc.disnumber.center.model.mapper.RuleMapper;
import cn.org.byc.disnumber.center.server.domain.rule.impl.ConstantRule;
import cn.org.byc.disnumber.center.server.domain.rule.impl.DateRule;
import cn.org.byc.disnumber.center.server.domain.rule.impl.EnumRule;
import cn.org.byc.disnumber.center.server.domain.rule.impl.SerialRule;
import cn.org.byc.disnumber.center.server.domain.rule.impl.SpecifiedRule;
import cn.org.byc.disnumber.center.server.dto.RuleInstanceDto;
import cn.org.byc.disnumber.center.server.service.IRuleEditService;
import cn.org.byc.disnumber.center.server.service.IRuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RuleEditService implements IRuleEditService {
    private final IRuleService ruleService;
    private final RuleMapper ruleMapper;
    private final RuleInstanceMapper ruleInstanceMapper;
    private RuleInstanceHistoryMapper historyMapper;
    private final CuratorFramework curatorFramework;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final Environment env;
    public RuleEditService(IRuleService ruleService, ObjectMapper objectMapper,
       CuratorFramework curatorFramework, RabbitTemplate rabbitTemplate, Environment env,
       RuleInstanceMapper ruleInstanceMapper, RuleMapper ruleMapper, RuleInstanceHistoryMapper historyMapper){
        this.ruleService = ruleService;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.env = env;
        this.curatorFramework = curatorFramework;
        this.ruleInstanceMapper = ruleInstanceMapper;
        this.ruleMapper = ruleMapper;
        this.historyMapper = historyMapper;
    }
    @Override
    public String generate(GenerateDto dto) throws Exception {
        List<Rule> rules = ruleService.getRuleByCode(dto.getCode());
        if (rules == null || rules.size() == 0) {
            throw new InvalidInstanceKeyException();
        }
        StringBuilder builder = new StringBuilder();
        MapType valueType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        Map<String, Object> param = dto.getParams();

        for (Rule rule : rules) {
            RuleType ruleType = rule.getType();
            switch (ruleType) {
                case CONSTANT:
                    ConstantRule constantRule = new ConstantRule(rule.getVal());
                    String constant = constantRule.process(param);
                    if (constant != null) {
                        builder.append(constant);
                    }
                    break;
                case SPECIFIED:
                    SpecifiedRule specifiedRule = new SpecifiedRule(rule.getCode());
                    String specified = specifiedRule.process(param);
                    if (specified != null) {
                        builder.append(specified);
                    }
                    break;
                case DATE:
                    DateRule dateRule = new DateRule(rule.getFormat(), rule.getCode());
                    String date = dateRule.process(param);
                    if (date != null) {
                        builder.append(date);
                    }
                    break;
                case SERIAL:
                    SerialRule serialRule = new SerialRule(rule.getLen(), rule.getMin(), rule.getMax(), rule.getStep(),
                            rule.getPadding().charAt(0),
                            dto.getCode(),
                            rabbitTemplate, env, curatorFramework);
                    serialRule.setPrex("/" + dto.getCode() + "/" + builder.toString());
                    String serial = serialRule.process(param);
                    if (serial != null) {
                        builder.append(serial);
                    }
                    break;
                case ENUM:
                    Map<String, Object> o = objectMapper.readValue(rule.getVal(), valueType);
                    EnumRule enumRule = new EnumRule(rule.getCode(), o);
                    String enums = enumRule.process(param);
                    if (enums != null) {
                        builder.append(enums);
                    }
                    break;
                default:
                    break;
            }
        }
        if (builder.length() > 0) {
            return builder.toString();
        } else {
            return null;
        }
    }

    @Override
    public void save(RuleInstanceDto ruleInstanceDto) {
        RuleInstance ruleInstance = ruleInstanceMapper.selectByCode(ruleInstanceDto.getCode());
        if (ruleInstance != null) {
            throw new InstanceKeyExistsException();
        } else {
            ruleService.insertRule(ruleInstanceDto);
        }
    }

    @Override
    public void update(RuleInstanceDto ruleInstanceDto) {
        RuleInstance ruleInstance = ruleInstanceMapper.selectByCode(ruleInstanceDto.getCode());
        if (ruleInstance == null) {
            throw new InvalidInstanceKeyException();
        } else {
            ruleService.updateRule(ruleInstance, ruleInstanceDto);
        }
    }

    @Override
    public List<RuleInstance> listInstance() {
       return ruleInstanceMapper.selectAll();
    }

    @Override
    public List<Rule> listRule(String code) {
        return ruleMapper.selectByRuleCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInstance(Integer id) {
        RuleInstance ruleInstance = ruleInstanceMapper.selectByPrimaryKey(id);
        if (ruleInstance == null) {
            throw new InvalidInstanceKeyException();
        } else {
            ruleService.deleteInstance(ruleInstance);
        }
    }

    @Override
    public void deleteRule(Integer id) {
        Rule rule = ruleMapper.selectByPrimaryKey(id);
        if (rule == null) {
            throw new InvalidRuleIdException();
        } else {
            RuleInstance ruleInstance = ruleInstanceMapper.selectByPrimaryKey(rule.getInstanceId());
            ruleService.deleteRule(ruleInstance, rule);
        }
    }

    @Override
    public void recoverRuleCode(Date time) throws Exception {
        List<RuleInstanceHistory> instanceHistories = historyMapper.selectCoveryRuleCode(time);
        for (RuleInstanceHistory history : instanceHistories) {
            DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(curatorFramework, history.getCode()
                    , new RetryUntilElapsed(10000, 1000));
            InterProcessMutex processMutex = new InterProcessMutex(curatorFramework, history.getCode() + "-lock");
            try {
                if (processMutex.acquire(10, TimeUnit.SECONDS)) {
                    AtomicValue<Integer> atomicValue = atomicInteger.get();
                    if (atomicValue.succeeded()) {
                        atomicInteger.initialize(history.getVal());
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                processMutex.release();
            }
        }
    }

    @Override
    public void recoverRuleInstanceCode(String code) throws Exception {
        List<RuleInstanceHistory> instanceHistories = historyMapper.selectCoveryRuleInstanceCode(code);
        for (RuleInstanceHistory history : instanceHistories) {
            DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(curatorFramework, history.getCode()
                    , new RetryUntilElapsed(10000, 1000));
            InterProcessMutex processMutex = new InterProcessMutex(curatorFramework, history.getCode() + "-lock");
            try {
                if (processMutex.acquire(10, TimeUnit.SECONDS)) {
                    AtomicValue<Integer> atomicValue = atomicInteger.get();
                    if (atomicValue.succeeded()) {
                        atomicInteger.initialize(history.getVal());
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                processMutex.release();
            }
        }
    }


}
