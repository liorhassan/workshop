document.addEventListener("DOMContentLoaded", function () {

    var ws = new WebSocket("ws://localhost:8088");
    ws.onopen = function(){
        ws.onmessage = function(event){
            var message = JSON.parse(event.data);
            if(message.session_id)
                localStorage.setItem("session_id",message.session_id);
            else
                message.forEach(element => {alert(element)});
        };
    }
    window.addEventListener("storage",function(e){
        if(e.key == 'loggedInUserName'){
            ws.send(e.newValue);
            this.localStorage.removeItem("loggedInUserName");
        }
    },true);
});
