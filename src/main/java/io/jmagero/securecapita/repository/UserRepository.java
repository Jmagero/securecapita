package io.jmagero.securecapita.repository;

import io.jmagero.securecapita.domain.User;

import java.util.Collection;

public interface UserRepository <T extends User> {
//    Basic operation
    T create(T data);
    Collection<T>  list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

//    More complex Operations

}