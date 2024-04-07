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

package cn.org.byc.disnumber.center.model.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

@Getter
public enum RuleType implements IEnum<String> {
    CONSTANT("CT", "常量"),
    DATE("DT","日期"),
    ENUM("EM", "枚举"),
    SERIAL("SN", "流水号"),
    SPECIFIED("SP","指定值")

    ;

    private RuleType(String code, String description){
        this.code = code;
        this.description = description;
    }
    private final String code;
    private final String description;

    @Override
    public String getValue() {
        return code;
    }

    public static RuleType valueByCode(String code) {
        if (CONSTANT.getCode().equals(code)) {
            return CONSTANT;
        } else if (DATE.getCode().equals(code)) {
            return DATE;
        } else if (ENUM.getCode().equals(code)) {
            return ENUM;
        } else if (SERIAL.getCode().equals(code)) {
            return SERIAL;
        }
        else if (SPECIFIED.getCode().equals(code)) {
            return SPECIFIED;
        } else {
            return null;
        }
    }
}
