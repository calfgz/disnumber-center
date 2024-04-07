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

package cn.org.byc.disnumber.center.server.validator;

import cn.org.byc.disnumber.center.server.dto.RuleDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

@Component
public class EnumValidator implements Validator {
    private static final String regex ="[a-zA-Z0-9\\_\\&]+?";

    @Autowired
    ObjectMapper objectMapper;
    @Override
    public boolean supports(Class<?> clazz) {
        return RuleDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RuleDto o = (RuleDto) target;

        if (StringUtils.isEmpty(o.getCode())) {
            errors.rejectValue("code", "rule.code.NotNull", "枚举key必填");
        }
        if(!(o.getCode().matches(regex) && o.getCode().length()<50)){
            errors.rejectValue("code", "rule.code.regex", "枚举key不符合字母+数字+_或者& 和长度小于50");
        }
        if (StringUtils.isEmpty(o.getVal())) {
            errors.rejectValue("val", "rule.val.NotNull", "枚举值列表必填");
        }
        if(o.getVal().length()>49 ){

            errors.rejectValue("val", "rule.val.regex", "枚举key不符合字母+数字+_或者& 和长度小于50");
        }

        Map mapTypes = null;
        try {
            mapTypes = objectMapper.readValue(o.getVal(), Map.class);
        } catch (JsonProcessingException e) {
            errors.rejectValue("val","rule.val.Serializable","反序列错误，请检查格式是否正确");
        }
        for (Object obj : mapTypes.keySet()){
            if(!(obj.toString().matches(regex) ||mapTypes.get(obj).toString().matches(regex))){

                errors.rejectValue("val", "rule.val.regex", "枚举key不符合字母+数字+_或者& 和长度小于50");
            }
        }
    }
}
