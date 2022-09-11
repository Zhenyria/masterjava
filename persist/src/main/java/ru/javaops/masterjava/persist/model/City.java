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

    @Column("name_val")
    @NonNull
    private String name;

    public City(Integer id, @NonNull String name) {
        this(name);
        this.id = id;
    }
}
