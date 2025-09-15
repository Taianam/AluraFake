package br.com.alura.AluraFake.task.service.validation;

import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.TaskOptionRequest;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MultipleChoiceValidator implements TaskValidator<NewMultipleChoiceTaskRequest> {

    @Override
    public ResponseEntity<ErrorItemDTO> validate(NewMultipleChoiceTaskRequest dto) {
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
        long incorrectCount = dto.options().size() - correctCount;
        
        if (correctCount < 2 || incorrectCount < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Deve ter pelo menos 2 opções corretas e 1 incorreta"));
        }

        return null;
    }
}