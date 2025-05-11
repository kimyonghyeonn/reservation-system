package kr.co.wikibook.backend.common.log.UserLog.mapper;

import kr.co.wikibook.backend.common.log.UserLog.model.UserLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLogMapper {
    void insertUserLog(UserLog log);
    void archiveLogsOlderThan(int days);
    void deleteLogsOlderThan(int days);
}
