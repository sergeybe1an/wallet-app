package com.sbelan.walletapp.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @NonNull
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(
            o)) {
            return false;
        }
        UserDto userDto = (UserDto) o;
        return id != null && Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
