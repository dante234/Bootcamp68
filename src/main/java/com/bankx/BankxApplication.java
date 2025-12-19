package com.bankx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la aplicación BankX.
 *
 * <p>Esta clase inicializa el contexto de Spring Boot y
 * lanza la aplicación.</p>
 */
@SpringBootApplication
public class BankxApplication {

  /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos
  */
  public static void main(String[] args) {
    SpringApplication.run(BankxApplication.class, args);
  }
}
