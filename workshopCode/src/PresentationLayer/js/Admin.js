const window_name = "AdminView";
document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/tradingSystem/isLoggedIn")
    .then(response=>response.json())
    .then(updateNavBar);
})

function getCustomerHistory() {
    const customerName = document.getElementById("customerName").value;
    fetch('http://localhost:8080/tradingSystem/userPurchaseHistoryAdmin', {
        method: 'POST',
        body: JSON.stringify({ user: customerName })
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
        body: JSON.stringify({ store: storeName })
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