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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.util.StringUtils;

import java.util.Map;

public class DateRule implements Rule {
    private static final String DEFAULT_REG = "yyyy-MM-dd";

    private static final DateTimeFormatter DEFAULT_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DEFAULT_REG).toFormatter();
    /**
     * 日期格式
     */
    private String format;

    /**
     * 对应日期的key
     */
    private String code;

    public DateRule(String format, String code) {
        this.format = format;
        this.code = code;
    }

    @Override
    public String process(Map<String, Object> params) {
        //如果为空默认为当前时间
        if (StringUtils.isEmpty(code)) {
            return DateTime.now().toString(format);
        } else {
            Object o = params.get(code);
            if (o == null) {
                throw new IllegalArgumentException(String.format("%s:无效的日期key", code));
            } else {
                if (o instanceof String) {
                    String p = (String) o;
                    return DateTime.parse(p, DEFAULT_FORMATTER).toString(format);
                } else {
                    throw new IllegalArgumentException(String.format("%s:无效的日期格式(%s)", code, DEFAULT_REG));
                }
            }
        }
    }
}
