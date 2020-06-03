document.addEventListener("DOMContentLoaded", function () {

    var ws = new WebSocket("ws://localhost:8088");
    ws.onopen = function(){
        ws.onmessage = function(event){
            alert(event.data);
        };
    }
    window.addEventListener("storage",function(e){
        if(e.key == 'loggedInUserName'){
            ws.send(e.newValue);
            this.localStorage.removeItem("loggedInUserName");
        }
    },true);
});
