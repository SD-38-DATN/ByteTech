import * as yup from 'yup'

// File này để dùng làm rule string là tiếng việt nhé , cần custom gì viết vòa filenayf rồi lấy ra 

yup.setLocale({
  mixed: {
    default: 'Giá trị không hợp lệ',
    required: 'Trường này là bắt buộc',
  },
  string: {
    email: 'Email không hợp lệ',
    min: 'Phải có ít nhất ${min} ký tự',
    max: 'Không được quá ${max} ký tự',
  },
  number: {
    min: 'Phải lớn hơn hoặc bằng ${min}',
    max: 'Phải nhỏ hơn hoặc bằng ${max}',
  },
})

export const rules = {
  // Email
  email: yup.string()
    .required('Vui lòng nhập email')
    .email('Email không hợp lệ'),
  
  emailOptional: yup.string()
    .email('Email không hợp lệ'),
  
  // Username
  username: yup.string()
    .required('Vui lòng nhập tên đăng nhập')
    .min(3, 'Tên đăng nhập phải có ít nhất 3 ký tự')
    .max(50, 'Tên đăng nhập không được quá 50 ký tự'),
  
  // Password
  password: yup.string()
    .required('Vui lòng nhập mật khẩu')
    .min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
  
  passwordStrong: yup.string()
    .required('Vui lòng nhập mật khẩu')
    .min(8, 'Mật khẩu phải có ít nhất 8 ký tự')
    .matches(/[a-z]/, 'Mật khẩu phải có ít nhất 1 chữ thường')
    .matches(/[A-Z]/, 'Mật khẩu phải có ít nhất 1 chữ hoa')
    .matches(/[0-9]/, 'Mật khẩu phải có ít nhất 1 số'),
  
  // Phone
  phone: yup.string()
    .required('Vui lòng nhập số điện thoại')
    .matches(/^(0|\+84)[0-9]{9}$/, 'Số điện thoại không hợp lệ'),
  
  phoneOptional: yup.string()
    .matches(/^(0|\+84)[0-9]{9}$/, 'Số điện thoại không hợp lệ'),
  
  // Name
  fullName: yup.string()
    .required('Vui lòng nhập họ tên')
    .min(2, 'Họ tên phải có ít nhất 2 ký tự')
    .max(100, 'Họ tên không được quá 100 ký tự'),
  
  // Address
  address: yup.string()
    .required('Vui lòng nhập địa chỉ')
    .min(5, 'Địa chỉ phải có ít nhất 5 ký tự'),
  
  addressOptional: yup.string()
    .min(5, 'Địa chỉ phải có ít nhất 5 ký tự'),
  
  // Number
  positiveNumber: yup.number()
    .required('Trường này là bắt buộc')
    .positive('Phải là số dương')
    .integer('Phải là số nguyên'),
  
  price: yup.number()
    .required('Vui lòng nhập giá')
    .positive('Giá phải là số dương')
    .min(0, 'Giá không được âm'),
  
  // String
  requiredString: yup.string()
    .required('Trường này là bắt buộc'),
}

export default rules

