package com.xiaoyang.diary.framework.mq.core;

public interface MessageIdempotentService {

    boolean claim(String eventId);

}
