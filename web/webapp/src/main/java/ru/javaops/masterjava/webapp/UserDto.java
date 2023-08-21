package ru.javaops.masterjava.webapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.javaops.masterjava.persist.model.type.UserFlag;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private String fullName;
    private String email;
    private UserFlag flag;
    private String cityRef;
    private Boolean isSelected;
}
