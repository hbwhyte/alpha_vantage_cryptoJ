package alpha_vantage.repository;

import alpha_vantage.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
}
