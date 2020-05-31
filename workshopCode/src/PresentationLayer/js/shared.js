
//<script src="//cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js" integrity="sha256-yr4fRk/GU1ehYJPAs8P4JlTgu0Hdsp4ZKrx8bDEDC3I=" crossorigin="anonymous"></script>
//<script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
//
//<script type="text/javascript" charset="utf-8">
//    var socket = io();
//
//    socket.emit('message', "hey");
//
//    socket.on('message', function (message) {
//        alert("Notification: " + message);
//    });
//
//</script>


//const worker = new SharedWorker("js/SharedWebSocket.js");
//
//document.addEventListener("DOMContentLoaded", function () {
//
//worker.port.start();
//// post message in order to make worker to push this page's port to port array.
//worker.port.postMessage({ id: location.href });
////worker.port.addEventListener("message", e => console.log(e.data.msg));
//worker.port.addEventListener("message", e => alert(e.data.msg));
//
//
//});

    var worker = new SharedWorker("/../js/SharedWebSocket.js");
    worker.port.start();
    worker.port.postMessage("start");
    worker.port.postMessage("defineOnMsg");
