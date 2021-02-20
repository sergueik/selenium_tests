// based on: https://github.com/selenide/selenide/blob/master/src/main/java/com/codeborne/selenide/commands/SetValue.java
var setValue = function(element, text) {


if (element.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';
if (element.getAttribute('disabled') != undefined) return 'Cannot change value of disabled element';
element.focus();
var maxlength = element.getAttribute('maxlength') == null ? -1 : parseInt(element.getAttribute('maxlength'));
element.value = maxlength == -1 ? text : text.length <= maxlength ? text : text.substring(0, maxlength);
return null;
}

var text = arguments[1];

console.log("Argument type is " + typeof(arguments[0]));
if (typeof(arguments[0]) === 'string') {
  var selector = arguments[0];
  // alert("Variable selector value type is " + typeof(selector));
  var nodes = window.document.querySelectorAll(selector);
  if (nodes) { 
    setValue(nodes[0], text);
  }
} else {
  var element = arguments[0]; 
  setValue(element, text);
}
return;