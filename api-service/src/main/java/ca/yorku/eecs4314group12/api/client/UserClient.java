// package ca.yorku.eecs4314group12.api.client;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.client.WebClient;

// import ca.yorku.eecs4314group12.api.dto.userServiceDTO.LoginRequest;
// import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserRegisterRequest;
// import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserResponseDTO;
// import reactor.core.publisher.Mono;

// import java.util.List;

// @Component
// public class UserClient {

//     private final BaseWebClient baseWebClient;

//     public UserClient(@Qualifier("APIUserClient") WebClient webClient) {
//         this.baseWebClient = new BaseWebClient(webClient);
//     }

//     public Mono<UserResponseDTO> register(UserRegisterRequest request) {
//         return baseWebClient.post("/user/register", request,  new ParameterizedTypeReference<UserResponseDTO>() {});
//     }

//     public Mono<UserResponseDTO> login(LoginRequest request) {
//         return baseWebClient.post("/user/login", request,  new ParameterizedTypeReference<UserResponseDTO>() {});
//     }

//     public Mono<String> verifyEmail(Long id, String code) {
//         return baseWebClient.post("/user/{id}/verify?code={code}", null,  new ParameterizedTypeReference<String>() {}, id, code);
//     }

//     public Mono<List<UserResponseDTO>> getUsers() {
//         return baseWebClient.get("/user", new ParameterizedTypeReference<List<UserResponseDTO>>() {});
//     }
    
//     public Mono<UserResponseDTO> getUserById(Long id) {
//         return baseWebClient.get("/user/{id}", new ParameterizedTypeReference<UserResponseDTO>() {}, id);
//     }

//     // need a user DTO
//     // public Mono<UserResponseDTO> updateUser(Long id, User user) {
//     //     return baseWebClient.put("/user/{id}", user, new ParameterizedTypeReference<UserResponseDTO>() {}, id);
//     // }

//     // delete should return some message
//     // public void deleteUser(Long id) {
//     //     baseWebClient.delete("/user/{id}", null, id);
//     // }

// }
