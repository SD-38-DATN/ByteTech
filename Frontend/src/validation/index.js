export * from './rules'
export * from './auth/loginValidate'
export * from './auth/registerValidate'
export * from './user/profileValidate'
export * from './product/productValidate'

export const validateField = async (schema, fieldName, value) => {
    try {
        await schema.validateAt(fieldName, { [fieldName]: value })
        return null
    } catch (error) {
        return error.message
    }
}

export const validateForm = async (schema, values) => {
    try {
        await schema.validate(values, { abortEarly: false })
        return {}
    } catch (error) {
        const errors = {}
        error.inner.forEach((err) => {
            errors[err.path] = err.message
        })
        return errors
    }
}

export default {
    validateField,
    validateForm,
}

