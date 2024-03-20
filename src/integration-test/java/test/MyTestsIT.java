package java.test;

import ch.hearc.cafheg.business.allocations.Canton;
import org.assertj.core.api.AbstractBigDecimalAssert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MyTestsIT {
    @Test
    @DisplayName("fromValue given NE should be NE")
    void fromValue_GivenFR_ShouldBeFR() {
        assertThat(Canton.fromValue("FR")).isEqualTo(Canton.FR);
    }

    private <SELF extends AbstractBigDecimalAssert<SELF>> AbstractBigDecimalAssert<SELF> assertThat(Canton fr) {
        return null;

    }
}
