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

package cn.org.byc.disnumber.center.api.service;

import cn.org.byc.disnumber.center.api.dto.GenerateDto;
import cn.org.byc.disnumber.center.api.dto.GenerateResultDto;
import cn.org.byc.disnumber.center.api.dto.ResultModel;
import org.springframework.validation.annotation.Validated;

/**
 * 编码生成接口
 *
 * @author ken
 */
public interface IDisNumberService {

    /**
     * 生成编码
     *
     * @param dto 参数
     * @return GenerateResultDto
     */
    ResultModel<GenerateResultDto> generate(@Validated GenerateDto dto);
}
