package com.xiaoyang.diary.framework.web.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.xiaoyang.diary.framework.common.exception.ServiceException;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;
import static com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants.METHOD_NOT_ALLOWED;
import static com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException e) {
            return missingServletRequestParameterExceptionHandler(e);
        }
        if (ex instanceof MethodArgumentTypeMismatchException e) {
            return methodArgumentTypeMismatchExceptionHandler(e);
        }
        if (ex instanceof MethodArgumentNotValidException e) {
            return methodArgumentNotValidExceptionExceptionHandler(e);
        }
        if (ex instanceof BindException e) {
            return bindExceptionHandler(e);
        }
        if (ex instanceof ConstraintViolationException e) {
            return constraintViolationExceptionHandler(e);
        }
        if (ex instanceof ValidationException e) {
            return validationException(e);
        }
        if (ex instanceof MaxUploadSizeExceededException e) {
            return maxUploadSizeExceededExceptionHandler(e);
        }
        if (ex instanceof NoHandlerFoundException e) {
            return noHandlerFoundExceptionHandler(e);
        }
        if (ex instanceof NoResourceFoundException e) {
            return noResourceFoundExceptionHandler(request, e);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException e) {
            return httpRequestMethodNotSupportedExceptionHandler(e);
        }
        if (ex instanceof HttpMediaTypeNotSupportedException e) {
            return httpMediaTypeNotSupportedExceptionHandler(e);
        }
        if (ex instanceof ServiceException e) {
            return serviceExceptionHandler(e);
        }
        if (ex instanceof AccessDeniedException e) {
            return accessDeniedExceptionHandler(request, e);
        }
        return defaultExceptionHandler(request, ex);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数缺失: %s", ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误: %s", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        String errorMessage = null;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            if (CollUtil.isNotEmpty(allErrors)) {
                errorMessage = allErrors.get(0).getDefaultMessage();
            }
        } else {
            errorMessage = fieldError.getDefaultMessage();
        }
        if (StrUtil.isEmpty(errorMessage)) {
            return CommonResult.error(BAD_REQUEST);
        }
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", errorMessage));
    }

    @ExceptionHandler(BindException.class)
    public CommonResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[bindExceptionHandler]", ex);
        FieldError fieldError = ex.getFieldError();
        if (fieldError == null) {
            return CommonResult.error(BAD_REQUEST);
        }
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", fieldError.getDefaultMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<?> methodArgumentTypeInvalidFormatExceptionHandler(HttpMessageNotReadableException ex) {
        log.warn("[methodArgumentTypeInvalidFormatExceptionHandler]", ex);
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误: %s", invalidFormatException.getValue()));
        }
        if (StrUtil.startWith(ex.getMessage(), "Required request body is missing")) {
            return CommonResult.error(BAD_REQUEST.getCode(), "请求参数错误: request body 缺失");
        }
        return CommonResult.error(BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确: %s", constraintViolation.getMessage()));
    }

    @ExceptionHandler(value = ValidationException.class)
    public CommonResult<?> validationException(ValidationException ex) {
        log.warn("[validationException]", ex);
        return CommonResult.error(BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonResult<?> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex) {
        log.warn("[maxUploadSizeExceededExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), "上传文件过大，请调整后重试");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在: %s", ex.getRequestURL()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private CommonResult<?> noResourceFoundExceptionHandler(HttpServletRequest req, NoResourceFoundException ex) {
        log.warn("[noResourceFoundExceptionHandler][url({})]", req.getRequestURI(), ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在: %s", ex.getResourcePath()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return CommonResult.error(METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确: %s", ex.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<?> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex) {
        log.warn("[httpMediaTypeNotSupportedExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求类型不正确: %s", ex.getMessage()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) url({})]", WebFrameworkUtils.getLoginUserId(req), req.getRequestURL(), ex);
        return CommonResult.error(FORBIDDEN);
    }

    @ExceptionHandler(value = UncheckedExecutionException.class)
    public CommonResult<?> uncheckedExecutionExceptionHandler(HttpServletRequest req, UncheckedExecutionException ex) {
        return allExceptionHandler(req, ex.getCause());
    }

    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        if (ex.getCause() instanceof ServiceException serviceException) {
            return serviceExceptionHandler(serviceException);
        }
        log.error("[defaultExceptionHandler][url({})]", req.getRequestURI(), ex);
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

}
