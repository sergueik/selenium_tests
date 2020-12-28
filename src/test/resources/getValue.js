// simplified 'getText' for retrieveing the values shown in textarea nodes
// that are not displayed through  Selenium getText() or getAttribute('innerHTML')
const element = arguments[0]; const debug = arguments[1]; return element.value;
