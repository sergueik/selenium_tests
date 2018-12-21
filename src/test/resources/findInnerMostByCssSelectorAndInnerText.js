/**
 * Find elements by css selector and textual content.
 * Based on: Protractor clientLibrary API
 * @param {string} cssSelector The css selector to match. When not provided, set to 'body *'  
 * @param {string} searchText exact text or a serialized RegExp to search for
 *
 * @return last element in the array of matching elements (the innermost matching element is taken to the caller).
 */
var findInnerMostByCssSelectorAndInnerText = function(сssSelector, searchText) {
  if (сssSelector == null || сssSelector == '' ) {
    _cssSelector = 'body *';
  } else {
	    _cssSelector = сssSelector;
  }
  var _debug = false;
  if (_debug != null  && _debug ) {
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
  if (_debug != null  && _debug ) {
    alert("result: " + result.innerText); 
  }
  return result; 
};

var cssSelector = arguments[0];
var searchText = arguments[1];
return findInnerMostByCssSelectorAndInnerText(cssSelector, searchText);
