package ch.hearc.cafheg.business.allocations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CantonTest {

  @Test
  @DisplayName("fromValue given NE should be NE")
  void fromValue_GivenFR_ShouldBeFR() {
    assertThat(Canton.fromValue("FR")).isEqualTo(Canton.FR);
  }

  @Test
  @DisplayName("fromValue given MM should be null")
  void fromValue_GivenMM_ShouldBeNull() {
    assertThat(Canton.fromValue("MM")).isNull();
  }

}