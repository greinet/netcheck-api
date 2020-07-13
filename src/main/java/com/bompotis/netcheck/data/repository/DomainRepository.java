package com.bompotis.netcheck.data.repository;

import com.bompotis.netcheck.data.entity.DomainEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Kyriakos Bompotis on 8/6/20.
 */
@Repository
public interface DomainRepository extends JpaRepository<DomainEntity, String> {
    Page<DomainEntity> findAllByCheckFrequency(int frequency, Pageable pageable);
}
