package com.woody.plm.drawing.domain;

import com.woody.plm.drawing.dto.DrawingDto;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;
import java.util.List;

public interface DrawingCustomRepository {
  public List<DrawingDto> findAllByObject(DrawingSearchRequestDto query);
}
