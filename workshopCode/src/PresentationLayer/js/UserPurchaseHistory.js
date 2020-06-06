const window_name = "UserPurchaseHistory";
document.addEventListener("DOMContentLoaded", function () {

    fetch("http://localhost:8080/tradingSystem/isLoggedIn")
    .then(response=>response.json())
    .then(updateNavBar);
    


     fetch("http://localhost:8080/tradingSystem/userPurchaseHistory")
         .then(response => response.json())
          .then(setMyHistory)

});


function setMyHistory(history){
    document.getElementById("num-of-purchases").innerHTML=history.length

    var historyList = document.getElementById("user-history-list");
    //historyList.innerHTML = "<br>";
    history.innerHTML = "";
    history.forEach(purchase=>{
        purchase.forEach(prod=>{
            const element = document.createElement("li");
            element.classList.add("list-group-item");
            element.classList.add("d-flex");
            element.classList.add("justify-content-between");
            element.classList.add("lh-condensed");
            const element_div = document.createElement("div");
            const product_name = document.createElement("h6");
            product_name.classList.add("my-0");
            product_name.append(document.createTextNode(prod.name));
            const amount = document.createElement("small");
            amount.classList.add("text-muted");
            amount.append(document.createTextNode("Amount: "+prod.amount));
            element_div.appendChild(product_name);
            element_div.appendChild(amount);
            const price = document.createElement("span");
            price.classList.add("text-muted");
            price.append(document.createTextNode("$" + prod.price));
            element.appendChild(element_div);
            element.appendChild(price); 
            historyList.appendChild(element);
        })
        const break_element = document.createElement("br");
        historyList.appendChild(break_element);
    })
}




