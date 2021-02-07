i// based on: https://qna.habr.com/q/931725
fetchImage = async function(url) {
  const data = await fetch(url, {
    credentials: 'include'
  });
  // {credentials: 'omit'} for CORS
  const contentType = data.headers.get('content-type');
  const buffer = await data.arrayBuffer();
  const blob = new Blob([buffer], {
    type: contentType
  });
  return blob;
}
createLinkImage = async function(url) {
  var image_name = url.replace(/^.*[^\/]\//, '');
  var blob = await fetchImage(url);
  var link = document.createElement('a');
  link.id = 'saveImageLink';
  link.href = URL.createObjectURL(blob);
  link.download = image_name;
  link.innerHTML = 'download the image ' + image_name;
  document.body.append(link);
}
var url = arguments[0];
if (url == null) {
  url = 'https://www.linux.org.ru/images/19581/original.png'
}
createLinkImage(url);

