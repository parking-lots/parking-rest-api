package parking.beans.document;

import org.bson.types.ObjectId;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Document(collection = "car")
    public class Car {

        @Id
        private ObjectId id;
        @Size(min = 6, max = 15)
        @Pattern(regexp="^[A-Za-z0-9]*$")
        private String regNo;
        private String model;

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
