package cn.mijack.meme.remote;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> success(T t) {
        return success(t, "success");
    }

    public static <T> Result<T> success(T t, String msg) {
        return new Result<T>(200, msg, t);
    }

    public static <T> Result<T> failure(int code, String msg) {
        return new Result<T>(code, msg, null);
    }
}
