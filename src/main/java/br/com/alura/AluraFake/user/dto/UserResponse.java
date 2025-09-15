package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role
) {
    public UserResponse(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}