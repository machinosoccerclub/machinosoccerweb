package machinosoccerweb.infra.jpa;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {
  @Override
  public Date convertToDatabaseColumn(LocalDate attribute) {
    return Date.valueOf(attribute);
  }

  @Override
  public LocalDate convertToEntityAttribute(Date dbData) {
    return dbData.toLocalDate();
  }
}
