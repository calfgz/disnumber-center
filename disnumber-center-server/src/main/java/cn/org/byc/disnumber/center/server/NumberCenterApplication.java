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

package cn.org.byc.disnumber.center.server;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = {"cn.org.byc"})
@MapperScan(basePackages = "cn.org.byc.disnumber.center.model.mapper")
@EnableCaching
public class NumberCenterApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(NumberCenterApplication.class, args);
    }

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework build = CuratorFrameworkFactory.builder().connectString(env.getProperty("zk.host")).namespace(env.getProperty("zk.namepace")).retryPolicy(new RetryNTimes(5, 1000)).build();
        build.start();
        return build;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer(){
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }
}
