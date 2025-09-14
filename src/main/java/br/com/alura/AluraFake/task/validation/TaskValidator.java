package br.com.alura.AluraFake.task.validation;

import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.ResponseEntity;

public interface TaskValidator<T> {
    ResponseEntity<ErrorItemDTO> validate(T dto);
}