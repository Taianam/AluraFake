package br.com.alura.AluraFake.task.validation;

import br.com.alura.AluraFake.task.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.TaskOptionDTO;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SingleChoiceValidator implements TaskValidator<NewSingleChoiceTaskDTO> {

    @Override
    public ResponseEntity<ErrorItemDTO> validate(NewSingleChoiceTaskDTO dto) {
        Set<String> optionTexts = dto.getOptions().stream()
                .map(TaskOptionDTO::getOption)
                .collect(Collectors.toSet());
        
        if (optionTexts.size() != dto.getOptions().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options must be unique"));
        }

        if (optionTexts.contains(dto.getStatement())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Options cannot be equal to statement"));
        }

        long correctCount = dto.getOptions().stream()
                .mapToLong(opt -> opt.getIsCorrect() ? 1 : 0)
                .sum();
        
        if (correctCount != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("options", "Must have exactly one correct option"));
        }

        return null;
    }
}