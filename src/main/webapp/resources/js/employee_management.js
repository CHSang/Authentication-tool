window.addEventListener("pageshow", function() {
	var a= getParameterByName("q",window.location.href);
	$("input[id*='searchstring']").val(a);
	$("input[id*='searchstring']").keyup();	
});
$(document).ready(function() {
	/* Prevent key Enter down on Employee Search */
	$("input[id*='searchstring']").keydown(function(event) {
		if (event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
	// this function use for sticky header of table
	var topPosition = 0;
	if ($("div.font-head-table").length) {
		topPosition = $("div.font-head-table").offset().top;
	} else if ($("div.table-header").length) {
		topPosition = $("div.table-header").offset().top;
	}
	
	$(document).scroll(function() {
		if ($(window).scrollTop() > topPosition) {
			$("div.table-header").addClass("sticky");
		} else
			$("div.table-header").removeClass("sticky");
	});
	// *******************************************
});


