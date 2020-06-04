document.addEventListener("DOMContentLoaded", function () {

    var ws = new WebSocket("ws://localhost:8088");
    ws.onopen = function(){
        ws.onmessage = function(event){
            localStorage.setItem('session_id',event.data);
            first_message = false;
        };
    }
    window.addEventListener("storage",function(e){
        if(e.key == 'loggedInUserName'){
            ws.send(e.newValue);
            this.localStorage.removeItem("loggedInUserName");
        }
    },true);
});
