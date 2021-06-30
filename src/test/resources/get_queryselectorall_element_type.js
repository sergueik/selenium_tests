var selector = arguments[0];
var elements = document.querySelectorAll(selector);
let results = [];
// https://stackoverflow.com/questions/7390426/better-way-to-get-type-of-a-javascript-variable
var toType = function (obj) {
  return ({}).toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase()
}
// origin: https://developer.mozilla.org/en-US/docs/Web/API/NodeList/forEach
if (window.NodeList && !NodeList.prototype.forEach) {
  NodeList.prototype.forEach = function (callback, thisArg) {
    thisArg = thisArg || window;
    for (var i = 0; i < this.length; i++) {
      callback.call(thisArg, this[i], i, this);
    }
  };
}

elements.forEach(
  function (currentValue, currentIndex, listObj) {
    results.push(toType(currentValue));
  },
  ''
);
return results;