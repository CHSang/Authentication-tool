package vn.axonactive.authentication.domain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vn.axonactive.authentication.domain.ConfigurationEnum;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils { //NOSONAR

    private static final String DATETIME_PATTERN = ConfigPropertiesUtils
            .getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "leaverequest.datetime.pattern");
    private static final String DATE_PATTERN = ConfigPropertiesUtils
            .getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "date.pattern");
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

    public static boolean isOverlapTime(String oldFromDate, String oldToDate, String newFromDate, String newToDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        try {
            LocalDateTime oldFrom = LocalDateTime.parse(oldFromDate, formatter);
            LocalDateTime oldTo = LocalDateTime.parse(oldToDate, formatter);
            LocalDateTime newFrom = LocalDateTime.parse(newFromDate, formatter);
            LocalDateTime newTo = LocalDateTime.parse(newToDate, formatter);
            return !(newTo.isBefore(oldFrom) || newFrom.isAfter(oldTo));
        } catch (DateTimeParseException e) {
            logger.error("Cannot parse date with value ", e);
            return false;
        }

    }

    /**
     * return true if fromDate is before the currentDate
     * 
     * @param fromDate
     * @return
     */
    public static boolean isFromDateLessThanCurrentDay(LocalDate fromDate) {
        LocalDate currentLocalDate = Calendar.getInstance().getTime().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (fromDate.getYear() < currentLocalDate.getYear()) {
            return true;
        }
        if (fromDate.isEqual(currentLocalDate)) {
            return false;
        }
        return fromDate.isBefore(currentLocalDate);
    }
    
    public static LocalDateTime toLocalDateTime(String date, String time, String datePattern, String timePattern) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);
        try {
            LocalDate localDate = LocalDate.parse(date, dateFormatter);
            LocalTime localTime = LocalTime.parse(time, timeFormatter);
            return LocalDateTime.of(localDate, localTime);
        } catch (DateTimeParseException e) {
            logger.error("Cannot parse date with date " + date + " time " + time, e);
            return LocalDateTime.MIN;
        }
    }

    public static String convertToNewFormat(LocalDateTime current, String toPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(toPattern);
        return current.format(formatter);
    }

    public static Date toUtilDate(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        Date fromDateFormatted = new Date();
        try {
            fromDateFormatted = formatter.parse(dateInString);
        } catch (ParseException e) {
            logger.error("Cannot parse date with value " + dateInString, e);
        }

        return fromDateFormatted;
    }

    public static boolean isWeekends(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    public static int calculateDaysOfWeekend(LocalDate start, LocalDate end) {
		int numberOfDays = 0;

		if (start.equals(end)) {
			if (isWeekends(start)) {
				numberOfDays++;
			}
			return numberOfDays;
		}

		LocalDate date = start;
		LocalDate endDate = end;

		do {
			if (isWeekends(date)) {
				numberOfDays++;
				endDate = endDate.plusDays(1);
			}
			date = date.plusDays(1);
		} while (!date.equals(endDate.plusDays(1)));

		return numberOfDays;
	}

}