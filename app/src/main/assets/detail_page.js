var title;
var author;
var time;
var body;
var images;
var imageCount = 0;

function load_day() {
    document.bgColor="#FF0000";
    var font = document.getElementById("font");
    if (!font) {
        return;
    }
    font.style.color="white";
}

function load_night() {
    alert('hello');
    document.bgColor="#1f1f1f";
        var font = document.getElementById("font");
        if (!font) {
            return;
        }
        font.style.color="black";
}

function fill(detailBody) {
     var myBody = document.getElementById('body');
     myBody.innerHTML = detailBody
 }