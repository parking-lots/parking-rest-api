package parking.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
        public Role findByName(String name);
}
