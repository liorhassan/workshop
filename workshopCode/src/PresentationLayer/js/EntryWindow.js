

document.addEventListener("DOMContentLoaded", function () {
    //var webSocket = new WebSocket( "ws://localhost:8088");
    //webSocket.onmessage = function(msgEvent) {
      //                      alert(msgEvent.data)
    //};
    document.getElementById("loginBtn").addEventListener("click", function () {

        var inputUsername = document.getElementById("inputUsername").value;
        var inputPassword = document.getElementById("inputPassword").value;
        var isAdminMode = document.getElementById("isAdmin").checked;

        fetch("http://localhost:8080/tradingSystem/login", {
            method: "POST",
            body: JSON.stringify({userName:inputUsername, password:inputPassword, adminMode:isAdminMode })
        })
         .then(response => {
             if (response.ok) {
                 return response.json();
             } else {
                 return response.text();
             }
          })
         .then((responseMsg) => {
             if (responseMsg.SUCCESS) {
                  Swal.fire(
                        'Congratulations!',
                        responseMsg.SUCCESS,
                        'success').then(() => {
                        if (isAdminMode) {
                            window.location.href = "/html/AdminWindow.html";
                        }
                        else{
                            window.location.href = "/html/HomeGuest.html";
                        }
                        })
                        //webSocket.send(inputUsername);
//                        worker.port.postMessage(inputUsername);

             } else {
                  Swal.fire(
                     'OOPS!',
                     responseMsg,
                     'error')
             }
         })
    })

    document.getElementById("registerBtn").addEventListener("click", function () {

            var inputUsername = document.getElementById("inputUsername").value;
            var inputPassword = document.getElementById("inputPassword").value;

            fetch("http://localhost:8080/tradingSystem/register", {
                method: "POST",
                body: JSON.stringify({userName:inputUsername, password:inputPassword})
            })
             .then(response => {
                 if (response.ok) {
                     return response.json();
                 } else {
                     return response.text();
                 }
              })
             .then((responseMsg) => {
                 if (responseMsg.SUCCESS) {
                      Swal.fire(
                            'Congratulations!',
                            responseMsg.SUCCESS,
                            'success')
                 } else {
                      Swal.fire(
                         'OOPS!',
                         responseMsg,
                         'error')
                 }
             })
        })

});




