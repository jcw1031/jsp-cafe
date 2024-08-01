package woopaca.jspcafe.database;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MySQLPropertiesTest {

    @Test
    void getUrl_메서드는_jdbc_url_값을_반환한다() {
        String expected = "jdbc:h2:mem:test?MODE=MYSQL";
        String actual = MySQLProperties.getUrl();
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}