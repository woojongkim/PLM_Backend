package com.woody.plm.common.domain;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
public class DeletableEntity extends ModifiableEntity{
  private LocalDateTime deleteDate;

  protected void softDelete() {
    checkDeletable();
    this.deleteDate = LocalDateTime.now();
  }
  private void checkDeletable() {
    if (deleteDate != null) {
      throw new NotDeletableException();
    }
  }

  public DeletableEntity(LocalDateTime createDate, LocalDateTime modifiedDate,
      LocalDateTime deleteDate) {
    super(createDate, modifiedDate);
    this.deleteDate = deleteDate;
  }
}
