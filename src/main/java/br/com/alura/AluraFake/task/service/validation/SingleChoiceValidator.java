package br.com.alura.AluraFake.task.service.validation;


import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.TaskOptionRequest;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SingleChoiceValidator implements TaskValidator<NewSingleChoiceTaskRequest> {

    @Override
    public ResponseEntity<ErrorItemDTO> validate(NewSingleChoiceTaskRequest dto) {
        Set<String> optionTexts = dto.options().stream()
                .map(TaskOptionRequest::option)
                .collect(Collectors.toSet());
        
        if (optionTexts.size() != dto.options().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Opções devem ser únicas"));
        }

        if (optionTexts.contains(dto.statement())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Opções não podem ser iguais ao enunciado"));
        }

        long correctCount = dto.options().stream()
                .mapToLong(opt -> opt.isCorrect() ? 1 : 0)
                .sum();
        
        if (correctCount != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Deve ter exatamente uma opção correta"));
        }

        return null;
    }
}