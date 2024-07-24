package com.example.demo.baseResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    @Builder.Default
    String code = "00";

    @Builder.Default
    String message = "SUCCESS";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data;
}
