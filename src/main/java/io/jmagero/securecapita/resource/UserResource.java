package io.jmagero.securecapita.resource;

import io.jmagero.securecapita.domain.HttpResponse;
import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import io.jmagero.securecapita.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserServiceImpl userService;

    @PostMapping()
    public ResponseEntity<HttpResponse> addUser(@RequestBody @Valid User user){
       UserDTO userDTO = userService.createUser(user);
       return  ResponseEntity.created(getUri()).body(
               HttpResponse.builder()
                       .timeStamp(now().toString())
                       .data(Map.of("user",userDTO))
                       .message("User created")
                       .status(HttpStatus.CREATED)
                       .statusCode(HttpStatus.CREATED.value())
                       .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/get/<userId>").toUriString());
    }
}