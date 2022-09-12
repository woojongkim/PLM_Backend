package com.woody.plm.drawing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingDto {


  @JsonProperty("drawingId")
  private Long id;

  private String drawingNo;

  private String drawingName;

//  private Long revision;

  private String drafter;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdDate;

  @QueryProjection
  public DrawingDto(Long id, String drawingNo, String drawingName, String drafter,
      LocalDateTime createdDate) {
    this.id = id;
    this.drawingNo = drawingNo;
    this.drawingName = drawingName;
//    this.revision = revision;
    this.drafter = drafter;
    this.createdDate = createdDate;
  }
}
