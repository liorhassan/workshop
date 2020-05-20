var ws = null;
var url = "ws://localhost:8088";
self.addEventListener("connect", function(e) {
    var port = e.ports[0]
    port.addEventListener("message", function(e) {
        if (e.data === "start") {
            if (ws === null) {
                ws = new WebSocket(url);
                ws.onmessage = function(msgEvent) {
                                   alert(msgEvent.data)
                               };
                port.postMessage("started connection to " + url);
            } else {
                port.postMessage("reusing connection to " + url);
            }
        }
    }, false);
    port.start();
}, false);



//    var webSocket = new WebSocket( "ws://localhost:8088");
////    var webSocket = new WebSocket( "ws://localhost:8090/WebSocketServer/client");
//    webSocket.onmessage = function(msgEvent) {
//                            alert(msgEvent.data)
//                       };