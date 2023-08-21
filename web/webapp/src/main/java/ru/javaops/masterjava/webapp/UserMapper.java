package ru.javaops.masterjava.webapp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.javaops.masterjava.persist.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static List<UserDto> mapToDto(Collection<User> users) {
        return users.stream()
                    .map(UserMapper::mapToDto)
                    .collect(Collectors.toList());
    }

    public static UserDto mapToDto(User user) {
        return new UserDto(user.getFullName(), user.getEmail(), user.getFlag(), user.getCityRef(), false);
    }

}
