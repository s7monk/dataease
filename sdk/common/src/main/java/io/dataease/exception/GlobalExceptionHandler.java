package io.dataease.exception;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import io.dataease.i18n.Translator;
import io.dataease.result.ResultCode;
import io.dataease.result.ResultMessage;
import io.dataease.utils.LogUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /** -------- 参数校验异常 -------- **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMessage MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String msg = objectError.getDefaultMessage();
        msg = Translator.get(msg);
        LogUtil.error(msg);
        return new ResultMessage(ResultCode.PARAM_IS_INVALID.code(),msg);
    }

    @ExceptionHandler(DEException.class)
    public ResultMessage deExceptionHandler(DEException e) {
        LogUtil.error(e.getMessage());
        return new ResultMessage(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResultMessage handleNotPermissionException(NotPermissionException e) {
        return new ResultMessage(ResultCode.PERMISSION_NO_ACCESS.code(),"该用户无访问权限");
    }

    /*@ExceptionHandler(NotLoginException.class)
    public ResultMessage handleNotLoginException(NotLoginException e) {
        return new ResultMessage(ResultCode.USER_NOT_LOGGED_IN.code(),"该用未登录或TOKEN失效，请重新登录");
    }*/

}
