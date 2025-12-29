package org.codeexectutionservice.code_exectution_service.models;


import lombok.Data;

@Data
public class SubmissionDAO {
  
  private String problemId;
  private String code;
  private String submissionId;
  private String language;
  private String userId;

}
