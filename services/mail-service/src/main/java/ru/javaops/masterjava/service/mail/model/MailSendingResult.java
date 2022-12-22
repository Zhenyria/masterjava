package ru.javaops.masterjava.service.mail.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.javaops.masterjava.persist.model.BaseEntity;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailSendingResult extends BaseEntity {
    private String email;
    @Column("name_val")
    private String name;
    @Column("message_title")
    private String messageTitle;
    @Column("message_body")
    private String messageBody;
    private MailSendingStatus status;
    private String description;
    @Column("create_time")
    private LocalDateTime createTime;
}
