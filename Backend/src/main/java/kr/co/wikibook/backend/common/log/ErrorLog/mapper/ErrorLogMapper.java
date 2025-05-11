package kr.co.wikibook.backend.common.log.ErrorLog.mapper;

import kr.co.wikibook.backend.common.log.ErrorLog.model.ErrorLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorLogMapper {
    void insertErrorLog(ErrorLog errorLog);
}
