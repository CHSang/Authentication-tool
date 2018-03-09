var url = "#{request.contextPath}/views/employee_management.xhtml";
// timer
var isLoseFocus = false;
String.prototype.replaceAll = function(search, replacement) {
	var target = this;
	return target.replace(new RegExp(search, 'g'), replacement);
};



$(document).ready(function() {
	
	/* check current page to add active class to menu-bar items */
	checkCurrentPage();

	/* click event of menu bar */
	$(window).click(function(event) {
		closeOpenMenuItems();
		var menu = $("#menu-bar #menu-button").next("ul");
		if (menu.hasClass("open")) {
			menu.removeClass("open");
		}
	});

	$("#menu-bar #menu-button").click(function() {
		var menu = $(this).next("ul");
		if (menu.hasClass("open")) {
			menu.removeClass("open");
			closeOpenMenuItems();
		} else {
			menu.addClass("open");
		}
		event.stopPropagation();
	});

	$("#menu-bar>ul>li").click(function(event) {
		var submenu = $(this).find("ul");
		if (submenu.hasClass("open")) {
			$(this).removeClass("open");
			submenu.removeClass("open");
		} else {
			closeOpenMenuItems();
			$(this).addClass("open");
			submenu.addClass("open");
		}
		event.stopPropagation();
	});
	/* end click event of menu bar */

	/* copy right year */
	var date = new Date();
	var year = date.getFullYear();
	$("span.copyright-year").html(year);

	setHeight($("div.ui-datatable-scrollable-body"));

	/* remove empty field of team on small devices of employee_detail page */
	$(window).resize(function() {
		displayEmployeeDetailInformation();
	});

	$(window).ready(function() {
		displayEmployeeDetailInformation();
	});

	/* remove valid class on IE */
	$(".input-field input").blur(function() {
		if ($(this).val() == "") {
			if ($(this).hasClass("valid")) {
				$(this).removeClass("valid");
			}
		}
	});

	/* remove on-click action materialize added */
	$('button').prop('onclick', null);

	/* update text field of materialize */
	Materialize.updateTextFields();
});

function checkCurrentPage() {
	var pathname = window.location.pathname;
	if (pathname.indexOf("/employee/") != -1 || pathname == "/hrtool/") {
		$("#menu-bar-employee").addClass("menu-bar-active");
	}
	if (pathname.indexOf("/request/") != -1) {
		$("#menu-bar-request").addClass("menu-bar-active");
	}
}

function closeOpenMenuItems() {
	var otherMenuItems = $("#menu-bar>ul>li");
	for (var i = 0, lenMenu = otherMenuItems.length; i < lenMenu; i++) {
		if (otherMenuItems.eq(i).hasClass("open")) {
			otherMenuItems.eq(i).removeClass("open");
		}
	}
	var otherSubMenuItems = $("#menu-bar>ul>li>ul");
	for (var j = 0, lenSubMenu = otherSubMenuItems.length; j < lenSubMenu; j++) {
		if (otherSubMenuItems.eq(j).hasClass("open")) {
			otherSubMenuItems.eq(j).removeClass("open");
		}
	}
}
function setHeight(table) {
	var header = $('div.header');
	var footer = $('footer');
	if (typeof table !== "undefined") {
		$(window).on(
				'resize',
				function() {
					var height = $(window).height() - header.height()
							- footer.height() - 200;
					table.height(height);
				}).trigger('resize');
	}
}
function hiddenBlankInformation() {
	var department = $("#employee-detail-form\\:department-content").html();
	var location = $("#employee-detail-form\\:location-content").html();
	var homephone = $("#employee-detail-form\\:homephone-content").html();
	var skype = $("#employee-detail-form\\:skype-content").html();

	if (location == "" && skype == "") {
		$("#location-container").hide();
		$("#skype-container").hide();
		if (department == "" && homephone == "") {
			$("#department-container").hide();
			$("#homephone-container").hide();
		}
	}
}
function displayEmployeeDetailInformation() {
	if ($(window).width() <= 640) {
		if ($("#employee-detail-form\\:department-content").html() == "") {
			$("#department-container").hide();
		}
		if ($("#employee-detail-form\\:location-content").html() == "") {
			$("#location-container").hide();
		}
		if ($("#employee-detail-form\\:homephone-content").html() == "") {
			$("#homephone-container").hide();
		}
		if ($("#employee-detail-form\\:skype-content").html() == "") {
			$("#skype-container").hide();
		}
	} else {
		$("#department-container").show();
		$("#location-container").show();
		$("#homephone-container").show();
		$("#skype-container").show();
		hiddenBlankInformation();
	}
}

function focusCampo(id) {
	var inputField = document.getElementById(id);
	if (inputField != null && inputField.value.length != 0) {
		if (inputField.createTextRange) {
			var FieldRange = inputField.createTextRange();
			FieldRange.moveStart('character', inputField.value.length);
			FieldRange.collapse();
			FieldRange.select();
		} else if (inputField.selectionStart
				|| inputField.selectionStart == '0') {
			var elemLen = inputField.value.length;
			inputField.selectionStart = elemLen;
			inputField.selectionEnd = elemLen;
			inputField.focus();
		}
	} else {
		inputField.focus();
	}
}

function getParameterByName(name, url) {
	if (!url)
		url = window.location.href;
	name = name.replace(/[\[\]]/g, "\\$&");
	var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
			.exec(url);
	if (!results)
		return null;
	if (!results[2])
		return '';
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function setIsLoseFocusToTrue() {
	isLoseFocus = true;
}

function handleAjax(xhr, status, args) {

	if (xhr.status == 999) {
		window.location.reload();
	}
}

function replaceSearchStringToParam() {
	var searchString = $("input[id*='searchstring']").val().trim();
	history.replaceState([], '', "?q=" + searchString);
}