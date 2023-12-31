package KlajdiNdoci.Capstone.controllers;

import KlajdiNdoci.Capstone.entities.Game;
import KlajdiNdoci.Capstone.entities.User;
import KlajdiNdoci.Capstone.exceptions.BadRequestException;
import KlajdiNdoci.Capstone.payloads.NewUserDTO;
import KlajdiNdoci.Capstone.payloads.UpdateUserDTO;
import KlajdiNdoci.Capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "createdAt") String orderBy,
                              @RequestParam(defaultValue = "desc") String direction) {
        if (!direction.equalsIgnoreCase("desc") && !direction.equalsIgnoreCase("asc")) {
            throw new IllegalArgumentException("The direction has to be 'asc' or 'desc'!");
        }
        return userService.getUsers(page, size > 20 ? 5 : size, orderBy, direction);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public User findById(@PathVariable UUID id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID id) {
        userService.findByIdAndDelete(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findByIdAndUpdate(@PathVariable UUID id, @RequestBody @Validated UpdateUserDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException(validation.getAllErrors());
        } else {
            return userService.findByIdAndUpdate(id, body);
        }
    }


    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public UserDetails getProfile(@AuthenticationPrincipal UserDetails currentUser) {
        return currentUser;
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public UserDetails updateProfile(@AuthenticationPrincipal User currentUser, @RequestBody @Validated UpdateUserDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException(validation.getAllErrors());
        } else {
            return userService.findByIdAndUpdate(currentUser.getId(), body);
        }
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@AuthenticationPrincipal User currentUser) {
        userService.findByIdAndDelete(currentUser.getId());
    }

    @PutMapping("/{id}/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User upload(@RequestParam("avatar") MultipartFile body, @PathVariable UUID id) throws IOException {
        try {
            return userService.uploadImg(body, id);
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/me/upload")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public UserDetails uploadOnProfile(@AuthenticationPrincipal User currentUser,  @RequestParam("avatar") MultipartFile body) throws IOException {
        try {
            return userService.uploadImg(body, currentUser.getId());
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
    @PostMapping("/{gameId}/games")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public User addOrRemoveGame(@PathVariable UUID gameId, @AuthenticationPrincipal User currentUser) {
        return userService.addOrRemoveGame(gameId, currentUser.getId());
    }
    @PostMapping("/friends/{friendId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public User addOrRemoveFriend(@PathVariable UUID friendId, @AuthenticationPrincipal User currentUser) {
        return userService.addOrRemoveFriend(friendId, currentUser.getId());
    }

    @GetMapping("/{userId}/friends")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public Page<User> getUserFriends(@PathVariable UUID userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "createdAt") String orderBy,
                                     @RequestParam(defaultValue = "desc") String direction) {
        if (!direction.equalsIgnoreCase("desc") && !direction.equalsIgnoreCase("asc")) {
            throw new IllegalArgumentException("The direction has to be 'asc' or 'desc'!");
        }
        return userService.getUserFriends(userId, page, size, orderBy, direction);
    }
}
