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

package cn.org.byc.disnumber.center.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class ResultModel<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;

    public static <T> ResultModel<T> success(){
        return restResult(null,true,null);
    }

    public static <T> ResultModel<T> success(T data){
        return restResult(data,true,null);
    }

    public static <T> ResultModel<T> fail(){
        return restResult(null,false,null);
    }
    public static <T> ResultModel<T> fail(String message){
        return restResult(null,false,message);
    }

    private static <T> ResultModel<T> restResult(T data, boolean success, String msg) {
        ResultModel<T> resultModel = new ResultModel();
        resultModel.setSuccess(success);
        resultModel.setData(data);
        resultModel.setMessage(msg);
        return resultModel;
    }
}
