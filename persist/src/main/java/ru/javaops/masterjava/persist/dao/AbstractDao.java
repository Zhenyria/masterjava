package ru.javaops.masterjava.persist.dao;

public interface AbstractDao {

    int nextVal();

    void clean();

    void prepareSequenceToBatchInsert();

    void returnSequenceToInitialCondition();
}
