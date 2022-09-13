package com.woody.plm.drawing.application;

public class FileUploadFailedException extends RuntimeException {

  public FileUploadFailedException(String msg, Exception e) {
    super(msg, e);
  }

  public FileUploadFailedException(String msg) {
    super(msg);
  }
}
