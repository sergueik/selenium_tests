// origin: https://ru.selenide.org/2019/12/06/advent-calendar-visualize-click/ (in Russian)
function onClick(event) {
    var e = event || window.event;
    var target = e.target || e.srcElement;
    target.style['box-sizing'] = 'border-box';
    target.style['border'] = '2px solid green';
}

document.addEventListener('click', onClick);
