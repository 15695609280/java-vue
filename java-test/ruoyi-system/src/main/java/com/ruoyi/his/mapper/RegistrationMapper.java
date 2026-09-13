package com.ruoyi.his.mapper;

import com.ruoyi.his.domain.Registration;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 挂号Mapper接口
 */
public interface RegistrationMapper
{
    public Registration selectRegistrationByRegId(Long regId);

    public List<Registration> selectRegistrationList(Registration registration);

    public int insertRegistration(Registration registration);

    public int updateRegistration(Registration registration);

    public int deleteRegistrationByRegId(Long regId);

    public int deleteRegistrationByRegIds(Long[] regIds);

    /** 查询排班当前最大排队号 */
    public Integer selectMaxQueueNo(Long scheduleId);

    /** 修改就诊状态 */
    public int updateVisitStatus(@Param("regId") Long regId, @Param("visitStatus") String visitStatus);

    /** 修改缴费状态 */
    public int updatePayStatus(@Param("regId") Long regId, @Param("payStatus") String payStatus);

    /** 候诊队列(按排队号) */
    public List<Registration> selectQueueList(Registration registration);
}
