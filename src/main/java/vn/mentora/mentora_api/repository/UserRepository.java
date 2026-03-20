package vn.mentora.mentora_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByRefreshTokenAndEmail(String refreshToken, String email);

    @Query("SELECT DISTINCT u.country FROM User u WHERE u.country IS NOT NULL")
    List<String> findDistinctCountries();

}
