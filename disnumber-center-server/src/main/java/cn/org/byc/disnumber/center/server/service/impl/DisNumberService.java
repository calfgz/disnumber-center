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

package cn.org.byc.disnumber.center.server.service.impl;

import cn.org.byc.disnumber.center.api.dto.GenerateDto;
import cn.org.byc.disnumber.center.api.dto.GenerateResultDto;
import cn.org.byc.disnumber.center.api.dto.ResultModel;
import cn.org.byc.disnumber.center.api.service.IDisNumberService;
import cn.org.byc.disnumber.center.server.service.IRuleEditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/number-center")
public class DisNumberService implements IDisNumberService {

    private final IRuleEditService ruleEditService;

    public DisNumberService(IRuleEditService ruleEditService){
        this.ruleEditService = ruleEditService;
    }
    @Override
    @PostMapping("/generate")
    public ResultModel<GenerateResultDto> generate(@RequestBody GenerateDto dto) {
        try {
            String no = ruleEditService.generate(dto);
            if (StringUtils.hasText(no)) {
                GenerateResultDto resultDto = new GenerateResultDto(no);
                return ResultModel.success(resultDto);
            } else {
                return ResultModel.fail("编号生成失败");
            }

        } catch (Exception e) {
            log.error("{},生成编码异常", dto, e.fillInStackTrace());
            return ResultModel.fail(e.getMessage());
        }
    }
}
