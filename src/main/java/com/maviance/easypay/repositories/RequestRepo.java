package com.maviance.easypay.repositories;

import com.maviance.easypay.model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RequestRepo extends CrudRepository<Request, UUID> {
    Request findBySourcePTN(String sourcePTN);
}
