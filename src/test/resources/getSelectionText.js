/**
 * Returns the currently selected part of the page
 *
 */
// origin: https://stackoverflow.com/questions/5379120/get-the-highlighted-selected-text

getSelectionText = function () {
  var text = "";
  if (window.getSelection) {
    text = window.getSelection().toString();
  } else if (document.selection && document.selection.type != "Control") {
    text = document.selection.createRange().text;
  }
  return text;
};
return getSelectionText();