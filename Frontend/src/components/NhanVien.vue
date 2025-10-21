<template>
  <div class="container py-4">
    <div class="card shadow-sm">
      <div class="card-body">
        <h5 class="card-title mb-3">{{ mode === 'edit' ? 'Cập nhật nhân viên' : 'Thêm nhân viên' }}</h5>

       
        <div v-if="generalError" class="alert alert-danger" role="alert">
          {{ generalError }}
        </div>

        <form @submit.prevent="onSubmit" novalidate>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Tên hiển thị</label>
              <input v-model="form.tenHienThi" type="text" class="form-control" :class="{'is-invalid': errors.tenHienThi}">
              <div class="invalid-feedback">{{ errors.tenHienThi }}</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Tên đăng nhập</label>
              <input v-model="form.username" type="text" class="form-control" :class="{'is-invalid': errors.username}">
              <div class="invalid-feedback">{{ errors.username }}</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Mật khẩu</label>
              <input v-model="form.passwords" type="password" class="form-control" :class="{'is-invalid': errors.passwords}">
              <div class="invalid-feedback">{{ errors.passwords }}</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Họ tên</label>
              <input v-model="form.hoTen" type="text" class="form-control" :class="{'is-invalid': errors.hoTen}">
              <div class="invalid-feedback">{{ errors.hoTen }}</div>
            </div>

            <div class="col-md-4 mb-3">
              <label class="form-label">Giới tính</label>
              <select v-model="form.gioiTinh" class="form-select" :class="{'is-invalid': errors.gioiTinh}">
                <option :value="1">Nam</option>
                <option :value="0">Nữ</option>
              </select>
              <div class="invalid-feedback">{{ errors.gioiTinh }}</div>
            </div>

            <div class="col-md-8 mb-3">
              <label class="form-label">Email</label>
              <input v-model="form.email" type="email" class="form-control" :class="{'is-invalid': errors.email}">
              <div class="invalid-feedback">{{ errors.email }}</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Số điện thoại</label>
              <input v-model="form.soDienThoai" type="text" class="form-control" :class="{'is-invalid': errors.soDienThoai}">
              <div class="invalid-feedback">{{ errors.soDienThoai }}</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Địa chỉ giao hàng</label>
              <input v-model="form.diaChiGiaoHang" type="text" class="form-control" :class="{'is-invalid': errors.diaChiGiaoHang}">
              <div class="invalid-feedback">{{ errors.diaChiGiaoHang }}</div>
            </div>

            <div class="col-md-4 mb-3">
              <label class="form-label">Role ID</label>
              <input v-model.number="form.roleId" type="number" class="form-control" min="1">
            </div>

            <div class="col-md-8 mb-3 d-flex align-items-end">
              <button class="btn btn-primary me-2" type="submit" :disabled="submitting">
                {{ submitting ? 'Đang gửi...' : (mode === 'edit' ? 'Cập nhật' : 'Thêm mới') }}
              </button>
              <button class="btn btn-secondary" type="button" @click="resetForm">Làm mới</button>
            </div>
          </div>
        </form>

       
        <div v-if="successMsg" class="alert alert-success mt-3">{{ successMsg }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { reactive, ref, toRefs, watch } from 'vue'

export default {
  name: 'NhanVienForm',
  props: {
  
    mode: { type: String, default: 'create' },
    
    initialData: { type: Object, default: () => ({}) }
  },
  setup(props) {
    const defaultForm = {
      id: null,
      tenHienThi: '',
      username: '',
      passwords: '',
      hoTen: '',
      gioiTinh: 1,
      email: '',
      soDienThoai: '',
      diaChiGiaoHang: '',
      roleId: 3,
      roleName: ''
    }

    const form = reactive({ ...defaultForm, ...props.initialData })
    const errors = reactive({})
    const generalError = ref('')
    const successMsg = ref('')
    const submitting = ref(false)

    // khi prop initialData thay đổi (khi edit), cập nhật form
    watch(() => props.initialData, (v) => {
      Object.assign(form, { ...defaultForm, ...v })
      clearErrors()
      generalError.value = ''
      successMsg.value = ''
    })

    function clearErrors() {
      Object.keys(errors).forEach(k => delete errors[k])
    }

    function resetForm() {
      Object.assign(form, defaultForm)
      clearErrors()
      generalError.value = ''
      successMsg.value = ''
    }

    function clientValidate() {
      clearErrors()
      let ok = true
      if (!form.tenHienThi || form.tenHienThi.trim().length === 0) {
        errors.tenHienThi = 'Tên hiển thị không được để trống'
        ok = false
      }
      if (!form.username || form.username.trim().length < 4) {
        errors.username = 'Username phải từ 4 ký tự'
        ok = false
      }
      if (props.mode === 'create' && (!form.passwords || form.passwords.length < 6)) {
        errors.passwords = 'Mật khẩu phải từ 6 ký tự'
        ok = false
      }
      if (!form.hoTen) {
        errors.hoTen = 'Họ tên không được để trống'
        ok = false
      }
      if (form.gioiTinh !== 0 && form.gioiTinh !== 1) {
        errors.gioiTinh = 'Giới tính không hợp lệ'
        ok = false
      }
      const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRe.test(form.email || '')) {
        errors.email = 'Email không hợp lệ'
        ok = false
      }
      const phoneRe = /^0\d{9}$/
      if (!phoneRe.test(form.soDienThoai || '')) {
        errors.soDienThoai = 'Số điện thoại phải gồm 10 số và bắt đầu bằng 0'
        ok = false
      }
      if (!form.diaChiGiaoHang) {
        errors.diaChiGiaoHang = 'Địa chỉ giao hàng không được để trống'
        ok = false
      }
      return ok
    }

    async function onSubmit() {
      generalError.value = ''
      successMsg.value = ''
      clearErrors()

      if (!clientValidate()) return

      submitting.value = true
      try {
        const payload = { ...form }
       
        if (props.mode === 'create') delete payload.id

        const token = localStorage.getItem('token') 
        const headers = { 'Content-Type': 'application/json' }
        if (token) headers['Authorization'] = 'Bearer ' + token

        let res
        if (props.mode === 'edit') {
          res = await axios.put(`http://localhost:8081/api/nhanvien/sua/${form.id}`, payload, { headers })
        } else {
          res = await axios.post('http://localhost:8081/api/nhanvien/them', payload, { headers })
        }

        successMsg.value = (props.mode === 'edit') ? 'Cập nhật thành công' : 'Thêm mới thành công'
       
      } catch (err) {
        
        if (err.response && err.response.status === 400) {
          const data = err.response.data
         
          if (data && typeof data === 'object') {
          
            if (data.message) {
              generalError.value = data.message
            }
        
            Object.keys(data).forEach(k => {
          
              if (k in form) {
                errors[k] = data[k]
              }
            })
          } else {
            generalError.value = 'Lỗi: ' + (err.response.data || err.message)
          }
        } else {
          generalError.value = err.message || 'Lỗi không xác định'
        }
      } finally {
        submitting.value = false
      }
    }

    return {
      form,
      errors,
      generalError,
      successMsg,
      submitting,
      onSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>

.card { max-width: 900px; margin: 0 auto; }
</style>
