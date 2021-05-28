package com.maviance.just_pay_here.repositories;

import com.maviance.just_pay_here.model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RequestRepo extends CrudRepository<Request, UUID> {
    Request findBySourcePTN(String sourcePTN);
}
