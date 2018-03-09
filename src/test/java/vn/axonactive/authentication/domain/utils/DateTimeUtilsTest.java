package vn.axonactive.authentication.domain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.domain.utils.DateTimeUtils;

public class DateTimeUtilsTest {

	String oldFromDate;
	String oldToDate;

	@Before
	public void init() {
		oldFromDate = "2017-08-01 08:00 AM";
		oldToDate = "2017-08-04 12:00 PM";
	}

	@Test
	public void isOverlapTime_Should_ReturnFalse_When_DateIsInvalid() {
		String newFromDate = "InvalidNewFromDate";
		String newToDate = "InvalidNewToDate";
		Assert.assertFalse(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnFalse_When_TimeIsBeforeOldFromDate() {
		String newFromDate = "2017-07-27 08:00 AM";
		String newToDate = "2017-07-30 05:30 PM";

		Assert.assertFalse(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnFalse_When_TimeIsAfterOldToDate() {
		String newFromDate = "2017-08-04 01:30 PM";
		String newToDate = "2017-08-05 05:30 PM";

		Assert.assertFalse(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnTrue_When_NewTimeContainOldTime() {
		String newFromDate = "2017-07-27 08:00 AM";
		String newToDate = "2017-08-05 05:30 PM";

		Assert.assertTrue(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnTrue_When_NewFromTimeIsInOldTime() {
		String newFromDate = "2017-08-02 08:00 AM";
		String newToDate = "2017-08-05 05:30 PM";

		Assert.assertTrue(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnTrue_When_NewToTimeIsInOldTime() {
		String newFromDate = "2017-08-02 08:00 AM";
		String newToDate = "2017-08-05 05:30 PM";

		Assert.assertTrue(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isOverlapTime_Should_ReturnTrue_When_NewTimeIsContainedInOldTime() {
		String newFromDate = "2017-08-02 08:00 AM";
		String newToDate = "2017-08-03 05:30 PM";

		Assert.assertTrue(DateTimeUtils.isOverlapTime(oldFromDate, oldToDate, newFromDate, newToDate));
	}

	@Test
	public void isFromDateLessThanCurrentDay_Should_ReturnTrue_When_FromDayIsLessThanCurrentDay() {
		LocalDate localDate = LocalDate.of(2017, 07, 31);
		Assert.assertTrue(DateTimeUtils.isFromDateLessThanCurrentDay(localDate));
	}

	@Test
	public void isFromDateLessThanCurrentDay_Should_ReturnFalse_When_FromDayIsEqualToCurrentDay() {
		LocalDate localDate = Calendar.getInstance().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Assert.assertFalse(DateTimeUtils.isFromDateLessThanCurrentDay(localDate));
	}
	
	@Test
	public void isFromDateLessThanCurrentDay_Should_ReturnTrue_When_YearOfFromDayIsLessThanYearOfCurrentDay() {
	    LocalDate localDate = LocalDate.now().minusYears(1);
	    Assert.assertTrue(DateTimeUtils.isFromDateLessThanCurrentDay(localDate));
	}

	@Test
	public void isFromDateLessThanCurrentDay_Should_ReturnTrue_When_FromDayIsGreaterThanCurrentDay() {
		LocalDate localDate = Calendar.getInstance().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		localDate.plusDays(1);
		Assert.assertFalse(DateTimeUtils.isFromDateLessThanCurrentDay(localDate));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenWeekendInMiddleDuration() {
		LocalDate start = LocalDate.of(2017, 8, 9);	// 09/08/2017 is Wednesday
		LocalDate end = LocalDate.of(2017, 8, 14);	// 14/08/2017 is Monday

		Assert.assertEquals(2, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenWeekendAtStartDateOfDuration() {
		LocalDate start = LocalDate.of(2017, 8, 13);	// 13/08/2017 is Sunday
		LocalDate end = LocalDate.of(2017, 8, 14);		// 14/08/2017 is Monday

		Assert.assertEquals(1, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenWeekendAtEndDateOfDuration() {
		LocalDate start = LocalDate.of(2017, 8, 9);		// 09/08/2017 is Wednesday
		LocalDate end = LocalDate.of(2017, 8, 13);		// 13/08/2017 is Sunday

		Assert.assertEquals(2, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenDurationDontHaveWeekend() {
		LocalDate start = LocalDate.of(2017, 8, 7);		// 13/08/2017 is Monday
		LocalDate end = LocalDate.of(2017, 8, 11);		// 13/08/2017 is Friday

		Assert.assertEquals(0, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenWhenDurationIsWeekend() {
		LocalDate start = LocalDate.of(2017, 8, 12);	// 12/08/2017 is Saturday
		LocalDate end = LocalDate.of(2017, 8, 12);

		Assert.assertEquals(1, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenDurationIsNotWeekend() {
		LocalDate start = LocalDate.of(2017, 8, 7);
		LocalDate end = LocalDate.of(2017, 8, 7);

		Assert.assertEquals(0, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void calculateDaysOfWeekend_Should_ReturnANumberOfWeekend_WhenWhenDurationAreWeekend() {
		LocalDate start = LocalDate.of(2017, 8, 12);
		LocalDate end = LocalDate.of(2017, 8, 13);

		Assert.assertEquals(2, DateTimeUtils.calculateDaysOfWeekend(start, end));
	}

	@Test
	public void convertToNewFormat_Should_ReturnLocalDateTimeMin_When_ParseUnsuccessful() {
		DateTimeUtils.toLocalDateTime("invalidDate", "invalidTime", "dd/MM/yyyy", "H:mm");
	}

	@Test
	public void convertToNewFormat_Should_ReturnCorrectFormat() {
		LocalDateTime current = DateTimeUtils.toLocalDateTime("01/08/2017", "13:30", "dd/MM/yyyy", "H:mm");
		String actual = DateTimeUtils.convertToNewFormat(current, "yyyy-MM-dd hh:mm a");
		String expect = "2017-08-01 01:30 PM";
		Assert.assertEquals(expect, actual);
	}

	@Test
	public void toDateType_Should_ReturnRightDate_When_StringIsValid() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "date.pattern"));

		Date expectedDate = formatter.parse("08/12/2017");
		String date = "08/12/2017";
		Assert.assertEquals(expectedDate, DateTimeUtils.toUtilDate(date));
	}

	@Test
	public void toDateType_Should_ReturnEmpty_WhenStringIsInvalid() {
		String date = "abcxyz";
		DateTimeUtils.toUtilDate(date);
	}

}
