package qtedu.Impact_design.api.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.common.response.SuccessCreateResponse;
import qtedu.Impact_design.common.response.SuccessOnlyResponse;

public class ResponseHelper {

    public static <T> ResponseEntity<HttpResponse<T>> success(T data){
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.OK.value(), data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<HttpResponse<SuccessOnlyResponse>> successOnly(){
        HttpResponse<SuccessOnlyResponse> response = new HttpResponse<>(HttpStatus.OK.value(), new SuccessOnlyResponse());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<HttpResponse<SuccessCreateResponse>> successCreateOnly(){
        HttpResponse<SuccessCreateResponse> response = new HttpResponse<>(HttpStatus.CREATED.value(), new SuccessCreateResponse());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<HttpResponse<T>> successCreate(T data){
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.CREATED.value(), data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<HttpResponse<T>> error(HttpStatus status, T data){
        HttpResponse<T> response = new HttpResponse<>(status.value(), data);
        return new ResponseEntity<>(response, status);
    }


}