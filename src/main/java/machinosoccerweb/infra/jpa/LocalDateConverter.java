package machinosoccerweb.infra.jpa;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {
  @Override
  public Date convertToDatabaseColumn(LocalDate attribute) {
    return Optional.ofNullable(attribute)
        .map(a -> Date.valueOf(a))
        .orElse(null);
  }

  @Override
  public LocalDate convertToEntityAttribute(Date dbData) {
    return Optional.ofNullable(dbData)
        .map(d -> d.toLocalDate())
        .orElse(null);
  }
}
