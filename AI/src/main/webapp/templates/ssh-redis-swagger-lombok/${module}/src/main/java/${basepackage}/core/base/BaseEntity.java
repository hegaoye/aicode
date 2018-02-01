package ${basePackage}.core.base;

import lombok.Data;

@Data
public class BaseEntity implements java.io.Serializable {
    protected Long id;

    public void setId(Long value) {
        this.id = value;
    }
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

}
