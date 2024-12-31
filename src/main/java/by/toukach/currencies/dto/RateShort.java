package by.toukach.currencies.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateShort {

  @JsonProperty("Cur_Name")
  private String name;

  @JsonProperty("Cur_ID")
  private int id;

  @JsonProperty("Cur_Abbreviation")
  private String abbreviation;
}
