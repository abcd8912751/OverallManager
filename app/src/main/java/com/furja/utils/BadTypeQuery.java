package com.furja.utils;

import java.util.List;

/**
 * 寻找sourcetype=2时异常类型
 */

public interface BadTypeQuery {

    List<String> query(String input);
}
