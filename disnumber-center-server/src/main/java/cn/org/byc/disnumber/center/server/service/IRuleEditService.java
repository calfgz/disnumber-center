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

import cn.org.byc.disnumber.center.api.dto.GenerateDto;
import cn.org.byc.disnumber.center.model.entity.Rule;
import cn.org.byc.disnumber.center.model.entity.RuleInstance;
import cn.org.byc.disnumber.center.server.dto.RuleInstanceDto;

import java.util.Date;
import java.util.List;

public interface IRuleEditService {

    /**
     * 生成编码
     * @param dto
     * @return
     */
    String generate(GenerateDto dto) throws Exception;

    /**
     * 保存规则实例
     *
     * @param ruleInstanceDto
     * @throws Exception
     */
    void save(RuleInstanceDto ruleInstanceDto);

    /**
     * 更新规则实例
     *
     * @param ruleInstanceDto
     */
    void update(RuleInstanceDto ruleInstanceDto);

    /**
     * 所有实例
     *
     * @return
     */
    List<RuleInstance> listInstance();

    /**
     * 对应实例的所有规则
     *
     * @param code
     * @return
     */
    List<Rule> listRule(String code);

    /**
     * 删除实例
     *
     * @param id
     */
    void deleteInstance(Integer id);

    /**
     * 删除规则
     *
     * @param id
     */
    void deleteRule(Integer id);

    /**
     * 恢复编码的流水号
     *
     * @param time
     */
    void recoverRuleCode(Date time) throws Exception;

    void recoverRuleInstanceCode(String code) throws Exception;

}
