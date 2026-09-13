package com.ruoyi.his.service;

import com.ruoyi.his.domain.Registration;
import java.util.List;

/**
 * 挂号Service接口
 */
public interface IRegistrationService
{
    public Registration selectRegistrationByRegId(Long regId);

    public List<Registration> selectRegistrationList(Registration registration);

    public int insertRegistration(Registration registration);

    public int updateRegistration(Registration registration);

    public int deleteRegistrationByRegIds(Long[] regIds);

    public int deleteRegistrationByRegId(Long regId);

    /** 挂号：生成单号/排队号，扣号源，生成挂号费账单 */
    public Long register(Registration registration);

    /** 状态流转: 1叫号就诊 3退号 4过号 */
    public int changeStatus(Long regId, String status);

    /** 候诊队列 */
    public List<Registration> selectQueueList(Registration registration);
}
