package com.magic.api.commons;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * @author yongkun
 * @author jolestar
 */
public class DefaultHttpClientAceessLog implements ApiHttpClient.AccessLog {
    private static final Logger access = LogManager.getLogger(DefaultHttpClientAceessLog.class);

    public void accessLog(long time, String method, int status, int len,
                          String url, String post, String ret) {
        if (post != null) {
            post.replaceAll("\n", "");
        }
        if (ret != null) {
            ret = ret.trim();
            ret = ret.replaceAll("\n", "");
        }
        //TODO
        //String requestId = ThreadLocalContext.getInstance().get().getRequestId();
        String requestId = "";
        access.info(format("%s %s %s %s %s %s %s %s", requestId, time, method, status, len, url,
                isEmpty(post) ? "-" : post, isEmpty(ret) ? "-" : ret));
    }
}
