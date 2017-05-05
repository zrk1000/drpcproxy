package com.zrk1000.demo.exception;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/5
 * Time: 13:05
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -5875371379845226068L;

    /**
     * 生成序列异常时
     */
    public static final BizException DB_GET_SEQ_NEXT_VALUE_ERROR = new BizException(10040007, "序列生成超时");

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    public BizException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
        this.msg = String.format(msgFormat, args);
    }

    public BizException() {
        super();
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message) {
        super(message);
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    /**
     * 实例化异常
     *
     * @param msgFormat
     * @param args
     * @return
     */
    public BizException newInstance(String msgFormat, Object... args) {
        return new BizException(this.code, msgFormat, args);
    }

}
    