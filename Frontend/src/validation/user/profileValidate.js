import * as yup from 'yup'
import { rules } from '../rules'

export const profileSchema = yup.object({
  hoTen: rules.fullName,
  email: rules.email,
  soDienThoai: rules.phoneOptional,
  diaChiGiaoHang: rules.addressOptional,
})

export const changePasswordSchema = yup.object({
  currentPassword: yup.string()
    .required('Vui lòng nhập mật khẩu hiện tại'),
  newPassword: rules.password,
  confirmPassword: yup.string()
    .required('Vui lòng xác nhận mật khẩu mới')
    .oneOf([yup.ref('newPassword')], 'Mật khẩu xác nhận không khớp'),
})

export default {
  profileSchema,
  changePasswordSchema,
}

