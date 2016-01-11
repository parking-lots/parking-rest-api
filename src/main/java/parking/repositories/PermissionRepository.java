package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Permission;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    public Permission findByName(String name);
}
