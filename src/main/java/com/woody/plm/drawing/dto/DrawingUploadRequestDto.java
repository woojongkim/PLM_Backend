package com.woody.plm.drawing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrawingUploadRequestDto {

  private String drawingNo;

  private String drawingName;

  private String drafter;

  private String comment;
}
