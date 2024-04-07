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

public class SpecifiedRule implements Rule {
    /**
     * 对应指定的key
     */
    private String code;

    public SpecifiedRule(String code) {
        this.code = code;
    }


    @Override
    public String process(Map<String, Object> params) throws Exception {
        Object o = params.get(code);
        if (o == null) {
            throw new IllegalArgumentException(String.format("%s:无效的指定key", code));
        } else {
            if (o instanceof String) {
                return (String) o;
            } else {
                throw new IllegalArgumentException(String.format("%s:无效的指定key值(%s)", code, o));
            }
        }
    }
}
