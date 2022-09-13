package com.woody.plm.drawing.application;

import com.woody.plm.drawing.domain.Drawing;
import com.woody.plm.drawing.domain.DrawingRepository;
import com.woody.plm.drawing.dto.DrawingDto;
import com.woody.plm.drawing.dto.DrawingRevisionResponseDto;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;
import com.woody.plm.drawing.dto.DrawingUploadRequestDto;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DrawingService {

  private final DrawingRepository drawingRepository;

  private final Path fileStorageLocation;

  @Autowired
  public DrawingService(DrawingRepository drawingRepository) {
    this.drawingRepository = drawingRepository;

    this.fileStorageLocation = Paths.get("C:\\Users\\5385k\\OneDrive\\Desktop\\files")
        .toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<DrawingDto> findAllByQuery(DrawingSearchRequestDto query) {
    return drawingRepository.findAllByObject(query);
  }

  @Transactional
  public List<Drawing> uploadFiles(MultipartFile[] files) {
    List<Drawing> result = new ArrayList<>();

    for (var file : files) {
      result.add(uploadFile(file, null));
    }

    return result;
  }

  //  @Transactional
  public Drawing uploadFile(MultipartFile file, DrawingUploadRequestDto data) {

    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

    String physicalFileName;

    try {
      if (originalFileName.contains("..")) {
        throw new FileUploadFailedException("파일명에 부적합 문자가 포함되어 있습니다.");
      }

      String fileNameWithoutExtension;
      String fileExtension;

      try {
        fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
      } catch (Exception e) {
        fileNameWithoutExtension = originalFileName;
        fileExtension = "";
      }

      { // if data is null
        if (data == null) {
          data = new DrawingUploadRequestDto();
          data.setDrawingNo("" + System.currentTimeMillis() % 100000);
          data.setDrawingName(fileNameWithoutExtension);
          data.setDrafter("woody");
        }
      }

      Drawing byDrawingNo = drawingRepository.findByDrawingNo(data.getDrawingNo());
      Drawing saveDrawing;

      if (byDrawingNo == null) {
        saveDrawing = Drawing.builder()
            .drawingNo((data != null) ? data.getDrawingNo()
                : String.valueOf(System.currentTimeMillis() % 10000000))
            .drawingName((data != null) ? data.getDrawingName() : fileNameWithoutExtension)
            .fileName(originalFileName)
            .drafter((data != null) ? data.getDrafter() : "tester")
            .version(0L)
            .comment(data.getComment())
            .build();

      } else {

        Drawing lastEntity = drawingRepository.findLastChangeRevision(byDrawingNo.getId()).get()
            .getEntity();

        saveDrawing = Drawing.builder()
            .id(byDrawingNo.getId())
            .drawingNo(byDrawingNo.getDrawingNo())
            .drawingName(data.getDrawingName())
            .fileName(originalFileName)
            .drafter(data.getDrafter())
            .version(lastEntity.getVersion() + 1)
            .comment(data.getComment())
            .build();
      }

      Drawing save = drawingRepository.save(saveDrawing);

      physicalFileName = save.getId().toString()+"_"+save.getVersion();

      Path targetLocation = this.fileStorageLocation.resolve(physicalFileName);

      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return save;
    } catch (Exception e) {
      throw new FileUploadFailedException("upload failed", e);
    }
  }

  public Resource loadFile(Long no, boolean checkout) throws Exception {

    Optional<Drawing> byId = drawingRepository.findById(no);
    Drawing drawing = byId.get();

    String fileName = drawing.getFileName();
    String physicalFileName = drawing.getId().toString() + "_" + drawing.getVersion();
    Path filePath = this.fileStorageLocation.resolve(physicalFileName).normalize();
    Resource resource = null;
    try {
      resource = new UrlResource(filePath.toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (resource.exists()) {
      return resource;
    } else {
      throw new FileNotFoundException("File not found " + fileName);
    }
  }


  public List<DrawingRevisionResponseDto> listRevisions(Long drawingId) {
    Revisions<Integer, Drawing> revisions = drawingRepository.findRevisions(drawingId);

    List<DrawingRevisionResponseDto> collect = revisions.getContent().stream()
        .map(r -> DrawingRevisionResponseDto.builder()
            .id(r.getEntity().getId())
            .drawingNo(r.getEntity().getDrawingNo())
            .drawingName(r.getEntity().getDrawingName())
            .drafter(r.getEntity().getDrafter())
            .version(r.getEntity().getVersion())
            .comment(r.getEntity().getComment())
            .modifiedDate(r.getEntity().getModifiedDate())
            .build()).sorted(Comparator.comparing(DrawingRevisionResponseDto::getVersion).reversed()).collect(Collectors.toList());

    return collect;
  }
}
