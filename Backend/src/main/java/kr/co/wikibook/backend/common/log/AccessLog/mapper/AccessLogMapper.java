package kr.co.wikibook.backend.common.log.AccessLog.mapper;

import kr.co.wikibook.backend.common.log.AccessLog.model.AccessLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessLogMapper {
    void insertAccessLog(AccessLog accessLog);
}
