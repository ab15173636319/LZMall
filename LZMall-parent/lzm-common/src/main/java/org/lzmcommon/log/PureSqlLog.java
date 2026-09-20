package org.lzmcommon.log;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class PureSqlLog implements Log {
    private final Logger logger = LoggerFactory.getLogger(PureSqlLog.class);


    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String s, Throwable e) {
        System.err.println(s);
        logger.error(s, e);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }
}
