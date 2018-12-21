/**
 * Find elements by css selector and textual content.
 *
 * @param {string} searchText exact text or a serialized RegExp to search for
 *
 * @return last element in the array of matching elements.
 */
var findByCssContainingText = function(сssSelector, searchText) {
  if (сssSelector == null || сssSelector == '' ) {
    _cssSelector = 'body *';
  } else {
	    _cssSelector = сssSelector;
  }
  if (debug != null  && debug ) {
    alert(_cssSelector);
  }
  var elements = document.querySelectorAll(_cssSelector);
  var matches = [];
  for (var i = 0; i < elements.length; ++i) {
    var element = elements[i];
    var elementText = element.textContent || element.innerText || element.getAttribute('placeholder') || '';
    if (elementText.indexOf(searchText) > -1) {
      matches.push(element);
    }
  }
  var result  = matches[matches.length - 1];
  alert("result: " + result.innerText); 
  return result; 
};

var using = arguments[0] || document;
var cssSelector = arguments[1];
var searchText = arguments[2];
return findByCssContainingText(cssSelector, searchText);
