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

import java.util.Map;

/**
 * 枚举规则
 *
 * @author ken
 */
public class EnumRule implements Rule {
    ;
    /**
     * 枚举key
     */
    private String code;

    /**
     * 枚举值
     */
    private Map<String, Object> val;

    public EnumRule(String code, Map<String, Object> val) {
        this.code = code;
        this.val = val;
    }
    @Override
    public String process(Map<String, Object> params) {
        Object v = params.get(code);
        if (v != null) {
            if (v instanceof String) {
                String k = (String) v;
                Object o = val.get(k);
                if (o == null) {
                    throw new IllegalArgumentException(code + ", 枚举key获取值为空");
                }
                return o.toString();
            } else {
                throw new IllegalArgumentException("无效的枚举格式:" + code);
            }
        } else {
            throw new IllegalArgumentException("无效的枚举key:" + code);
        }
    }
}
