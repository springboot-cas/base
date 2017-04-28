window.onload = function () {
    document.getElementsByClassName("j-btn")[0].onclick = function () {
    	window.location.href = document.getElementById("context-path").getAttribute("content");
    }
}