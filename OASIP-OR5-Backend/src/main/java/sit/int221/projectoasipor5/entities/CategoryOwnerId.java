package sit.int221.projectoasipor5.entities;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CategoryOwnerId implements Serializable {
    private static final long serialVersionUID = 5551499355171185881L;
    @Column(name = "userId", nullable = false)
    private Integer userId;
    @Column(name = "eventCategoryId", nullable = false)
    private Integer eventCategoryId;

    public Integer getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(Integer eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventCategoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategoryOwnerId entity = (CategoryOwnerId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.eventCategoryId, entity.eventCategoryId);
    }
}