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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ConstantValidator implements Validator {
    private static final String regex ="[a-zA-Z0-9\\_\\&]+?";
    @Override
    public boolean supports(Class<?> clazz) {
        return RuleDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RuleDto o = (RuleDto) target;
        if (StringUtils.isEmpty(o.getVal())){
            errors.rejectValue("val","rule.val.NotNull","常量值必填");
        }

        if(!(o.getVal().matches(regex) && o.getVal().length()<50)){
            errors.rejectValue("val", "rule.val.regex", "枚举key不符合字母+数字+_或者& 和长度小于50");
        }
    }
}
