  <script>
      var worker = new SharedWorker("SharedWebSocket.js");
      worker.port.addEventListener("message", function(e) {
          console.log("Got message: " + e.data);
          alert(e.data);
      }, false);
      worker.port.start();
      worker.port.postMessage("start");
  </script>



