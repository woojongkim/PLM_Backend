package com.woody.plm.drawing.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawingRepository extends JpaRepository<Drawing, Long>, DrawingCustomRepository,
    QuerydslPredicateExecutor<Drawing>, RevisionRepository<Drawing, Long, Integer> {

  @Query("select d from Drawing d " +
      "where upper(d.drawingNo) like upper(?1) and upper(d.drawingName) like upper(?2) and upper(d.drafter) like upper(?3) and d.modifiedDate < ?4 and d.modifiedDate > ?5")
  Optional<List<Drawing>> findByObject(
      @Nullable String drawingNo, @Nullable String drawingName, @Nullable String drafter,
      LocalDateTime startDate, LocalDateTime endDate);

  Drawing findByDrawingNo(String drawingNo);
}
