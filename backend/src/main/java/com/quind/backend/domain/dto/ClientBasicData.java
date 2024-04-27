package com.quind.backend.domain.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientBasicData {
  
  @Size(min = 2, message = "should containt at least 2 characters")
  private String firstName;
  @Size(min = 2, message = "should containt at least 2 characters")
  private String lastName;

  @Past
  private Date birthDate;
  @Email
  private String email;

}
