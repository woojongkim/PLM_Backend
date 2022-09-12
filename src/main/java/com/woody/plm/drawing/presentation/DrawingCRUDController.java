package com.woody.plm.drawing.presentation;

import com.woody.plm.drawing.application.DrawingService;
import com.woody.plm.drawing.domain.Drawing;
import com.woody.plm.drawing.dto.DrawingSearchRequestDto;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
  public ResponseEntity listDrawing( DrawingSearchRequestDto query){
    log.info(query.toString());
    return ResponseEntity.ok(drawingService.findAllByQuery(query));
  }

  @PostMapping("/uploadFile")
  public ResponseEntity uploadFile(@RequestParam("file") MultipartFile[] files) {
    return ResponseEntity.ok(drawingService.uploadFiles(files));
  }

  @GetMapping("/downloadFile/{no}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("no") Long no, HttpServletRequest request) {

    log.info("downloadFile : " + no);
    Resource resource = null;
      try {
        resource = drawingService.loadFileAsResource(no);
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
