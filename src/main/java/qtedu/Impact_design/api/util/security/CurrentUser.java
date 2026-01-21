package qtedu.Impact_design.api.util.security;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented // JavaDoc 등 문서 생성 시 이 어노테이션이 사용된 대상에 표시되도록 함
//@io.swagger.v3.oas.annotations.Parameter(hidden = true) // 스웨거에서는 가려지도록
public @interface CurrentUser {

}