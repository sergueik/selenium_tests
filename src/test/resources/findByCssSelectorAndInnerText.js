/**
 * Find elements by cssSelector and text content. 
 * Derived from: Protractor clientLibrary API functions.findByCssContainingText
 * https://github.com/angular/protractor/blob/master/lib/clientsidescripts.js#L686
 * @param {string} cssSelector The css selector to send to document.querySelectorAll. Default is a 'body *'  
 * @param {string} innerText exact text or a serialized RegExp to search for
 *
 * @return the last element in the array of matching elements (the innermost matching element).
 */
var findByCssSelectorAndInnerText = function(сssSelector, innerText, debug) {
  if (!сssSelector) {
    сssSelector = 'body *';
  }
  if (debug) {
    alert('cssSelector: ' + сssSelector + '\n' + 'text: ' + innerText + '\n' + 'debug: ' + debug);
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
  if (debug) {
    alert((result) ? ('Result:\n' + 'HTML: ' + result.outerHTML + '\n' + 'Text: ' + result.textContent) : 'nothing found');
  }
  return result;
};

var cssSelector = arguments[0];
var innerText = arguments[1];
var debug = arguments[2];
return findByCssSelectorAndInnerText(cssSelector, innerText, debug);