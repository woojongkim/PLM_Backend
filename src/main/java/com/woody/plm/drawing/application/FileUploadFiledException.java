package com.woody.plm.drawing.application;

public class FileUploadFiledException extends RuntimeException {

  public FileUploadFiledException(String msg, Exception e) {
    super(e);
  }
}
