package wang.kidd.http;

import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author kidd
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = 7299767941867123952L;
    private Integer code;

    private byte[] body;

    public Response(int code, byte[] body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public byte[] getBodyAsBytes() {
        return body;
    }

    public String getBody() {
        try {
            return new String(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getText() {
        return getBody();
    }

    public <T> T toBean(Class<T> cls) {
        return JsonHelper.json2bean(getBody(), cls);
    }

    public Map<String, Object> toMap() {
        return JsonHelper.json2map(getBody());
    }

    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.code);
        str.append("\n");
        str.append(getBody());
        return str.toString();
    }
}
