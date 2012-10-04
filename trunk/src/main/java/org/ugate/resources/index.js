// updates the toggle switches based upon the data-value attribute
function updateFlipToggles() {
	try {
		$('select').each(function() {
			var id = $(this).attr('id');
			var v = $(this).attr('data-value');
			//alert("select value = " + v);
			if (v) {
				var ops = this.getElementsByTagName('option');
				//alert("options = " + ops);
				for (var i=0; i<ops.length; i++) {
					var ov = $(ops[i]).attr('value');
					if (ov && v.toString() == ov.toString()) {
						$(ops[i]).attr('selected', 'selected');
						//alert("select " + id + " value = " + v + " option " + i + " selected = " + $(ops[i]).attr("selected"));
					} else {
						$(ops[i]).removeAttr('selected');
					}
				}
			}
			return true;
		});
	} catch (e) {
		alert(e);
	}
}
updateFlipToggles();
$(document).ready(function() {
	updateFlipToggles();
	//$.mobile.initializePage();
});
$(document).bind("mobileinit", function(){
	$.mobile.initializePage = false;
	$.mobile.ajaxEnabled = false;
	$.mobile.page.prototype.options.domCache = false;
});
function execCommand(command) {
	var loc = '/?command=' + command;
}
function submitForm(isNodeSelect) {
	if (isNodeSelect) {
		var rnId = $("input[name=remoteNodeId]:checked").val();
		if (rnId) {
			$('#rnSelectForm').submit();
			//$.mobile.changePage("/?remoteNode=" + id);
		}
	} else if($('#rnSelect').style.display != 'none') {
		$('#rnSelectForm').submit();
	} else {
		$('#settingsForm').submit();
	}
}
$('#mainPage').live("pageinit", function(event) {
	updateFlipToggles();
	//$.mobile.changePage($('#mainPage'), {transition: 'slidedown'});
//	$(":input[@name='remoteNodeId']").live('change mousedown',function(event) { 
//		submitForm(true);
//	});
//	$(":input[@name='remoteNodeIdReselect']").live('change mousedown',function(event) { 
//		submitForm();
//	});
//	$.mobile.loading('show');
//	$('head').append('<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.1/jquery.mobile-1.1.1.min.css" />');
});