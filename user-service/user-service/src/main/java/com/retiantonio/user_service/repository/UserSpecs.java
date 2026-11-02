package com.retiantonio.user_service.repository;

import com.retiantonio.user_service.model.UserAbstract;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserSpecs {
    public static Specification<UserAbstract> filter(UUID id, String email, String name) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (id != null) p.add(cb.equal(root.get("uuid"), id));
            if (has(email))    p.add(cb.like(cb.lower(root.get("email")), like(email)));
            if (has(name))     p.add(cb.like(cb.lower(root.get("name")), like(name)));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
    private static boolean has(String s) { return s != null && !s.isBlank(); }
    private static String like(String s) { return "%" + s.toLowerCase() + "%"; }
}