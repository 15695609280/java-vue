<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="住院号" prop="admNo">
            <el-input v-model="queryParams.admNo" placeholder="请输入住院号" clearable style="width: 160px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="科室" prop="deptId">
            <el-select v-model="queryParams.deptId" clearable placeholder="科室" style="width: 140px">
               <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
            </el-select>
         </el-form-item>
         <el-form-item label="状态" prop="admStatus">
            <el-select v-model="queryParams.admStatus" clearable placeholder="状态" style="width: 120px">
               <el-option v-for="d in his_adm_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['his:admission:add']">住院登记</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="admList" border>
         <el-table-column label="住院号" align="center" prop="admNo" width="130" />
         <el-table-column label="患者" align="center" prop="patientName" width="90" />
         <el-table-column label="科室" align="center" prop="deptName" width="90" />
         <el-table-column label="病区" align="center" prop="wardName" width="110" />
         <el-table-column label="床号" align="center" prop="bedNo" width="80" />
         <el-table-column label="主治医生" align="center" prop="doctorName" width="100" />
         <el-table-column label="入院时间" align="center" prop="inDate" width="150">
            <template #default="s"><span>{{ parseTime(s.row.inDate) }}</span></template>
         </el-table-column>
         <el-table-column label="押金" align="center" prop="deposit" width="90" />
         <el-table-column label="状态" align="center" width="80">
            <template #default="s"><dict-tag :options="his_adm_status" :value="s.row.admStatus" /></template>
         </el-table-column>
         <el-table-column label="操作" width="110" align="center" fixed="right">
            <template #default="scope">
               <el-button v-if="scope.row.admStatus === '0'" link type="warning" @click="handleDischarge(scope.row)" v-hasPermi="['his:admission:edit']">办理出院</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 住院登记: 患者->科室->病区->空床 四级联动 -->
      <el-dialog title="住院登记" v-model="open" width="640px" append-to-body>
         <el-form ref="admRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="患者" prop="patientId">
               <el-select v-model="form.patientId" filterable remote :remote-method="searchPatient" placeholder="输入姓名搜索" style="width:100%">
                  <el-option v-for="p in patientOptions" :key="p.patientId" :label="p.patientName + '(' + p.patientNo + ')'" :value="p.patientId" />
               </el-select>
            </el-form-item>
            <el-form-item label="科室" prop="deptId">
               <el-select v-model="form.deptId" placeholder="选择科室" style="width:100%" @change="onDeptChange">
                  <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
               </el-select>
            </el-form-item>
            <el-form-item label="病区" prop="wardId">
               <el-select v-model="form.wardId" placeholder="选择病区" style="width:100%" @change="onWardChange">
                  <el-option v-for="w in wardOptions" :key="w.wardId" :label="w.wardName + ' (' + w.location + ')'" :value="w.wardId" />
               </el-select>
            </el-form-item>
            <el-form-item label="床位" prop="bedId">
               <el-select v-model="form.bedId" placeholder="仅显示空闲床位" style="width:100%">
                  <el-option v-for="b in freeBeds" :key="b.bedId" :label="b.bedNo + '床 ¥' + b.pricePerDay + '/天'" :value="b.bedId" />
               </el-select>
            </el-form-item>
            <el-form-item label="主治医生" prop="doctorId">
               <el-select v-model="form.doctorId" filterable style="width:100%">
                  <el-option v-for="d in doctorOptions" :key="d.doctorId" :label="d.doctorName" :value="d.doctorId" />
               </el-select>
            </el-form-item>
            <el-form-item label="预缴押金" prop="deposit"><el-input-number v-model="form.deposit" :precision="2" :min="0" controls-position="right" /></el-form-item>
            <el-form-item label="入院诊断" prop="diagnosisIn"><el-input v-model="form.diagnosisIn" type="textarea" :rows="2" /></el-form-item>
         </el-form>
         <template #footer>
            <el-button type="primary" @click="submit">确认登记</el-button>
            <el-button @click="open = false">取 消</el-button>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="HisAdmission">
import { listAdmission, admit } from "@/api/his/admission"
import { listPatient } from "@/api/his/patient"
import { listDepartment } from "@/api/his/dept"
import { listWard } from "@/api/his/ward"
import { listFreeBed } from "@/api/his/bed"
import { listDoctor } from "@/api/his/doctor"
import { discharge } from "@/api/his/admission"

const { proxy } = getCurrentInstance()
const { his_adm_status } = useDict("his_adm_status")

const admList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const deptOptions = ref([]), wardOptions = ref([]), freeBeds = ref([]), patientOptions = ref([]), doctorOptions = ref([])
const form = ref({})

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, admNo: undefined, deptId: undefined, admStatus: undefined } })
const { queryParams } = toRefs(data)
const rules = {
  patientId: [{ required: true, message: "请选择患者", trigger: "change" }],
  deptId: [{ required: true, message: "请选择科室", trigger: "change" }],
  wardId: [{ required: true, message: "请选择病区", trigger: "change" }],
  bedId: [{ required: true, message: "请选择床位", trigger: "change" }],
  doctorId: [{ required: true, message: "请选择医生", trigger: "change" }],
  deposit: [{ required: true, message: "押金不能为空", trigger: "blur" }]
}

function getList() {
  loading.value = true
  listAdmission(queryParams.value).then(res => { admList.value = res.data; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery() }

function searchPatient(kw) { listPatient({ pageNum: 1, pageSize: 50, patientName: kw || undefined }).then(res => patientOptions.value = res.data) }

function handleAdd() {
  form.value = { patientId: undefined, deptId: undefined, wardId: undefined, bedId: undefined, doctorId: undefined, deposit: 3000, diagnosisIn: undefined }
  wardOptions.value = []; freeBeds.value = []
  searchPatient("")
  open.value = true
}

function onDeptChange() {
  form.value.wardId = undefined; form.value.bedId = undefined; freeBeds.value = []
  listWard({ pageNum: 1, pageSize: 100, deptId: form.value.deptId }).then(res => wardOptions.value = res.data)
}
function onWardChange() {
  form.value.bedId = undefined
  listFreeBed(form.value.wardId).then(res => freeBeds.value = res.data || [])
}

function submit() {
  proxy.$refs["admRef"].validate(valid => {
    if (!valid) return
    admit(form.value).then(() => { proxy.$modal.msgSuccess("住院登记成功，床位已占用"); open.value = false; getList() })
  })
}

function handleDischarge(row) {
  proxy.$modal.confirm(`确认为患者 ${row.patientName} 办理出院？将释放床位 ${row.bedNo} 并生成床位费账单。`).then(() => discharge(row.admId)).then(() => { proxy.$modal.msgSuccess("已出院，床位已释放"); getList() }).catch(() => {})
}

listDepartment({ pageNum: 1, pageSize: 100 }).then(res => deptOptions.value = res.data)
listDoctor({ pageNum: 1, pageSize: 200 }).then(res => doctorOptions.value = res.data)
getList()
</script>
