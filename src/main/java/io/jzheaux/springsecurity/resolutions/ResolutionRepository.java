package io.jzheaux.springsecurity.resolutions;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResolutionRepository extends CrudRepository<Resolution, UUID> {
	List<Resolution> findByOwner(UUID owner);

	@Modifying
	@Query("UPDATE Resolution SET text = :text WHERE id = :id")
	void revise(UUID id, String text);

	@Modifying
	@Query("UPDATE Resolution SET completed = 1 WHERE id = :id")
	void complete(UUID id);
}
