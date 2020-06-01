//var ws = null;
//var url = "ws://localhost:8088";
//self.addEventListener("connect", function(e) {
//    var port = e.ports[0]
//    port.addEventListener("message", function(e) {
//        if (e.data === "start") {
//            if (ws === null) {
//                ws = new WebSocket(url);
//                ws.onmessage = function(msgEvent) {
//                                   alert(msgEvent.data)
//                               };
//                port.postMessage("started connection to " + url);
//            } else {
//                port.postMessage("reusing connection to " + url);
//            }
//        }
//    }, false);
//    port.start();
//}, false);
//
//const ports = {};
//const ws = new WebSocket("ws://localhost:8088");
//
////static send(username) {
////    ws.send(username);
////};
//
//ws.addEventListener("message", e => {
//  Object.values(ports).forEach(port => {
//    port.postMessage({ msg: "From sever: " + e.data });
//  });
//});
//
//addEventListener("connect", e => {
//  const port = e.ports[0];
//  port.start();
//  port.addEventListener("message", e => {
//    // if you open same html page at multiple windows,
//    // only newest window can send and recieve a message.
//    if (e.data.id) ports[e.data.id] = port;
//    if (e.data.msg) ws.send(e.data.msg);
//  });
//});


var ws = null
var url = "ws://localhost:8088"
self.addEventListener("connect", function(e) {
    //console.log("in connect");
    var port = e.ports[0]
    port.addEventListener("message", function(e) {
        if (e.data === "start") {
            if (ws === null) {
                ws = new WebSocket(url);
                ws.on("message", msg => {alert(msg.data)});
            }
        }else{
            ws.send(e.data);
        }
    }, false);
    port.start();
}, false);