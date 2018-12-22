/**
 * Find elements by css selector and inner Textual content. Alternative to xpath locator //*[contains(text(),'...'))
 * Derived from: Protractor clientLibrary API
 * https://github.com/angular/protractor/blob/master/lib/clientsidescripts.js#L686
 * @param {string} cssSelector The css selector to send to document.querySelectorAll. Default is a 'body *'  
 * @param {string} innerText exact text or a serialized RegExp to search for
 *
 * @return last element in the array of matching elements (the innermost matching element is taken to the caller).
 */
var findByCssSelectorAndInnerText = function(сssSelector, innerText) {
  if (сssSelector == null || сssSelector == '') {
    сssSelector = 'body *';
  }
  if (debug != null && debug) {
    alert('cssSelector: ' +сssSelector);
  }
  if (debug != null && debug) {
	alert('text: ' + innerText );
  }
  if (innerText.indexOf('__REGEXP__') === 0) {
    var match = innerText.split('__REGEXP__')[1].match(/\/(.*)\/(.*)?/);
    innerText = new RegExp(match[1], match[2] || '');	  
  }

  var elements = document.querySelectorAll(сssSelector);
  var matches = [];
  for (var i = 0; i < elements.length; ++i) {
    var element = elements[i];
    var elementText = element.textContent.replace(/\n/, ' ') || element.innerText.replace(/\n/, ' ') || element.getAttribute('placeholder') || '';
    var elementMatches = innerText instanceof RegExp ?
      innerText.test(elementText) :
      elementText.indexOf(innerText) > -1;
    if (elementMatches) {
      matches.push(element);
    }
  }
  var result = matches[matches.length - 1];
  if (debug != null && debug) {
	  if (result!= null ){
			 alert('Result: ' + /* result.outerHTML */ result.textContent );
	  } else {
		  alert('nothing found');
		  
	  }
  }
  return result;
};

var debug = false;
var cssSelector = arguments[0];
var innerText = arguments[1];
return findByCssSelectorAndInnerText(cssSelector, innerText);