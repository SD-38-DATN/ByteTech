import * as yup from 'yup'
import { rules } from '../rules'

export const registerSchema = yup.object({
  email: rules.email,
  username: rules.username,
  password: rules.password,
  confirmPassword: yup.string()
    .required('Vui lòng xác nhận mật khẩu')
    .oneOf([yup.ref('password')], 'Mật khẩu xác nhận không khớp'),
  hoTen: rules.fullName,
  soDienThoai: rules.phone,
  diaChiGiaoHang: rules.addressOptional,
})

export const quickRegisterSchema = yup.object({
  email: rules.email,
  password: rules.password,
  confirmPassword: yup.string()
    .required('Vui lòng xác nhận mật khẩu')
    .oneOf([yup.ref('password')], 'Mật khẩu xác nhận không khớp'),
})

export default {
  registerSchema,
  quickRegisterSchema,
}

