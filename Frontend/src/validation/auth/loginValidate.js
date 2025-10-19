import * as yup from 'yup'
import { rules } from '../rules'

export const loginUserSchema = yup.object({
  username: yup.string()
    .required('Vui lòng nhập Email/Số điện thoại/Tên đăng nhập')
    .min(3, 'Phải có ít nhất 3 ký tự'),
  passwords: rules.password,
})

export const loginSellerSchema = yup.object({
  username: yup.string()
    .required('Vui lòng nhập Email/Tên đăng nhập')
    .min(3, 'Phải có ít nhất 3 ký tự'),
  passwords: rules.password,
})

export const validateLoginForm = (values) => {
  const errors = {}
  
  if (!values.username || values.username.trim() === '') {
    errors.username = 'Vui lòng nhập tên đăng nhập'
  }
  
  if (!values.passwords || values.passwords.trim() === '') {
    errors.passwords = 'Vui lòng nhập mật khẩu'
  } else if (values.passwords.length < 6) {
    errors.passwords = 'Mật khẩu phải có ít nhất 6 ký tự'
  }
  
  return errors
}

export default {
  loginUserSchema,
  loginSellerSchema,
  validateLoginForm,
}

