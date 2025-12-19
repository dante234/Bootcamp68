package com.bankx.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception que se lanza cuando ocurre un error en la negocio.
 * Se utiliza para indicar que la operación no pudo ser realizada.
 *
 * @author Nombre Apellido
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}