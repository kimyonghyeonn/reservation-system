package kr.co.wikibook.backend.common.log.MailLog.mapper;

import kr.co.wikibook.backend.common.log.MailLog.model.PasswordResetLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordResetLogMapper {
    void insertPasswordResetLog(PasswordResetLog log);
}
