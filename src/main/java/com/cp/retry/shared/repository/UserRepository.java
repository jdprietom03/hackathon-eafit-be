package com.cp.retry.shared.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cp.retry.shared.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Query()
    public User
}
