const window_name = "AdminView";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn", {
        method: "POST",
        body: JSON.stringify({session_id: localStorage["session_id"]})
    })
    .then(response=>response.json())
    .then(updateNavBar);

    document.getElementById("filter-button").addEventListener("click",function(){
        var fromD = document.getElementById("from-date").value;
        var toD = document.getElementById("to-date").value;
        fetch("http://localhost:8080/tradingSystem/adminstats", {
        method: "POST",
        body: JSON.stringify({from_date: fromD, to_date: toD})
    })
    .then(response => {
        if (response.ok) {
            response.json().then(updateStatsTable);
        } else {
            response.text().then(responseMsg=>{
                Swal.fire(
                    'OOPS!',
                    responseMsg,
                    'error')
            })
        }
    })
    })
})

function getCustomerHistory() {
    const customerName = document.getElementById("customerName").value;
    fetch('http://localhost:8080/tradingSystem/userPurchaseHistoryAdmin', {
        method: 'POST',
        body: JSON.stringify({session_id: localStorage["session_id"], user: customerName })
    })
        .then(response => {
            if (response.ok) {
                return response.json()
            } else {
                return response.text()
            }
        })
        .then(data => {
            if (typeof (data) == "string") {
                Swal.fire(
                    'OOPS..',
                    data,
                    'error')

            } else if (data.length == 0) {
                Swal.fire(
                    'Unfortunatly',
                    "The user has no purchase history",
                    'warning')
            } else {
                setCustomerHistory(data)
            }
        })

    document.getElementById("customerName").value = "";
    closeModal("viewCustomerModal");
}

function getStoreHistory() {
    const storeName = document.getElementById("storeName").value;
    fetch('http://localhost:8080/tradingSystem/storePurchaseHistoryAdmin', {
        method: 'POST',
        body: JSON.stringify({session_id: localStorage["session_id"], store: storeName })
    })
        .then(response => {
            if (response.ok) {
                return response.json()
            } else {
                return response.text()
            }
        })
        .then(data => {
            if (typeof (data) == "string") {
                Swal.fire(
                    'OOPS..',
                    data,
                    'error')

            } else if (data.length == 0) {
                Swal.fire(
                    'Unfortunatly',
                    "The store has no purchase history",
                    'warning')
            } else {
                setCustomerHistory(data)
            }
        })

    document.getElementById("storeName").value = "";
    closeModal("viewCustomerModal");
}

function setHistory(items) {
    const HistoryDiv = document.getElementById("HistoryPurchases");
    HistoryDiv.innerHTML = "";
    HistoryDiv.classList.add("jumbotron");
    HistoryDiv.classList.add("text-center");

    const HistoryHeader = document.createElement("h2")
    HistoryHeader.append(document.createTextNode("Purchases History"))
    HistoryDiv.appendChild(HistoryHeader);

    const HistoryPurchasesContent = document.createElement("div")

    items.forEach(currItem => {
        const cardItem = document.createElement("div")
        cardItem.classList.add("card");

        const cardBodyItem = document.createElement("div")
        cardBodyItem.classList.add("card-body");

        const storeLabel = document.createElement("label")
        storeLabel.append(document.createTextNode("store name: " + currItem.store))
        const nameLabel = document.createElement("label")
        nameLabel.append(document.createTextNode("Product name: " + currItem.name))
        const amountLabel = document.createElement("label")
        amountLabel.append(document.createTextNode("amount: " + currItem.amount))
        const priceLabel = document.createElement("label")
        priceLabel.append(document.createTextNode("price: " + currItem.price))

        cardBodyItem.appendChild(storeLabel);
        cardBodyItem.appendChild(nameLabel);
        cardBodyItem.appendChild(amountLabel);
        cardBodyItem.appendChild(priceLabel);

        cardItem.appendChild(cardBodyItem);

        HistoryPurchasesContent.appendChild(cardItem);
        HistoryDiv.appendChild(HistoryPurchasesContent)
    });
}

function addAdmin() {
    const userName = document.getElementById("userName").value;
    fetch("http://localhost:8080/tradingSystem/addAdmin", {
        method: 'POST',
        body: JSON.stringify({ userName: userName })
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(response => {
            if (response.SUCCESS) {
                Swal.fire(
                    'Congratulations!',
                    userName + " is now admin",
                    'success')
            } else {
                Swal.fire(
                    'OOPS!',
                    response,
                    'error')
            }
        })
    document.getElementById("userName").value = ""
    closeModal("addAdminModal");
}

function fixDate(date){
    return date.split("-").reverse().join("-");

}

function updateStatsTable(stats){
    var guest = 0;
    var subsc = 0;
    var manag = 0;
    var owner = 0;
    var numOfItems = document.getElementById("num-of-results");
    numOfItems.innerHTML = "";
    numOfItems.append(document.createTextNode(stats.length));
    var body = document.getElementById("results-body");
    body.innerHTML = "";
    stats.forEach(stat => {
        var tr = document.createElement("tr");

        var date_td = document.createElement("td");
        date_td.append(document.createTextNode(fixDate(stat.date)));
        tr.append(date_td);

        var guestCount_td = document.createElement("td");
        guestCount_td.append(document.createTextNode(stat.guestCount));
        tr.append(guestCount_td);
        guest += stat.guestCount;

        var subscribedCount_td = document.createElement("td");
        subscribedCount_td.append(document.createTextNode(stat.subscribedCount));
        tr.append(subscribedCount_td);
        subsc += stat.subscribedCount;

        var managerCount_td = document.createElement("td");
        managerCount_td.append(document.createTextNode(stat.managerCount));
        tr.append(managerCount_td);
        manag += stat.managerCount;

        var ownerCount_td = document.createElement("td");
        ownerCount_td.append(document.createTextNode(stat.ownerCount));
        tr.append(ownerCount_td);
        owner += stat.ownerCount;

        body.append(tr);

    })
    var tr = document.createElement("tr");
    tr.classList.add("table-dark");

    var date_td = document.createElement("td");
    date_td.append(document.createTextNode("total"));
    tr.append(date_td);

    var guestCount_td = document.createElement("td");
    guestCount_td.append(document.createTextNode(guest));
    tr.append(guestCount_td);

    var subscribedCount_td = document.createElement("td");
    subscribedCount_td.append(document.createTextNode(subsc));
    tr.append(subscribedCount_td);

    var managerCount_td = document.createElement("td");
    managerCount_td.append(document.createTextNode(manag));
    tr.append(managerCount_td);

    var ownerCount_td = document.createElement("td");
    ownerCount_td.append(document.createTextNode(owner));
    tr.append(ownerCount_td);

    body.append(tr);
}

function closeModal(modalName) {
    document.getElementById(modalName).style.display = "none";
}

function openModal(modalName) {
    document.getElementById(modalName).style.display = "block";
}

window.onclick = function (event) {
    if (event.target == document.getElementById("addAdminModal")) {
        closeModal("addAdminModal");
    } else if (event.target == document.getElementById("viewCustomerModal")) {
        closeModal("viewCustomerModal");
    } else if (event.target == document.getElementById("viewStoreModal")) {
        closeModal("viewStoreModal");
    }
}