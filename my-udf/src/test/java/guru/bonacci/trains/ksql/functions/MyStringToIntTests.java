package guru.bonacci.trains.ksql.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MyStringToIntTests {

  @ParameterizedTest(name = "convert({0})= {1}")
  @CsvSource({
    "1, 1",
    "2, 2",
    "-10, -10",
  })
  void convert(final String source, final String expectedResult) {
    final MyStringToInt s2i = new MyStringToInt();
    final Integer actualResult = s2i.stringToInt(source);
    assertEquals(Integer.valueOf(expectedResult), actualResult, source + " should equal " + expectedResult);
  }
}