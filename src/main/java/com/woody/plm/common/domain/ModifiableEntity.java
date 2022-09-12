package com.woody.plm.common.domain;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public class ModifiableEntity {

  @CreatedDate
  private LocalDateTime createDate;

  @LastModifiedDate
  private LocalDateTime modifiedDate;

  public ModifiableEntity(LocalDateTime createDate, LocalDateTime modifiedDate) {
    this.createDate = createDate;
    this.modifiedDate = modifiedDate;
  }
}
