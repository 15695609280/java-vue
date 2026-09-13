package com.ruoyi.his.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.his.domain.Admission;
import com.ruoyi.his.domain.Bed;
import com.ruoyi.his.domain.Charge;
import com.ruoyi.his.mapper.AdmissionMapper;
import com.ruoyi.his.mapper.BedMapper;
import com.ruoyi.his.mapper.ChargeMapper;
import com.ruoyi.his.service.IAdmissionService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 住院登记Service业务层处理
 */
@Service
public class AdmissionServiceImpl implements IAdmissionService
{
    @Autowired
    private AdmissionMapper admissionMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public Admission selectAdmissionByAdmId(Long admId)
    {
        return admissionMapper.selectAdmissionByAdmId(admId);
    }

    @Override
    public List<Admission> selectAdmissionList(Admission admission)
    {
        return admissionMapper.selectAdmissionList(admission);
    }

    @Override
    public int insertAdmission(Admission admission)
    {
        admission.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        int rows = admissionMapper.insertAdmission(admission);
        return rows;
    }

    @Override
    public int updateAdmission(Admission admission)
    {
        admission.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
        return admissionMapper.updateAdmission(admission);
    }

    @Override
    public int deleteAdmissionByAdmIds(Long[] admIds)
    {
        return admissionMapper.deleteAdmissionByAdmIds(admIds);
    }

    @Override
    public int deleteAdmissionByAdmId(Long admId)
    {
        return admissionMapper.deleteAdmissionByAdmId(admId);
    }

    /** 住院登记：校验床位 -> 占用 -> 生成住院号 */
    @Override
    @Transactional
    public Long admit(Admission admission)
    {
        if (selectInHospitalByPatient(admission.getPatientId()) != null)
        {
            throw new ServiceException("该患者已有在院记录，请先办理出院");
        }
        Bed bed = bedMapper.selectBedByBedId(admission.getBedId());
        if (bed == null || !"0".equals(bed.getStatus()))
        {
            throw new ServiceException("床位不可用，请重新选择");
        }
        Bed updBed = new Bed();
        updBed.setBedId(bed.getBedId());
        updBed.setStatus("1");
        bedMapper.updateBed(updBed);

        admission.setAdmNo("ZY" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", new java.util.Random().nextInt(1000)));
        admission.setInDate(new Date());
        admission.setAdmStatus("0");
        admissionMapper.insertAdmission(admission);
        return admission.getAdmId();
    }

    /** 出院：释放床位 + 按住院天数生成床位费账单 */
    @Override
    @Transactional
    public int discharge(Long admId)
    {
        Admission adm = admissionMapper.selectAdmissionByAdmId(admId);
        if (adm == null) { throw new ServiceException("住院记录不存在"); }
        if (!"0".equals(adm.getAdmStatus())) { throw new ServiceException("该患者已出院"); }
        Date now = new Date();
        Admission upd = new Admission();
        upd.setAdmId(admId);
        upd.setAdmStatus("1");
        upd.setOutDate(now);
        int rows = admissionMapper.updateAdmission(upd);
        if (adm.getBedId() != null)
        {
            Bed updBed = new Bed();
            updBed.setBedId(adm.getBedId());
            updBed.setStatus("0");
            bedMapper.updateBed(updBed);
            Bed bed = bedMapper.selectBedByBedId(adm.getBedId());
            long days = Math.max(1, (now.getTime() - adm.getInDate().getTime()) / (1000 * 3600 * 24) + 1);
            if (bed != null && bed.getPricePerDay() != null)
            {
                Charge charge = new Charge();
                charge.setChargeNo("ZYF" + adm.getAdmNo());
                charge.setPatientId(adm.getPatientId());
                charge.setSourceType("4");
                charge.setSourceId(admId);
                charge.setItemName("床位费(" + bed.getBedNo() + "床 x" + days + "天)");
                charge.setPrice(bed.getPricePerDay());
                charge.setQuantity((int) days);
                charge.setAmount(bed.getPricePerDay().multiply(new BigDecimal(days)));
                charge.setChargeStatus("0");
                chargeMapper.insertCharge(charge);
            }
        }
        return rows;
    }

    @Override
    public Admission selectInHospitalByPatient(Long patientId)
    {
        return admissionMapper.selectInHospitalByPatient(patientId);
    }
}
