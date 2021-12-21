package com.niubicloud.exception;

/*
 *	结束运行请求
 * 这种情况一般是执行了quit()函数
 * 如果捕获到这种异常请往上抛
 */
@SuppressWarnings("serial")
public class FinishRequest extends RuntimeException {
}
