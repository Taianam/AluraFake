package br.com.alura.AluraFake.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.user.controller.UserController;
import br.com.alura.AluraFake.user.dto.NewUserRequest;
import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("Caio Bugorin", "", Role.STUDENT, null);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("Caio Bugorin", "caio", Role.STUDENT, null);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newUser__should_return_bad_request_when_email_already_exists() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("Caio Bugorin", "caio.bugorin@alura.com.br", Role.STUDENT, null);

        when(userRepository.existsByEmail(newUserRequest.email())).thenReturn(true);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("email"))
                .andExpect(jsonPath("$.message").value("Email já cadastrado no sistema"));
    }

    @Test
    void newUser__should_return_created_when_user_request_is_valid() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("Caio Bugorin", "caio.bugorin@alura.com.br", Role.STUDENT, null);
        User savedUser = new User("Caio Bugorin", "caio.bugorin@alura.com.br", Role.STUDENT);

        when(userRepository.existsByEmail(newUserRequest.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void listAllUsers__should_list_all_users() throws Exception {
        User user1 = new User("User 1", "user1@test.com", Role.STUDENT);
        User user2 = new User("User 2", "user2@test.com", Role.STUDENT);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

}