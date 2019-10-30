package guru.bonacci.trains.ksql.functions;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

@UdfDescription(name = "mystringtoint", description = "string to int reinvented")
public class MyStringToInt {

	@Udf(description = "String to int")
	public Integer stringToInt(
			@UdfParameter(value = "source", description = "the string to convert") final String source) {
		return Integer.valueOf(source);
	}
}
