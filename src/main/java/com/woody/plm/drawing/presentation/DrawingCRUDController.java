package com.woody.plm.drawing.presentation;

import com.woody.plm.drawing.application.DrawingService;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;
import com.woody.plm.drawing.dto.DrawingUploadRequestDto;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class DrawingCRUDController {

  private final DrawingService drawingService;

  public DrawingCRUDController(DrawingService drawingService) {
    this.drawingService = drawingService;
  }

  @GetMapping("/drawing")
  public ResponseEntity findAllByQuery( DrawingSearchRequestDto query){
    log.info(query.toString());
    return ResponseEntity.ok(drawingService.findAllByQuery(query));
  }

  @GetMapping("/drawing/list/{drawingId}")
  public ResponseEntity listRevisions(@PathVariable("drawingId") Long drawingId){
    log.info(drawingId.toString());
    return ResponseEntity.ok(drawingService.listRevisions(drawingId));
  }

  @PostMapping(value="/uploadFiles", consumes = {"multipart/form-data"})
  public ResponseEntity uploadFiles(@RequestPart("file") MultipartFile[] files) {
    return ResponseEntity.ok(drawingService.uploadFiles(files));
  }

  @PostMapping(value = "/uploadFile",consumes = {"multipart/form-data"})
  public ResponseEntity uploadFile(@RequestPart("file") MultipartFile files, @RequestPart("data")
      DrawingUploadRequestDto data) {
    return ResponseEntity.ok(drawingService.uploadFile(files, data));
  }

  @GetMapping("/downloadFile/{drawingId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("drawingId") Long drawingId, HttpServletRequest request) {

    log.info("downloadFile : " + drawingId);
    Resource resource = null;
      try {
        resource = drawingService.loadFile(drawingId, true);
      } catch (Exception e) {
        e.printStackTrace();
      }
      // Try to determine file's content type
      String contentType = null;
      try {
        contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
      } catch (IOException ex) {
        //logger.info("Could not determine file type.");
      }
      // Fallback to the default content type if type could not be determined
      if(contentType == null) {
        contentType = "application/octet-stream";
      }
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(
              HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);
  }

  @GetMapping("/checkout/{drawingId}")
  public ResponseEntity<Resource> checkout(@PathVariable("drawingId") Long drawingId, HttpServletRequest request) {

    log.info("downloadFile : " + drawingId);
    Resource resource = null;
    try {
      resource = drawingService.loadFile(drawingId, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Try to determine file's content type
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      //logger.info("Could not determine file type.");
    }
    // Fallback to the default content type if type could not be determined
    if(contentType == null) {
      contentType = "application/octet-stream";
    }
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }
}
