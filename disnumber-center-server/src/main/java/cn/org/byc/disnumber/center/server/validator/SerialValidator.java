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
public class SerialValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return RuleDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RuleDto o = (RuleDto) target;
        if (o.getLen() == null) {
            errors.rejectValue("len", "rule.len.NotNull", "流水号长度必填");
        }
        if (o.getMin() == null) {
            errors.rejectValue("min", "rule.min.NotNull", "流水号最小值必填");
        }
        if (o.getMax() == null) {
            errors.rejectValue("max", "rule.max.NotNull", "流水号最大值必填");
        }
        if (o.getStep() == null) {
            errors.rejectValue("step", "rule.step.NotNull", "流水号步长必填");
        }
        String padding = o.getPadding();
        if (StringUtils.isEmpty(padding)) {
            errors.rejectValue("padding", "rule.padding.NotNull", "流水号字符填充必填");
        } else {
            if (padding.length() > 1) {
                errors.rejectValue("padding", "rule.padding.Length", "流水号字符填充只允许设置一个字符");
            }
        }
    }
}
