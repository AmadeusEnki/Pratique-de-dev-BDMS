import org.junit.Test;


import ch.hearc.cafheg.business.allocations.Canton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MyTestsIT {

    @Test
    @DisplayName("fromValue given NE should be NE")
    void fromValue_GivenFR_ShouldBeFR() {
        assertThat(Canton.fromValue("FR")).isEqualTo(Canton.FR);
    }
}
