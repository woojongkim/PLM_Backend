package com.woody.plm.common.domain;

import com.woody.plm.common.domain.ModifiableEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
public class FileEntity extends ModifiableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false, updatable = false)
  private Long id;

  private String fileName;

  private String filePath;

  public FileEntity(LocalDateTime createDate, LocalDateTime modifiedDate, Long id) {
    super(createDate, modifiedDate);
    this.id = id;
  }
}
