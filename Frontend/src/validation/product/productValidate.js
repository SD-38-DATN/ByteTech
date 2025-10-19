import * as yup from 'yup'
import { rules } from '../rules'

export const productSchema = yup.object({
  tenSanPham: yup.string()
    .required('Vui lòng nhập tên sản phẩm')
    .min(3, 'Tên sản phẩm phải có ít nhất 3 ký tự')
    .max(200, 'Tên sản phẩm không được quá 200 ký tự'),
  gia: rules.price,
  soLuong: rules.positiveNumber,
  moTa: yup.string()
    .max(5000, 'Mô tả không được quá 5000 ký tự'),
  maDanhMuc: yup.number()
    .required('Vui lòng chọn danh mục'),
})

export const feedbackSchema = yup.object({
  rating: yup.number()
    .required('Vui lòng chọn số sao')
    .min(1, 'Số sao tối thiểu là 1')
    .max(5, 'Số sao tối đa là 5'),
  noiDung: yup.string()
    .required('Vui lòng nhập nội dung đánh giá')
    .min(10, 'Nội dung đánh giá phải có ít nhất 10 ký tự')
    .max(1000, 'Nội dung đánh giá không được quá 1000 ký tự'),
})

export const orderSchema = yup.object({
  hoTen: rules.fullName,
  soDienThoai: rules.phone,
  diaChiGiaoHang: rules.address,
  ghiChu: yup.string()
    .max(500, 'Ghi chú không được quá 500 ký tự'),
})

export default {
  productSchema,
  feedbackSchema,
  orderSchema,
}

