package ru.javaops.masterjava.webapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SendEmailsDto {
    private List<UserEmailDto> users;
    private String subject;
    private String body;
}
