
function updateNavBar(isLoggedIn){
    if(isLoggedIn.loggedin=="True"){
        document.getElementById("navbarCollapse").innerHTML="<ul class=\"navbar-nav mr-auto\"> \
        <li class=\"nav-item active\"> \
            <a class=\"nav-link\" href=\"/html/HomeGuest.html\">Home <span class=\"sr-only\">(current)</span></a>\
        </li>\
        <li class=\"nav-item\">\
            <a class=\"nav-link\" href=\"/html/ShoppingCart.html\">Shopping Cart</a>\
        </li>\
        <li class=\"nav-item\">\
            <a class=\"nav-link\" href=\"/html/ManageMyStores.html\">Manage Stores</a>\
        </li>\
        <li class=\"nav-item\">\
            <a class=\"nav-link\" href=\"#\">Purchase History</a>\
        </li>\
    </ul>\
    <ul class = \"navbar-nav navbar-right\">\
        <li class=\"nav-item\" >\
        <a class=\"nav-link\" href=\"/html/HomeGuest.html\">Logout</a>\
        </li>\
    </ul>"
    } else {
        document.getElementById("navbarCollapse").innerHTML="<ul class=\"navbar-nav mr-auto\">\
        <li class=\"nav-item active\">\
            <a class=\"nav-link\" href=\"/html/HomeGuest.html\">Home <span class=\"sr-only\">(current)</span></a>\
        </li>\
        <li class=\"nav-item\">\
            <a class=\"nav-link\" href=\"/html/ShoppingCart.html\">Shopping Cart</a>\
        </li>\
        <li class=\"nav-item\">\
            <a class=\"nav-link\" href=\"/html/EntryWindow.html\">Login/Register</a>\
        </li>\
    </ul>"
    }
}





