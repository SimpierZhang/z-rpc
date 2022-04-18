package com.zrpc.extension;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Zjw
 * @Description:
 * @Create 2022-04-14 17:24
 * @Modifier:
 */
@Setter
@Getter
public class Holder<T>
{
    private T value;
}
