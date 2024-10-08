package br.com.mandara.core;

import java.util.Optional;

public interface IRepository<T, ID> {

    Optional<T> findEntityById(ID id);
    
}
