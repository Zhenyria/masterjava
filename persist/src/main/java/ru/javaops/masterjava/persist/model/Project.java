package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseEntity {

    @Column("name_val")
    @NonNull
    private String name;

    @NonNull
    private String description;

    public Project(Integer id, @NonNull String name, @NonNull String description) {
        this(name, description);
        this.id = id;
    }
}
