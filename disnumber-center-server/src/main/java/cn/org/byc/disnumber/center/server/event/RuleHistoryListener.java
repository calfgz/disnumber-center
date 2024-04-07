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

package cn.org.byc.disnumber.center.server.event;

import cn.org.byc.disnumber.center.model.entity.RuleInstanceHistory;
import cn.org.byc.disnumber.center.model.mapper.RuleInstanceHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RuleHistoryListener {

    private final RuleInstanceHistoryMapper historyMapper;

    public RuleHistoryListener(RuleInstanceHistoryMapper historyMapper){
        this.historyMapper = historyMapper;
    }

    @RabbitListener(queues = "${rule.history.create.queue.name}", containerFactory = "singleListenerContainer")
    public void createRuleHistory(@Payload RuleInstanceHistory history) {
        if (log.isDebugEnabled()) {
            log.debug("创建历史记录:{}", history);
        }
        try {
            historyMapper.insertSelective(history);
        } catch (Exception e) {
            log.error("{}:创建历史记录异常", history, e.fillInStackTrace());
        }
    }
}
