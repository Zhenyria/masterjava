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
public class Group extends BaseEntity {

    @Column("project_id")
    @NonNull
    private Integer projectId;

    @Column("name_val")
    @NonNull
    private String name;

    @Column("type_val")
    @NonNull
    private GroupType type;

    public Group(Integer id, Integer projectId, String name, GroupType type) {
        this(projectId, name, type);
        this.id = id;
    }
}
