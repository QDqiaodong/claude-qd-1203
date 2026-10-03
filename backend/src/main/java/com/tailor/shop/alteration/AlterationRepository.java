package com.tailor.shop.alteration;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlterationRepository extends JpaRepository<Alteration, Long> {

    List<Alteration> findByGarmentId(Long garmentId);
}
