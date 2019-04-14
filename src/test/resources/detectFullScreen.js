// based on: https://groups.google.com/forum/#!topic/selenium-users/Gp2nPWbM1nE
// https://stackoverflow.com/questions/16755129/detect-fullscreen-mode/45192772
const debug = false;
detectFullScreen = function() {
  if ( (window.innerHeight == screen.height) || (!window.screenTop && !window.screenY)) {
    if (debug) {
      alert('Browser is in fullscreen');
    }
    return true;
  } else {
    if (debug) {
      alert('Browser is not in full screen');
    }
    return false;
  }
}
return detectFullScreen();