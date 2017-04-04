package com.magic.api.commons;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author jolestar
 */
public class HttpResponse {
    private final int status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(int status, Map<String, String> headers, byte[] body) {
        super();
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyAsString(String charset) {
        if (this.body == null) {
            return null;
        }
        try {
            return new String(this.body, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(body);
        }
    }

    public String getBodyAsString() {
        return this.getBodyAsString(ApacheHttpClient.DEFAULT_CHARSET);
    }
}

