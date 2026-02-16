package com.realestate.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.realestate.ai.model.Project;

public interface ProjectRepository
extends JpaRepository<Project,Long>{

@Query("""
SELECT p FROM Project p
WHERE p.published = true

AND (
:city IS NULL
OR LOWER(p.city)
LIKE LOWER(CONCAT('%',:city,'%'))
)

AND (
:bhk IS NULL
OR LOWER(p.bhk)
LIKE LOWER(CONCAT('%',:bhk,'%'))
)

AND (
:type IS NULL
OR LOWER(p.type)
LIKE LOWER(CONCAT('%',:type,'%'))
)

AND (
:gated IS NULL
OR p.gatedCommunity = :gated
)

AND (
:budget IS NULL
OR p.priceStart <= :budget
)

ORDER BY p.priceStart ASC
""")
List<Project> progressiveMatch(

@Param("city") String city,
@Param("bhk") String bhk,
@Param("type") String type,
@Param("gated") Boolean gated,
@Param("budget") Long budget

);

}
