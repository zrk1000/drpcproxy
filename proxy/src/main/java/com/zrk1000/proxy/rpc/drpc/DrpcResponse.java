package com.zrk1000.proxy.rpc.drpc;

/**
 * Created by zrk-PC on 2017/4/11.
 */
public class DrpcResponse {

    private int code;

    private String msg;

    private Object data;

    private Exception exception;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setException(String name, String message,StackTraceElement[] stackTraceElements) {
        this.exception = new Exception(name,message,stackTraceElements);
    }

    public boolean hasException() {
        return exception != null;
    }

    class Exception{

        private String name;

        private String message;

        private StackTraceElement[] stackTraceElements;

        public Exception() {}

        public Exception(String name, String message,StackTraceElement[] stackTraceElements) {
            this.name = name;
            this.message = message;
            this.stackTraceElements = stackTraceElements;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public StackTraceElement[] getStackTraceElements() {
            return stackTraceElements;
        }

        public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
            this.stackTraceElements = stackTraceElements;
        }
    }
}
