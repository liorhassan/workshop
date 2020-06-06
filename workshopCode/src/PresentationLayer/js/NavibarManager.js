var nav_curr_mapping={
    MainSearch:["",""],
    ShoppingCart:["",""],
    ManageMyStores:["",""],
    PurchaseHistory:["",""],
    Entry:["",""]
}
const active_lables = [" active"," <span class=\"sr-only\">(current)</span>"];
function updateNavBar(isLoggedIn){
    if(window_name == "MainSearch")
        nav_curr_mapping.MainSearch = active_lables;
    if(window_name == "ManageMyStores")
        nav_curr_mapping.ManageMyStores = active_lables;
    if(window_name == "ShoppingCart")
        nav_curr_mapping.ShoppingCart = active_lables;
    if(window_name == "UserPurchaseHistory")
        nav_curr_mapping.PurchaseHistory = active_lables;
    if(window_name == "EntryWindow")
        nav_curr_mapping.Entry = active_lables;
    if(isLoggedIn.loggedin=="True"){
        document.getElementById("navbarCollapse").innerHTML=`<ul class=\"navbar-nav mr-auto\"> \
        <li class=\"nav-item${nav_curr_mapping.MainSearch[0]}\"> \
            <a class=\"nav-link\" href=\"/html/SearchWindow.html\">Home${nav_curr_mapping.MainSearch[1]}</a>\
        </li>\
        <li class=\"nav-item${nav_curr_mapping.ShoppingCart[0]}\">\
            <a class=\"nav-link\" href=\"/html/ShoppingCart.html\">Shopping Cart${nav_curr_mapping.ShoppingCart[1]}</a>\
        </li>\
        <li class=\"nav-item${nav_curr_mapping.ManageMyStores[0]}\">\
            <a class=\"nav-link\" href=\"/html/ManageMyStores.html\">Manage Stores${nav_curr_mapping.ManageMyStores[1]}</a>\
        </li>\
        <li class=\"nav-item${nav_curr_mapping.PurchaseHistory[0]}\">\
            <a class=\"nav-link\" href=\"UserPurchaseHistory.html\">Purchase History${nav_curr_mapping.PurchaseHistory[1]}</a>\
        </li>\
        </ul>\
        <ul class = \"navbar-nav navbar-right\">\
            <li class=\"nav-item\" >\
            <a class=\"nav-link\" id=\"logout_navbar\" href=\"/html/SearchWindow.html\">Logout</a>\
            </li>\
        </ul>`;


    document.getElementById("logout_navbar").addEventListener("click",function(){
        fetch("http://localhost:8080/tradingSystem/logout");
        });
    } else {
        document.getElementById("navbarCollapse").innerHTML=`<ul class=\"navbar-nav mr-auto\">\
        <li class=\"nav-item${nav_curr_mapping.MainSearch[0]}\">\
            <a class=\"nav-link\" href=\"/html/SearchWindow.html\">Home${nav_curr_mapping.MainSearch[1]}</a>\
        </li>\
        <li class=\"nav-item${nav_curr_mapping.ShoppingCart[0]}\">\
            <a class=\"nav-link\" href=\"/html/ShoppingCart.html\">Shopping Cart${nav_curr_mapping.ShoppingCart[1]}</a>\
        </li>\
        <li class=\"nav-item${nav_curr_mapping.Entry[0]}\">\
            <a class=\"nav-link\" href=\"/html/EntryWindow.html\">Login/Register${nav_curr_mapping.Entry[1]}</a>\
        </li>\
    </ul>`
    }

}





