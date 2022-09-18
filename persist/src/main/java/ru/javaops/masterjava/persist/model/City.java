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
public class City extends BaseEntity {

    @NonNull
    private String code;

    @Column("name_val")
    @NonNull
    private String name;

    public City(Integer id, @NonNull String code, @NonNull String name) {
        this(code, name);
        this.id = id;
    }
}
