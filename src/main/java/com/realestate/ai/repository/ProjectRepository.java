package com.realestate.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.realestate.ai.model.Project;

public interface ProjectRepository
extends JpaRepository<Project,Long>{

// ================= VOICE AI PROGRESSIVE SEARCH =================
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
OR CAST(REPLACE(p.priceStart, ',', '') AS long) <= :budget
)

ORDER BY
CAST(REPLACE(p.priceStart, ',', '') AS long) ASC
""")
List<Project> progressiveMatch(

@Param("city") String city,
@Param("bhk") String bhk,
@Param("type") String type,
@Param("gated") Boolean gated,
@Param("budget") Long budget

);


// ================= GET ALL LOCATIONS =================
@Query("""
SELECT DISTINCT p.location FROM Project p
WHERE p.published=true
""")
List<String> findAllLocations();


// ================= GET ALL CITIES =================
@Query("""
SELECT DISTINCT p.city FROM Project p
WHERE p.published=true
""")
List<String> findAllCities();


}
