var isDotExist = 0;

var totalDate = $("input[id*='totalLeaveDay']");
var fromDate  = $("input[id*='from']");
var checkBox  = $("#isCorrect");
var listOfTypeButton = $("div[id*='list-types-request']");

var warningMessageCheckboxConfirm = $("div[id*='validateCheckboxConfirm']");
var warningMessageFromDate        = $("div[id*='validateDatefield']");
var warningMessageTotalDay        = $("label[id*='requireTotal']");
var warningMessageType            = $("label[id*='requireType']");

var oldLenOfFromDate = 0;

/* Event binding item need input validation >>> */

totalDate.bind({
	focus : function(e) {
		document.getElementById("labelForTotalLeaveDay").classList
				.add('text_color_label');
	},
	blur : function(e) {
		document.getElementById("labelForTotalLeaveDay")
				.classList
				.remove('text_color_label');
		validateTotalDate();
	}
});

$(".input-field.col.s12.total-leave-day").bind({
	keyup : function(e) {
		clearWarningMessageTotalDate();		
	}
});

totalDate.blur(function() {
	document.getElementById("labelForTotalLeaveDay")
			.classList
			.remove('text_color_label');
	validateTotalDate();
});

fromDate.bind({
	keyup : function(e) {
		if (fromDate.val().replace("_", "").length == 10) {
			validateFromDate();
		}
		else {
			clearWarningMessageFromDate();
		}
	},
	blur : function(e) {
		validateFromDate();
	}
});

checkBox.change(function() {
	clearWarningMessageCheckBox();
});

listOfTypeButton.find("div[class*='ui-button']").on('click',function(event) {
	clearWarningMessageType();
});

/* Event binding item need input validation <<<*/

$(document).ready(function() {
	document.getElementById("labelForTotalLeaveDay")
			.classList
			.add('labelForTotalLeaveDay');

	$('#from').formatter({
		'pattern': '{{99}}/{{99}}/{{9999}}',
	});
});

function getResultOfValidatingFromDate() {
	var finalValidateResult = true;
	var finalMessage = "";
	
	var dateString = fromDate.val();
	if (dateString != "") {
		var comp = dateString.split('/');
		
		var m = parseInt(comp[1], 10);
		var d = parseInt(comp[0], 10);
		var y = parseInt(comp[2], 10);
		
		var date = new Date(y, m - 1, d);
		
		var isDateValid = date.getFullYear()  == y
							&& date.getMonth() + 1 == m
							&& date.getDate() == d;
		
		if (isDateValid) {
			var currentDate = new Date();
			if (currentDate.getFullYear() != y) {
				finalMessage = dateInCurrentYearMessage;
				finalValidateResult = false;
			}
			else if (date.getDay() == 0 || date.getDay() == 6) {
				finalMessage = fromDateIsWeekendMessage;
				finalValidateResult = false;
			}
		}
		else {
			finalMessage = invalidateDateMessage;
			finalValidateResult = false;
		}
	} 
	else {
		finalMessage = requiredFieldMessage;
		finalValidateResult = false;
	}	

	return {"message": finalMessage, "isValid": finalValidateResult};
}

function getResultOfValidatingTotalDate() {
	var isValid = true;
	var message = "";
	
	if (totalDate.val() == "") {
		isValid = false;
		message = requiredFieldMessage;
	}
	else {
		var value = parseFloat(totalDate.val());
		if (!isNaN(value) && (value % 0.5 != 0.0)) {
			isValid = false;
			message = invalidFormatTotalLeaveDay;
		}
	}
	
	return {"message": message, "isValid": isValid };
}

function isATypeOfLeaveSelected() {
	return listOfTypeButton.find("div[class*='ui-state-active']").val() == "";
}

function getResultOfValidatingType() {
	var isValid = true;
	var message = "";
	
	if (!isATypeOfLeaveSelected()) {
		isValid = false;
		message = requiredFieldMessage;
	}
	
	return {"isValid": isValid, "message": message };
}

function getResultOfValidatingCheckBox() {
	var isValid = true;
	var message = "";
	
	if (!checkBox[0].checked) {
		isValid = false;
		message = validateAllInfomation;
	}
	
	return {"isValid": isValid, "message": message };
}

function isAllInputValid() {
	var resultOfValidatingTotalDate = getResultOfValidatingTotalDate();
	var resultOfValidatingFromDate  = getResultOfValidatingFromDate();
	var resultOfValidatingType      = getResultOfValidatingType();
	
	return resultOfValidatingTotalDate["isValid"] 
	    && resultOfValidatingFromDate ["isValid"]
	    && resultOfValidatingType     ["isValid"];
}

function validateOnInput() {
	if (isAllInputValid()) {
		validate();
	}
	else {
		resetAllMessage();
	}
}

function onSendRequestClick() {
	var isAllFieldValid = true;
	
	var isTotalDateValid = validateTotalDate();
	var isFromDateValid  = validateFromDate();
	var isTypeValid      = validateType();
	var isCheckBoxValid  = validateCheckBox();
	
	isAllFieldValid = isTotalDateValid 
		           && isFromDateValid
		           && isTypeValid
		           && isCheckBoxValid;
	
	if (isAllFieldValid) {
		PF('statusDialog').show();
	}
}

function onSendRequestError() {
	PF('statusDialog').hide();
	updateMessage();
	updateFromDateErrorMessage();
}

function onSendRequestSuccess() {
	PF('statusDialog').hide();
	   PF('blankModal').show();
	   Materialize.toast('Your request have been submitted successfully.',
	   					 3500,
	   					 'success-button-toast',
	   					 function() {
							redirectTo();
							}
						 );
}

function onUpdateFromDateErrorMessageComplete() {
	updateFromDataErrorMessageOnSendRequestError();
}

function validateTotalDate() {
	var resultOfValidatingTotalDate = getResultOfValidatingTotalDate();
	if (resultOfValidatingTotalDate["isValid"]) {
		clearWarningMessageTotalDate();
	}
	else {
		setWarningMessageTotalDate(resultOfValidatingTotalDate["message"]);
	}
	
	return resultOfValidatingTotalDate["isValid"];
}

function validateFromDate() {
	var resultOfValidatingFromDate = getResultOfValidatingFromDate();
	if (resultOfValidatingFromDate["isValid"]) {
		clearWarningMessageFromDate();		
	}
	else {
		setWarningMessageFromDate(resultOfValidatingFromDate["message"]);
	}
	
	return resultOfValidatingFromDate["isValid"];
}

function validateType() {
	var resultOfValidatingType = getResultOfValidatingType();
	if (resultOfValidatingType["isValid"]) {
		clearWarningMessageType();
	}
	else {
		setWarningMessageType(resultOfValidatingType["message"]);
	}
	
	return resultOfValidatingType["isValid"];
}

function validateCheckBox() {
	var resultOfValidatingCheckBox  = getResultOfValidatingCheckBox();
	if (resultOfValidatingCheckBox["isValid"]) {
		clearWarningMessageCheckBox();
	}
	else {
		setWarningMessageCheckBox(resultOfValidatingCheckBox["message"]);
	}
	
	return resultOfValidatingCheckBox["isValid"];
}

function fromDateKeyUpListener() {
	var str = fromDate.val().replace(/[_/]/g, "");
	var len = str.length;
	var cursorPos = 0;
	if (len > oldLenOfFromDate) {
		if (len >= 4) {
			cursorPos = len + 2;
		}
		else if (len >= 2) {
			cursorPos = len + 1;
		}
		else {
			cursorPos = len;
		}
		fromDate[0].focus();
		fromDate[0].setSelectionRange(cursorPos, cursorPos);
	}
	oldLenOfFromDate = len;
}

function setWarningMessageCheckBox(message) {
	warningMessageCheckboxConfirm.text(message);
}

function clearWarningMessageCheckBox() {
	warningMessageCheckboxConfirm.text("");
}

function setWarningMessageFromDate(message) {
	warningMessageFromDate.text(message);
	fromDate.addClass("invalid");
}

function clearWarningMessageFromDate() {
	warningMessageFromDate.text("");
	fromDate.removeClass("invalid");
}

function setWarningMessageTotalDate(message) {
	warningMessageTotalDay.text(message);
	totalDate.addClass("invalid");
}

function clearWarningMessageTotalDate() {
	warningMessageTotalDay.text("");
	totalDate.removeClass("invalid");
}

function setWarningMessageType(message) {
	warningMessageType.text(message);
}

function clearWarningMessageType() {
	warningMessageType.text("");
}

function updateFromDataErrorMessageOnSendRequestError() {
	var messageFromBackend = $(document.getElementById("form:fromDateErrorMessage")).html();
	if (messageFromBackend != "") {
		setWarningMessageFromDate(messageFromBackend);
	}
}
