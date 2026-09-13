package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.domain.Doctor;
import com.ruoyi.his.domain.Registration;
import com.ruoyi.his.domain.Schedule;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.mapper.DoctorMapper;
import com.ruoyi.his.mapper.RegistrationMapper;
import com.ruoyi.his.mapper.ScheduleMapper;
import com.ruoyi.his.service.IRegistrationService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 挂号Service业务层处理
 */
@Service
public class RegistrationServiceImpl implements IRegistrationService
{
    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public Registration selectRegistrationByRegId(Long regId)
    {
        return registrationMapper.selectRegistrationByRegId(regId);
    }

    @Override
    public List<Registration> selectRegistrationList(Registration registration)
    {
        return registrationMapper.selectRegistrationList(registration);
    }

    @Override
    public int insertRegistration(Registration registration)
    {
        registration.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = registrationMapper.insertRegistration(registration);
        return rows;
    }

    @Override
    public int updateRegistration(Registration registration)
    {
        registration.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return registrationMapper.updateRegistration(registration);
    }

    @Override
    public int deleteRegistrationByRegIds(Long[] regIds)
    {
        return registrationMapper.deleteRegistrationByRegIds(regIds);
    }

    @Override
    public int deleteRegistrationByRegId(Long regId)
    {
        return registrationMapper.deleteRegistrationByRegId(regId);
    }

    /** 挂号：生成单号/排队号，扣号源，生成挂号费账单 */
    @Override
    @Transactional
    public Long register(Registration registration)
    {
        Schedule schedule = scheduleMapper.selectScheduleByScheduleId(registration.getScheduleId());
        if (schedule == null || !"0".equals(schedule.getStatus()))
        {
            throw new ServiceException("排班不存在或已停诊");
        }
        if (scheduleMapper.decrQuota(schedule.getScheduleId()) <= 0)
        {
            throw new ServiceException("该时段号源已约满");
        }
        Doctor doctor = doctorMapper.selectDoctorByDoctorId(schedule.getDoctorId());
        registration.setRegNo("GH" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
        registration.setQueueNo(registrationMapper.selectMaxQueueNo(schedule.getScheduleId()) + 1);
        registration.setDeptId(schedule.getDeptId());
        registration.setDoctorId(schedule.getDoctorId());
        registration.setRegDate(schedule.getWorkDate());
        registration.setTimeSlot(schedule.getTimeSlot());
        registration.setRegFee(doctor != null && doctor.getRegFee() != null ? doctor.getRegFee() : BigDecimal.ZERO);
        registration.setVisitStatus("0");
        registration.setPayStatus("0");
        registrationMapper.insertRegistration(registration);

        Charge charge = new Charge();
        charge.setChargeNo("GHF" + registration.getRegNo());
        charge.setPatientId(registration.getPatientId());
        charge.setSourceType("0");
        charge.setSourceId(registration.getRegId());
        charge.setItemName("挂号费");
        charge.setPrice(registration.getRegFee());
        charge.setQuantity(1);
        charge.setAmount(registration.getRegFee());
        charge.setChargeStatus("0");
        chargeMapper.insertCharge(charge);
        return registration.getRegId();
    }

    /** 状态流转: 1叫号就诊 3退号(退号源+作废未缴挂号费) 4过号 */
    @Override
    @Transactional
    public int changeStatus(Long regId, String status)
    {
        Registration reg = registrationMapper.selectRegistrationByRegId(regId);
        if (reg == null) { throw new ServiceException("挂号单不存在"); }
        if ("3".equals(status))
        {
            if (!"0".equals(reg.getVisitStatus()) && !"4".equals(reg.getVisitStatus()))
            {
                throw new ServiceException("只有待就诊/过号才能退号");
            }
            if (reg.getScheduleId() != null) { scheduleMapper.incrQuota(reg.getScheduleId()); }
            chargeMapper.cancelBySource(regId, "0");
        }
        else if ("4".equals(status) && !"0".equals(reg.getVisitStatus()) && !"1".equals(reg.getVisitStatus()))
        {
            throw new ServiceException("当前状态不能过号");
        }
        else if ("1".equals(status) && !"0".equals(reg.getVisitStatus()) && !"4".equals(reg.getVisitStatus()))
        {
            throw new ServiceException("当前状态不能叫号");
        }
        return registrationMapper.updateVisitStatus(regId, status);
    }

    @Override
    public List<Registration> selectQueueList(Registration registration)
    {
        return registrationMapper.selectQueueList(registration);
    }
}
