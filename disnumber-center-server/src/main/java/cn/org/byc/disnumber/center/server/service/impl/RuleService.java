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

import cn.org.byc.disnumber.center.api.exception.InvalidRuleConfigException;
import cn.org.byc.disnumber.center.api.exception.InvalidRuleTypeException;
import cn.org.byc.disnumber.center.model.entity.Rule;
import cn.org.byc.disnumber.center.model.entity.RuleInstance;
import cn.org.byc.disnumber.center.model.enums.RuleType;
import cn.org.byc.disnumber.center.model.mapper.RuleInstanceMapper;
import cn.org.byc.disnumber.center.model.mapper.RuleMapper;
import cn.org.byc.disnumber.center.server.dto.RuleDto;
import cn.org.byc.disnumber.center.server.dto.RuleInstanceDto;
import cn.org.byc.disnumber.center.server.service.IRuleService;
import cn.org.byc.disnumber.center.server.validator.ConstantValidator;
import cn.org.byc.disnumber.center.server.validator.DateValidator;
import cn.org.byc.disnumber.center.server.validator.EnumValidator;
import cn.org.byc.disnumber.center.server.validator.SerialValidator;
import cn.org.byc.disnumber.center.server.validator.SpecifiedValidator;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Set;

@Service
public class RuleService implements IRuleService {

    private final RuleMapper ruleMapper;
    private final RuleInstanceMapper ruleInstanceMapper;

    private final ConstantValidator constantValidator;
    private final DateValidator dateValidator;
    private final EnumValidator enumValidator;
    private final SerialValidator serialValidator;
    private final SpecifiedValidator specifiedValidator;

    public RuleService(RuleMapper ruleMapper, RuleInstanceMapper ruleInstanceMapper,
           ConstantValidator constantValidator, DateValidator dateValidator, EnumValidator enumValidator,
           SerialValidator serialValidator, SpecifiedValidator specifiedValidator) {
        this.ruleMapper = ruleMapper;
        this.ruleInstanceMapper = ruleInstanceMapper;
        this.constantValidator = constantValidator;
        this.dateValidator = dateValidator;
        this.enumValidator = enumValidator;
        this.serialValidator = serialValidator;
        this.specifiedValidator = specifiedValidator;
    }

    private StringBuilder checkRule(RuleDto dto, Validator validator) {
        //校验
        Errors errors = new BeanPropertyBindingResult(dto, "Rule");
        ValidationUtils.invokeValidator(validator, dto, errors);
        StringBuilder errorMsg = null;
        if (errors.hasErrors()) {
            errorMsg = new StringBuilder();
            List<ObjectError> allErrors = errors.getAllErrors();
            for (ObjectError error : allErrors) {
                errorMsg.append(error.getDefaultMessage()).append(",");
            }
        }
        return errorMsg;
    }
    @Override
    public List<Rule> getRuleByCode(String code) {
        return ruleMapper.selectByRuleCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRule(RuleInstanceDto ruleInstanceDto) {
        RuleInstance ruleInstance = new RuleInstance();
        ruleInstance.setCode(ruleInstanceDto.getCode());
        ruleInstance.setDescription(ruleInstanceDto.getDescription());
        ruleInstanceMapper.insertSelective(ruleInstance);
        appendRule(ruleInstance, ruleInstanceDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#instance.code")
    public List<Rule> updateRule(RuleInstance instance, RuleInstanceDto ruleInstanceDto) {
        instance.setDescription(ruleInstanceDto.getDescription());
        ruleInstanceMapper.updateByPrimaryKeySelective(instance);
        //移除规则
        ruleMapper.deleteByRuleId(instance.getId());
        appendRule(instance, ruleInstanceDto);
        return ruleMapper.selectByRuleCode(instance.getCode());
    }

    @Override
    @CacheEvict(key = "#instance.code")
    @Transactional(rollbackFor = Exception.class)
    public void deleteInstance(RuleInstance instance) {
        ruleMapper.deleteByRuleId(instance.getId());
        ruleInstanceMapper.deleteByPrimaryKey(instance.getId());
    }

    @Override
    @CacheEvict(key = "#instance.code")
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(RuleInstance instance, Rule rule) {
        ruleMapper.deleteByPrimaryKey(rule.getId());
    }

    @Override
    public void cleanRuleByCodeCache(String code) {
        // TODO: 清理缓存然后重新加载
    }

    /**
     * 填充规则
     *
     * @param instance
     * @param ruleInstanceDto
     */
    private void appendRule(RuleInstance instance, RuleInstanceDto ruleInstanceDto) {
        if(ruleInstanceDto.getRuleDtos()==null){
            throw new InvalidRuleConfigException();
        }
        Set<RuleDto> ruleDtos = ruleInstanceDto.getRuleDtos();

        Rule rule = null;
        StringBuilder builder = new StringBuilder();
        for (RuleDto dto : ruleDtos) {
            RuleType ruleType = dto.getType();
            if (ruleType == null) {
                throw new InvalidRuleTypeException();
            }
            switch (ruleType) {
                case CONSTANT:
                    if (checkRule(dto, constantValidator) != null) {
                        builder.append(checkRule(dto, constantValidator));
                    }
                    break;
                case SPECIFIED:
                    if (checkRule(dto, specifiedValidator) != null) {
                        builder.append(checkRule(dto, specifiedValidator));
                    }
                    break;
                case DATE:
                    if (checkRule(dto, dateValidator) != null) {
                        builder.append(checkRule(dto, dateValidator));
                    }
                    break;
                case ENUM:
                    if (checkRule(dto, enumValidator) != null) {
                        builder.append(checkRule(dto, enumValidator));
                    }
                    break;
                case SERIAL:
                    if (checkRule(dto, serialValidator) != null) {
                        builder.append(checkRule(dto, serialValidator));
                    }
                    break;
                default:
                    break;
            }
            rule = new Rule();
            BeanUtils.copyProperties(dto, rule);
            rule.setInstanceId(instance.getId());
            ruleMapper.insertSelective(rule);
        }
        if (builder.length() > 0) {
            throw new IllegalArgumentException(builder.toString());
        }
    }
}
