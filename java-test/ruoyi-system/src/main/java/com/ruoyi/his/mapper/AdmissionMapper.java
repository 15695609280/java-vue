package com.ruoyi.his.mapper;

import java.util.List;

import com.ruoyi.his.domain.Admission;

/**
 * 住院登记数据访问层接口。
 *
 * <p>该接口由 MyBatis 根据
 * {@code resources/mapper/his/AdmissionMapper.xml} 自动生成实现，负责
 * {@code his_admission} 表的住院记录查询、保存、更新和删除。查询结果
 * 除住院记录本身的字段外，还会通过关联患者、科室、病区、床位和医生表
 * 填充患者姓名、科室名称、床位号等展示字段。</p>
 *
 * <p>具体的业务校验（例如患者是否已经在院、床位是否可用以及办理出院
 * 时释放床位）由 {@code AdmissionService} 负责，本接口只负责数据库访问。</p>
 */
public interface AdmissionMapper
{
    /**
     * 按住院记录主键查询完整的住院登记信息。
     *
     * @param admId 住院记录主键，对应 {@code his_admission.adm_id}
     * @return 找到的住院记录；主键不存在时返回 {@code null}
     */
    public Admission selectAdmissionByAdmId(Long admId);

    /**
     * 按条件查询住院登记列表。
     *
     * <p>查询条件由 {@code admission} 中的非空字段决定：住院号使用精确匹配，
     * 患者 ID、科室 ID 和住院状态也使用精确匹配。结果按住院记录主键倒序
     * 返回，最新创建的记录排在前面。传入空对象时返回全部住院记录。</p>
     *
     * @param admission 查询条件对象，可为空；为空字段不会参与筛选
     * @return 符合条件的住院记录列表；没有匹配记录时返回空列表
     */
    public List<Admission> selectAdmissionList(Admission admission);

    /**
     * 新增一条住院登记记录。
     *
     * <p>XML 映射会根据对象中的非空字段动态生成 INSERT 语句，并通过
     * {@code useGeneratedKeys} 将数据库生成的自增主键回填到
     * {@code admission.admId}。</p>
     *
     * @param admission 待保存的住院登记信息
     * @return 实际插入的记录数，通常为 {@code 1}
     */
    public int insertAdmission(Admission admission);

    /**
     * 更新住院登记记录。
     *
     * <p>以 {@code admission.admId} 定位记录，仅更新对象中非空的业务字段，
     * 同时支持更新修改人、修改时间和备注等审计字段。</p>
     *
     * @param admission 包含主键及待更新字段的住院登记对象
     * @return 实际更新的记录数；主键不存在时通常为 {@code 0}
     */
    public int updateAdmission(Admission admission);

    /**
     * 按住院记录主键删除一条住院登记记录。
     *
     * @param admId 待删除的住院记录主键
     * @return 实际删除的记录数；主键不存在时通常为 {@code 0}
     */
    public int deleteAdmissionByAdmId(Long admId);


    /**
     * 批量删除住院登记记录。
     *
     * <p>参数数组会在 MyBatis XML 中展开为 {@code IN (...)} 条件。调用方应
     * 在进入 Mapper 前完成空数组和参数合法性校验，避免执行无意义的批量删除。</p>
     *
     * @param admIds 待删除的住院记录主键数组
     * @return 实际删除的记录数
     */
    public int deleteAdmissionByAdmIds(Long[] admIds);

    /**
     * 查询患者当前有效的在院记录。
     *
     * <p>只匹配住院状态为 {@code 0} 的记录，并按住院记录主键倒序取最新一条。
     * 该方法用于判断患者是否已经在院，也可用于读取其当前科室、病区和床位信息。</p>
     *
     * @param patientId 患者主键，对应 {@code his_admission.patient_id}
     * @return 患者最新的在院记录；患者当前不在院时返回 {@code null}
     */
    public Admission selectInHospitalByPatient(Long patientId);
}
