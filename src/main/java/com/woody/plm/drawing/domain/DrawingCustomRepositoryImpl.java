package com.woody.plm.drawing.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woody.plm.drawing.dto.DrawingDto;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;

import com.woody.plm.drawing.dto.QDrawingDto;
import java.time.ZoneId;
import java.util.List;
import org.hibernate.criterion.Projection;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.woody.plm.drawing.domain.QDrawing.drawing;

@Repository
public class DrawingCustomRepositoryImpl extends QuerydslRepositorySupport implements DrawingCustomRepository{

  private final JPAQueryFactory jpaQueryFactory;

  public DrawingCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    super(Drawing.class);
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<DrawingDto> findAllByObject(DrawingSearchRequestDto query) {
    List<DrawingDto> fetch = jpaQueryFactory.select(
            new QDrawingDto(drawing.id, drawing.drawingNo, drawing.drawingName, drawing.drafter,
                drawing.createdDate))
        .from(drawing)
        .where(
            StringUtils.hasText(query.getDrawingNo()) ? drawing.drawingNo.contains(
                query.getDrawingNo()) : null,
            StringUtils.hasText(query.getDrawingName()) ? drawing.drawingName.contains(
                query.getDrawingName()) : null,
            StringUtils.hasText(query.getDrafter()) ? drawing.drafter.contains(query.getDrafter())
                : null,
            query.getStartDate() != null ? drawing.createdDate.after(query.getStartDate().toInstant().atZone(
                ZoneId.systemDefault()).toLocalDateTime()) : null,
            query.getEndDate() != null ? drawing.createdDate.before(query.getEndDate().toInstant().atZone(
                ZoneId.systemDefault()).toLocalDateTime()) : null
        ).fetch();
    return fetch;
  }
}
