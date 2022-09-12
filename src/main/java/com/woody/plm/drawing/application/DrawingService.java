package com.woody.plm.drawing.application;

import com.woody.plm.drawing.domain.Drawing;
import com.woody.plm.drawing.domain.DrawingRepository;
import com.woody.plm.drawing.dto.DrawingDto;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

  public List<DrawingDto> findAllByQuery(DrawingSearchRequestDto query){
    return drawingRepository.findAllByObject(query);
  }

  @Transactional
  public List<Drawing> uploadFiles(MultipartFile[] files) {
    List<Drawing> result = new ArrayList<>();

    for(var file : files){
      result.add(uploadFile(file));
    }

    return result;
  }

  @Transactional
  public Drawing uploadFile(MultipartFile file) {
    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

    String fileName;

    try {
      if (originalFileName.contains("..")) {
        throw new Exception("파일명에 부적합 문자가 포함되어 있습니다.");
      }

      String fileExtension;

      try {
        fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
      } catch (Exception e) {
        fileExtension = "";
      }

      ;

      // string base64 encode
      fileName = java.util.Base64.getEncoder().encodeToString(String.format("%s%s", originalFileName, (new Date()).toString()).getBytes()) + fileExtension;

      Drawing drawing = Drawing.builder()
          .drawingName(originalFileName)
          .fileName(fileName)
          .drafter("woody")
          .drawingNo(String.valueOf(System.currentTimeMillis() % 100000))
          .build();

      drawingRepository.save(drawing);

      Path targetLocation = this.fileStorageLocation.resolve(fileName);

      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return drawing;
    } catch (Exception e) {
      throw new FileUploadFiledException("upload failed", e);
    }
  }

  public Resource loadFileAsResource(Long no) throws Exception {

    Optional<Drawing> byId = drawingRepository.findById(no);
    Drawing drawing = byId.get();

    String fileName = drawing.getFileName();
    Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
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


}
