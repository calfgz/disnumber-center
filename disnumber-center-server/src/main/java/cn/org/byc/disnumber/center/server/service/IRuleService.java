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

package cn.org.byc.disnumber.center.server.service;

import cn.org.byc.disnumber.center.model.entity.Rule;
import cn.org.byc.disnumber.center.model.entity.RuleInstance;
import cn.org.byc.disnumber.center.server.dto.RuleInstanceDto;

import java.util.List;

public interface IRuleService {

    /**
     * 获取规则集合
     *
     * @param code
     * @return
     */
    List<Rule> getRuleByCode(String code);

    /**
     * 插入规则
     * @param ruleInstanceDto
     */
    void insertRule(RuleInstanceDto ruleInstanceDto);

    /**
     * 更新规则
     *
     * @param instance
     * @param ruleInstanceDto
     * @return
     */
    List<Rule> updateRule(RuleInstance instance, RuleInstanceDto ruleInstanceDto);

    /**
     * 删除实例
     *
     * @param instance
     */
    void deleteInstance(RuleInstance instance);

    /**
     * 删除实例规则
     *
     * @param instance
     * @param rule
     */
    void deleteRule(RuleInstance instance, Rule rule);

    /**
     * 清理缓存
     *
     * @param code
     */
    void cleanRuleByCodeCache(String code);
}
