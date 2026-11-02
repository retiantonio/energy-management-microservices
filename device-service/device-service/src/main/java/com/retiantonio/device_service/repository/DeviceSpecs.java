package com.retiantonio.device_service.repository;

import com.retiantonio.device_service.model.Device;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DeviceSpecs {
    public static Specification<Device> filter(UUID ownerId, String name) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (ownerId != null) p.add(cb.equal(root.get("ownerUserId"), ownerId));
            if (name != null && !name.isBlank())
                p.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
